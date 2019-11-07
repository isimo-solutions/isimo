package com.isimo.dependencies;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.isimo.core.IsimoProperties;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.xml.LocationAwareElement;
import com.isimo.core.xml.LocationSAXReader;
import com.isimo.core.xml.LocatorAwareDocumentFactory;

@SpringBootApplication
public class DependencyHolder {
	private static Logger logger = Logger.getLogger(DependencyHolder.class.getCanonicalName());
	
	@Autowired
	TestExecutionManager testExecutionManager;
	
	
	private File testDirectory;
	private LocationSAXReader reader;
	public Map<String, Scenario> path2scenarios = new HashMap<String, Scenario>();
	public DependencyHolder(String rootDir) {
		System.setProperty(testExecutionManager.getTestRootDir(), rootDir);
		reader = new LocationSAXReader();
		reader.setDocumentFactory(new LocatorAwareDocumentFactory());
		reader.setRootDir(rootDir);
	}
	public File getTestDirectory() {
		return testDirectory;
	}
	public void setTestDirectory(File testDirectory) {
		this.testDirectory = testDirectory;
	}
	
	boolean isScenario(Element elem) {
		return "scenario".equals(elem.getName());
	}
	
	public void analyzeDependency(Scenario s) {
		try {
			if(s.notFound) {
				logger.warning("Scenario file not found!");
				return;
			}
			if(!isScenario(s.xml.getRootElement())) {
				logger.log(Level.INFO, "Not a scenario file "+s.source);
			}
			List<LocationAwareElement> includes = s.xml.selectNodes("//*[name()='include']").stream().map(x -> (LocationAwareElement) x).collect(Collectors.toList());
			for(LocationAwareElement include: includes) {
				try {
					Scenario included = getScenario(include.attributeValue("scenario")+".xml");
					Dependency dep = new Dependency();
					dep.source = s;
					dep.target = included;
					dep.lineNumber = include.getLineNumber();
					s.includedScenarios.add(dep);
					included.includingScenarios.add(dep);
				} catch(DocumentException e) {
					logger.warning("Problems reading subscenario: "+e.getMessage());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void analyzeDependencies() throws DocumentException {
		analyzeDependencies("");
	}
	
	String canonicalPath(String path) {
		String p1 = path.replaceAll("\\\\+", "/");
		p1 = p1.replaceAll("\\/+", "/");
		return p1;
	}
	
	public Scenario getScenario(String relativePath) throws DocumentException {
		try {
			relativePath = canonicalPath(relativePath);
			File xml = new File(testDirectory+File.separator+relativePath);
			System.out.println("xml="+xml);
			
			try {
				xml = xml.getCanonicalFile();
			} catch(IOException ex) {
				Scenario s = path2scenarios.get(relativePath);
				if(s==null) {
					s = new Scenario();
					s.notFound = true;
					s.relativePath = relativePath;
					path2scenarios.put(relativePath, s);	
				}
				return s;
			}
			
			Scenario s = path2scenarios.get(relativePath);
			if(s==null) {
				s = new Scenario();
				s.source = xml;
				s.relativePath = relativePath;
				logger.info("Parsing "+relativePath+"...");
				s.xml = reader.read(xml);
				path2scenarios.put(relativePath, s);
			}
			return s;
		} catch(DocumentException ex) {
			throw ex;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void analyzeDependencies(String dirstr) {
		System.out.println("dirstr="+dirstr);
		File dir;
		if("".equals(dirstr))
			dir = testDirectory;
		else
			dir = new File(testDirectory.getAbsolutePath()+File.separator+dirstr);
		String[] xmls = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name!=null && name.endsWith(".xml");
			}
		});
		if(xmls!=null) {
			for(String xml: xmls) {
				try {
					analyzeDependency(getScenario(dirstr+File.separator+xml));
				} catch(DocumentException e) {
					logger.warning("Problems reading scenario file: "+e.getMessage());
				}
			}
		}
		String[] subdirs = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name!=null && (!"..".equals(name)) && (!".".equals(name)) && (new File(dir.getAbsolutePath()+File.separator+name)).isDirectory();
			}
		});
		if(subdirs!=null)
			for(String subdir: subdirs) {
				String subdirstr = ("".equals(dirstr))?subdir:dirstr+File.separator+subdir;
				analyzeDependencies(subdirstr);
			}
	}
	
	public static void main(String args[]) throws Exception {
		DependencyHolder holder = new DependencyHolder(args[0]);
		holder.testDirectory = new File(args[0]);
		holder.analyzeDependencies();
		Set<Scenario> rootScenarios = holder.getScenario(args[1]+".xml").getIncludingRootScenarios();
		Set<Dependency> including = holder.getScenario(args[1]+".xml").includingScenarios;
		System.out.println("Root scenarios:");
		for(Scenario s: rootScenarios) {
			System.out.println("  "+s.relativePath);
		}
		System.out.println("Direct includes:");
		for(Dependency d: including) {
			System.out.println("  Line: "+d.lineNumber+"  "+d.source.relativePath);
		}
	}
	
}
