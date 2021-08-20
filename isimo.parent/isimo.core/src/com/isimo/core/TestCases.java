package com.isimo.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.liquibase.SpringPackageScanClassResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.isimo.core.annotations.IsimoErrorHandler;
import com.isimo.core.annotations.IsimoInitializer;
import com.isimo.core.annotations.IsimoPredicate;
import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;
import com.isimo.core.xml.LocationAwareElement;
import com.isimo.core.xml.LocationSAXReader;
import com.isimo.core.xml.LocatorAwareDocumentFactory;


import junit.framework.TestCase;

@SpringBootTest
//@RunWith(value = Parameterized.class)
public class TestCases {
	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();
	
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();
	
	boolean failure = false;
	SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
	LocationSAXReader saxreader = null;
	String scenarioName;

	
	@Autowired
	TestExecutionManager testExecutionManager;

	@Autowired
	IExecutionController executionController;	
	
	
	
	@Autowired
	IsimoProperties isimoProperties;
	
	@Autowired
	ApplicationContext appCtx;
	
	
	public List<Initializer> initializers = null;
	public List<ErrorHandler> errorHandlers = null;
	
	@PostConstruct
	public void init() {
		errorHandlers = appCtx.getBeansOfType(ErrorHandler.class).values().stream().map(x -> (ErrorHandler)x).collect(Collectors.toList());
		initializers = appCtx.getBeansOfType(Initializer.class).values().stream().map(x -> (Initializer)x).collect(Collectors.toList());

	}
	
	

	public TestCases() throws Exception {
		System.out.println("Starting test...");
		this.scenarioName = System.getProperty("scenario");
	}
	

	public String getScenarioName() {
		return testExecutionManager.scenarioName;
	}
	
	public List<ErrorHandler> getErrorHandlers() {
		return errorHandlers;
	}
	
	public List<Initializer> getInitializers() {
		return initializers;
	}
	
	static <T> Class getAnnotationForAbstractClass(Class abstractClass) {
		if("com.isimo.core.Initializer".equals(abstractClass.getName()))
			return IsimoInitializer.class;
		else if("com.isimo.core.Finalizer".equals(abstractClass.getName()))
			return IsimoErrorHandler.class;
		throw new RuntimeException("No Annotation class for "+abstractClass);
	}
	
	public static <T> List<T> getAvailableClasses(Class clazz, String rootPackage) {
		List<T> list = new ArrayList<T>();
		ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(getAnnotationForAbstractClass(clazz)));
		Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(rootPackage);
		for(BeanDefinition bd: beanDefinitions) {
			String className = bd.getBeanClassName();
			T cl = null;
			try {
				cl = (T) Class.forName(className).newInstance();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			list.add(cl);
		}
		return list;
	}
	
	




	@Before
	public void setupTest() throws Exception {
		testExecutionManager.scenarioName = scenarioName;
		testExecutionManager.properties.putAll(System.getProperties());
		System.out
				.println("user.dir=" + System.getProperties().get("user.dir"));
		testExecutionManager.properties.load(new FileInputStream(System.getProperties().get(
				"user.dir")
				+ File.separator + "test.properties"));
		System.out.println("Properties loaded from file "
				+ System.getProperties().get("user.dir") + File.separator
				+ "test.properties");
		for(Initializer intializer: getInitializers()) {
			intializer.init();
		}
	}
	
	@After
	public void finalize() {
		
	}
	
	
	
	public int getIntegerProperty(String name, int defaultValue) {
		try {
			return Integer.parseInt((String) testExecutionManager.properties.get(name));
		} catch(Exception e) {
			return defaultValue;
		}
	}

	void assertEqualsContinue(String expected, String is) {
		try {
			TestCase.assertEquals(expected, is);
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			failure = true;
		}
	}

	void assertEqualsContinue(int expected, int is) {
		try {
			TestCase.assertEquals(expected, is);
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			failure = true;
		}
	}

	@Parameters
	public static Collection<Object[]> generateParams() {
		String scenarioName = System.getProperty("scenario");
		List<Object[]> params = new ArrayList<Object[]>();
		StringTokenizer st = new StringTokenizer(scenarioName);
		while (st.hasMoreTokens()) {
			params.add(new String[] { st.nextToken() });
		}
		return params;
	}
	
	String getStacktrace(Throwable e) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);
			e.printStackTrace(pw);
			bos.flush();
			pw.flush();
			return bos.toString();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	@Test
	public void testScenario() throws Exception {
		Exception possibleException = null;
		try {
			testExecutionManager.executionController.startTestCase(getScenarioName());
			String mainScenarioFile = isimoProperties.com.isimo.scenarios;
			mainScenarioFile += File.separator + getScenarioName() + ".xml";
			String mainScenarioDir = (new File(mainScenarioFile)).getParent();
			mainScenarioDir = mainScenarioDir.replaceAll("\\\\", "/");

			testExecutionManager.properties.setProperty("root.scenario.dir", mainScenarioDir);
			testExecutionManager.executeScenario(getScenarioName(), null, new HashMap<String, Attribute>());
			testExecutionManager.testExecution = TestExecutionManager.TestExecution.FINISHED;
			testExecutionManager.executionController.finalize();
			if(testExecutionManager.testStatus==TestExecutionManager.TestStatus.ERROR) {
				throw new FinishedWithUnexpectedError("despite test completion some of the assertions in the scenario caused an error, please see the details");
			}
			if(testExecutionManager.testStatus==TestExecutionManager.TestStatus.FAILED)
				Assert.fail("despite test completion some of the assertions in the scenario failed, please see the details");
		} catch (Exception e) {
			testExecutionManager.terminated(e, "Exception occured: "+getStacktrace(e),executionController.getCurrentAction());
			possibleException = e;
			for(ErrorHandler errorHandler: getErrorHandlers()) {
				errorHandler.handleError();
			}
			if(testExecutionManager.testStatus==TestExecutionManager.TestStatus.FAILED)
				org.junit.Assert.fail("test execution interrupted with status failure ");
			if (possibleException != null)
				throw new RuntimeException("test execution interrupted with status error: "+getStacktrace(possibleException),possibleException);
		} finally {
			testExecutionManager.executionController.stopTestCase(getScenarioName());
		}
	}
}
