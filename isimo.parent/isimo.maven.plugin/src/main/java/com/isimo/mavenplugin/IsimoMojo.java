package com.isimo.mavenplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.Typedef;
import org.apache.tools.ant.types.Path;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "isimo", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class IsimoMojo extends AbstractMojo {
    public static final String MAVEN_REFID_PREFIX = "maven.";
    

    
   public static final String DEFAULT_MAVEN_PROJECT_REFID = MAVEN_REFID_PREFIX + "project";

   /**
    * The refid used to store an object of type {@link MavenAntRunProject} containing the Maven project object in the
    * Ant build. This is useful when a custom task needs to change the Maven project, because, unlike
    * {@link #DEFAULT_MAVEN_PROJECT_REFID}, this makes sure to reference the same instance of the Maven project in all
    * cases.
    */
   public static final String DEFAULT_MAVEN_PROJECT_REF_REFID = MAVEN_REFID_PREFIX + "project.ref";

   /**
    * The refid used to store the Maven project object in the Ant build.
    */
   public static final String DEFAULT_MAVEN_PROJECT_HELPER_REFID = MAVEN_REFID_PREFIX + "project.helper";
   
   /**
    * The path to The XML file containing the definition of the Maven tasks.
    */
   public static final String ANTLIB = "com/isimo/mavenplugin/ant/antlib.xml";

   /**
    * The URI which defines the built in Ant tasks
    */
   public static final String TASK_URI = "antlib:com.isimo.mavenplugin.ant.tasks";

   /**
    * String to prepend to project and dependency property names.
    *
    * @since 1.4
    */
   @Parameter( defaultValue = "" )
   private String propertyPrefix = "";

   
   /**
    * The plugin dependencies.
    */
   @Parameter( property = "plugin.artifacts", required = true, readonly = true )
   private List<Artifact> pluginArtifacts;

   @Parameter( property = "localRepository", readonly = true )
   protected ArtifactRepository localRepository;

   @Parameter( defaultValue = "false" )
   private boolean exportAntProperties;

   @Parameter( defaultValue = "" )
   private String customTaskPrefix = "";
   
   @Parameter( property = "isimo.execution.phases")
   private String phases;
   
   

   /**
    * The name of a property containing the list of all dependency versions. This is used for the removing the versions
    * from the filenames.
    */
   @Parameter( defaultValue = "maven.project.dependencies.versions" )
   private String versionsPropertyName;

    
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
		    Model model = mavenProject.getModel();
		    Build build = model.getBuild();
		    String finalName = build.getFinalName();
		    File targetDir = new File(build.getDirectory());
		    File antFilesDir = new File(build.getDirectory()+File.separator+"antfiles");
		    if(antFilesDir.exists() && !antFilesDir.isDirectory()) {
		    	throw new MojoExecutionException("Not a directory: "+antFilesDir);
		    }
		    if(!antFilesDir.exists() && !antFilesDir.mkdirs()) {
		    	throw new MojoExecutionException("Unable to create directory: "+antFilesDir);
		    }
		    
	
			Project antProject = new Project();
	        antProject.addBuildListener( getConfiguredBuildLogger() );
	        File buildXml = new File(antFilesDir.getAbsolutePath()+File.separator+"build.xml");
	        IOUtil.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("build.xml"), new FileOutputStream(buildXml));
	        getLog().info( "Project = " + mavenProject.getName());
	        getLog().info( "Add references" );
            addAntProjectReferences( mavenProject, antProject );
	        getLog().info( "Configure project" );
            ProjectHelper.configureProject( antProject, buildXml );
            antProject.init();
            antProject.setBaseDir( mavenProject.getBasedir() );

            initMavenTasks( antProject );

            // The Ant project needs actual properties vs. using expression evaluator when calling an external build
            // file.
            copyProperties( mavenProject, antProject, buildXml );

            getLog().info( "Executing tasks" );
            Vector<String> targets = new Vector<String>();
            targets.addAll(Arrays.asList(phases.split("\\,")));
            antProject.executeTargets(targets);
            getLog().info( "Executed tasks" );

            copyProperties( antProject, mavenProject );

		} catch(Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
    /**
     * Copy properties from the Ant project to the Maven project.
     *
     * @param antProject not null
     * @param mavenProject not null
     * @since 1.7
     */
    public void copyProperties( Project antProject, MavenProject mavenProject )
    {
        if ( !exportAntProperties )
        {
            return;
        }

        getLog().debug( "Propagated Ant properties to Maven properties" );
        Hashtable<String, Object> antProps = antProject.getProperties();
        Properties mavenProperties = mavenProject.getProperties();

        for ( Map.Entry<String, Object> entry : antProps.entrySet() )
        {
            String key = entry.getKey();
            if ( mavenProperties.getProperty( key ) != null )
            {
                getLog().debug( "Ant property '" + key + "=" + mavenProperties.getProperty( key )
                    + "' clashs with an existing Maven property, SKIPPING this Ant property propagation." );
                continue;
            }
            // it is safe to call toString directly since the value cannot be null in Hashtable
            mavenProperties.setProperty( key, entry.getValue().toString() );
        }
    }

	
    /**
     * Copy properties from the Maven project to the Ant project.
     *
     * @param mavenProject {@link MavenProject}
     * @param antProject {@link Project}
     */
    public void copyProperties( MavenProject mavenProject, Project antProject, File buildxml )
    {
        Properties mavenProps = mavenProject.getProperties();
        Properties userProps = session.getUserProperties();
        for ( String key : mavenProps.stringPropertyNames() )
        {
            String value = userProps.getProperty( key, mavenProps.getProperty( key ) );
            antProject.setProperty( key, value );
        }

        antProject.setProperty( "ant.file", buildxml.getAbsolutePath() );

        // Add some of the common Maven properties
        getLog().debug( "Setting properties with prefix: " + propertyPrefix );
        antProject.setProperty( ( propertyPrefix + "project.groupId" ), mavenProject.getGroupId() );
        antProject.setProperty( ( propertyPrefix + "project.artifactId" ), mavenProject.getArtifactId() );
        antProject.setProperty( ( propertyPrefix + "project.name" ), mavenProject.getName() );
        if ( mavenProject.getDescription() != null )
        {
            antProject.setProperty( ( propertyPrefix + "project.description" ), mavenProject.getDescription() );
        }
        antProject.setProperty( ( propertyPrefix + "project.version" ), mavenProject.getVersion() );
        antProject.setProperty( ( propertyPrefix + "project.packaging" ), mavenProject.getPackaging() );
        antProject.setProperty( ( propertyPrefix + "project.build.directory" ),
                                mavenProject.getBuild().getDirectory() );
        antProject.setProperty( ( propertyPrefix + "project.build.outputDirectory" ),
                                mavenProject.getBuild().getOutputDirectory() );
        antProject.setProperty( ( propertyPrefix + "project.build.testOutputDirectory" ),
                                mavenProject.getBuild().getTestOutputDirectory() );
        antProject.setProperty( ( propertyPrefix + "project.build.sourceDirectory" ),
                                mavenProject.getBuild().getSourceDirectory() );
        antProject.setProperty( ( propertyPrefix + "project.build.testSourceDirectory" ),
                                mavenProject.getBuild().getTestSourceDirectory() );
        antProject.setProperty( ( propertyPrefix + "localRepository" ), localRepository.toString() );
        antProject.setProperty( ( propertyPrefix + "settings.localRepository" ), localRepository.getBasedir() );

        // Add properties for dependency artifacts
        Set<Artifact> depArtifacts = mavenProject.getArtifacts();
        for ( Artifact artifact : depArtifacts )
        {
            String propName = artifact.getDependencyConflictId();

            antProject.setProperty( propertyPrefix + propName, artifact.getFile().getPath() );
        }

        // Add a property containing the list of versions for the mapper
        StringBuilder versionsBuffer = new StringBuilder();
        for ( Artifact artifact : depArtifacts )
        {
            versionsBuffer.append( artifact.getVersion() ).append( File.pathSeparator );
        }
        antProject.setProperty( versionsPropertyName, versionsBuffer.toString() );
    }


    /**
     * @param antProject {@link Project}
     */
    public void initMavenTasks( Project antProject )
    {
        getLog().debug( "Initialize Maven Ant Tasks" );
        Typedef typedef = new Typedef();
        typedef.setProject( antProject );
        typedef.setResource( ANTLIB );
        if ( !customTaskPrefix.isEmpty() )
        {
            typedef.setURI( TASK_URI );
        }
        typedef.execute();
    }

    private void addAntProjectReferences( MavenProject mavenProject, Project antProject )
            throws DependencyResolutionRequiredException
        {
            Path p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getCompileClasspathElements().iterator(), File.pathSeparator ) );

            /* maven.dependency.classpath it's deprecated as it's equal to maven.compile.classpath */
            antProject.addReference( MAVEN_REFID_PREFIX + "dependency.classpath", p );
            antProject.addReference( MAVEN_REFID_PREFIX + "compile.classpath", p );

            p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getRuntimeClasspathElements().iterator(), File.pathSeparator ) );
            antProject.addReference( MAVEN_REFID_PREFIX + "runtime.classpath", p );

            p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getTestClasspathElements().iterator(), File.pathSeparator ) );
            antProject.addReference( MAVEN_REFID_PREFIX + "test.classpath", p );
            getLog().info(MAVEN_REFID_PREFIX + "test.classpath=" + p+";name="+mavenProject.getName());

            /* set maven.plugin.classpath with plugin dependencies */
            antProject.addReference( MAVEN_REFID_PREFIX + "plugin.classpath",
                                     getPathFromArtifacts( pluginArtifacts, antProject ) );

            antProject.addReference( DEFAULT_MAVEN_PROJECT_REFID, mavenProject );
            antProject.addReference( DEFAULT_MAVEN_PROJECT_HELPER_REFID, projectHelper );
            antProject.addReference( MAVEN_REFID_PREFIX + "local.repository", localRepository );
        }

    /**
     * @param artifacts {@link Artifact} collection.
     * @param antProject {@link Project}
     * @return {@link Path}
     * @throws DependencyResolutionRequiredException In case of a failure.
     */
    private Path getPathFromArtifacts( Collection<Artifact> artifacts, Project antProject )
        throws DependencyResolutionRequiredException
    {
        if ( artifacts == null )
        {
            return new Path( antProject );
        }

        List<String> list = new ArrayList<>( artifacts.size() );
        for ( Artifact a : artifacts )
        {
            File file = a.getFile();
            if ( file == null )
            {
                throw new DependencyResolutionRequiredException( a );
            }
            list.add( file.getPath() );
        }

        Path p = new Path( antProject );
        p.setPath( StringUtils.join( list.iterator(), File.pathSeparator ) );

        return p;
    }

	

	

	
	/**
     * The Maven project object
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject mavenProject;

    /**
     * The Maven session object
     */
    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;


    /**
     * The Maven project helper object
     */
    @Component
    private MavenProjectHelper projectHelper;
	
    private DefaultLogger getConfiguredBuildLogger()
    {
        DefaultLogger antLogger = new MavenLogger(getLog());
        if ( getLog().isDebugEnabled() )
        {
            antLogger.setMessageOutputLevel( Project.MSG_DEBUG );
        }
        else if ( getLog().isInfoEnabled() )
        {
            antLogger.setMessageOutputLevel( Project.MSG_INFO );
        }
        else if ( getLog().isWarnEnabled() )
        {
            antLogger.setMessageOutputLevel( Project.MSG_WARN );
        }
        else if ( getLog().isErrorEnabled() )
        {
            antLogger.setMessageOutputLevel( Project.MSG_ERR );
        }
        else
        {
            antLogger.setMessageOutputLevel( Project.MSG_VERBOSE );
        }
        return antLogger;
    }
    
    public class MavenLogger
    extends DefaultLogger
{

    private final Log log;

    public MavenLogger( Log log )
    {
        this.log = log;
    }

    @Override
    protected void printMessage( final String message, final PrintStream stream, final int priority )
    {
        switch ( priority )
        {
            case Project.MSG_ERR:
                log.error( message );
                break;
            case Project.MSG_WARN:
                log.warn( message );
                break;
            case Project.MSG_INFO:
                log.info( message );
                break;
            case Project.MSG_DEBUG:
            case Project.MSG_VERBOSE:
                log.debug( message );
                break;
            default:
                log.info( message );
                break;
        }
    }

}

}
