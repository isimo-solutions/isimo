package com.isimo.core;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;
import com.isimo.core.xml.LocationAwareElement;
import com.isimo.core.xml.LocationSAXReader;
import com.isimo.core.xml.LocatorAwareDocumentFactory;

@Component
public class TestExecutionManager {
	public enum TestStatus {
		SUCCESS, FAILED, ERROR
	}
	
	public enum TestExecution {
		ABORTED, FINISHED
	}
	
	public TestStatus testStatus = TestStatus.SUCCESS;
	public TestExecution testExecution = TestExecution.ABORTED;
	
	
	Properties properties = new Properties();
	
	@Autowired
	IsimoProperties isimoProperties;
	
	@Autowired
	IExecutionController executionController;
	
	@Autowired
	ExpressionEvaluator expressionEvaluator;

	Element currentParent = null;

	LocationSAXReader saxreader;
	
	String scenarioName;
	
	Object evaluateExpression(Action a, Properties props) {
		JexlExpression expr = expressionEvaluator.jexlEngine.createExpression(a.definition.attributeValue("expression"));
		JexlContext ctx = new MapContext();
		if(props!=null)
			for(Object key: props.keySet()) {
				ctx.set((String)key, props.get(key));
			}
		ctx.set("definition", a.definition);
		ctx.set("action", a);
		Object o = expr.evaluate(ctx);
		return o;
	}
	
	Object evaluateExpression(Action a) {
		return evaluateExpression(a, null);
	}
	
	public String isFailure(Element elem) {
		if(elem==null)
			return null;
		String issue = elem.attributeValue("issue");
		return issue;
	}
	
	public String isFailure(Element elem, Action action) {
		if(elem!=null) {
			String failure = isFailure(elem);
			if(!StringUtils.isEmpty(failure))
				return failure;
		}
		if(action==null)
			return null;
		String issue = isFailure(action.getDefinition());
		if(!StringUtils.isEmpty(issue))
			return issue;
		else
			return isFailure(null, action.getParent());
	}

	
	public Element logProblem(String message, Element elem, Action action) {
		String issuetext = "";
		String issue = isFailure(elem, action);
		boolean failure = (issue!=null);
		if (issue!=null) {
			issuetext = "ISSUE-NUMBER-START(" + issue + ")ISSUE-NUMBER-END";
			updateStatusFailure();
			elem = action.getLog().addElement("failure");
		} else {
			updateStatusError();
			elem = action.getLog().addElement("error");
		}
		log("\nERROR-START(\nACTION-NAME(" + action.toString() + ")ACTION-NAME\n" + message + issuetext + ")ERROR-END\n", action);
		if(message==null)
			message = "null";
		elem.setText(message);
		return elem;
	}

	
	public void updateStatusFailure() {
		if(testStatus!=TestStatus.ERROR)
			testStatus = TestStatus.FAILED;
	}
	
	public void updateStatusError() {
		testStatus = TestStatus.ERROR;

	}


	
	public Element logProblem(String message, Action action) {
		Element issueElement = action.getDefinition();
		Element elem = null;
		return logProblem(message, issueElement, action);
	}
	
	void logTerminated(Exception e, String message, Action action, Action origAction) {
		if(action==null) {
			updateStatusError();
			return;
		}
		if (StringUtils.isNotEmpty(action.getDefinition().attributeValue("issue"))) {
			updateStatusFailure();
			if(!(e instanceof AlreadyLoggedException)) {
				logProblem(message, action);
			}
			return;
		}
		logTerminated(e, message, action.getParent(), origAction);
	}
	
	public void terminated(Exception e, String stacktraceMessage, Action action) {
		log(stacktraceMessage, action);
		logTerminated(e, e.getMessage(), action, action);
	}


	public void log(String message, Action action) {
		String classifier = "[ROOT]";
		if (action != null) {
			classifier = "[" + action.getClass().getName() + "] ";
		}
		System.out.println(classifier + message);
		System.out.flush();
	}
	
	String preprocessAction(Action a, String str) {
		if (str == null)
			return null;
		Pattern pattern = Pattern.compile("\\{(\\w+)\\((.*?)\\)\\}");
		Matcher matcher = pattern.matcher(str);
		StringBuffer result = new StringBuffer();
		log("STRING="+str, a);
		while (matcher.find()) {
			matcher.appendReplacement(result, evalExpression(a, matcher.group(1), matcher.group(2)));
		}
		matcher.appendTail(result);
		return result.toString();
	}
	
	String evalExpression(Action action, String fun, String params) {
		System.out.println("Evaluating expression " + fun + ", " + params);
		NumberFormat nf = new DecimalFormat("00000");
		Calendar c = new GregorianCalendar();
		Date d = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String retval = "";
		if ("property".equals(fun)) {
			retval = action.getProperty(params);
		}
		if ("global".equals(fun)) {
			retval = action.getGlobal(params);
		} else if ("randominteger".equals(fun)) {
			if(!StringUtils.isEmpty(params))
				retval = String.valueOf(nf.format(Action.random.nextInt(Integer.parseInt(params))));
			retval = String.valueOf(nf.format(Action.random.nextInt(99999)));
			// retval = String.valueOf(Action.random.nextInt(99999)+10000);
		} else if ("sysdate".equals(fun)) {
			StringTokenizer st = new StringTokenizer(params,",");
			c.add(Calendar.DATE, Integer.parseInt(st.nextToken()));
			DateFormat f = dateFormat;
			if(st.hasMoreTokens()) {
				f = new SimpleDateFormat(st.nextToken());
			}
			retval = String.valueOf(f.format(c.getTime()));
		} else if ("systime".equals(fun)) {
			StringTokenizer st = new StringTokenizer(params,",");
			c.add(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
			DateFormat f = dateFormat;
			if(st.hasMoreTokens()) {
				f = new SimpleDateFormat(st.nextToken());
			}
			retval = String.valueOf(f.format(c.getTime()));
		} else if ("randomdigits".equals(fun)) {
			String randomGenValue = Action.randomNumberGeneration(params);
			retval = String.valueOf(Action.random.nextInt(Integer.parseInt(randomGenValue)));
		} else if ("randomstring".equals(fun)) {
			String randomStringGenValue = Action.randomStringGeneration(params);
			retval = String.valueOf(randomStringGenValue);
		} else if("urlencode".equals(fun)) {
			retval = URLEncoder.encode(params);
		}
		System.out.println("Evaluated expression " + fun + ", " + params + " to '" + retval + "'");
		return retval;
	}
	
	
	void preprocessAction(Action action, Node element) {
		try {
			element.setText(preprocessAction(action, element.getText()));
		} catch(UnsupportedOperationException e) {
			// ignore
		}
		if (element instanceof Element) {
			System.out.println("Preprocessing element " + element.getName());
			List<Attribute> attributes = (List<Attribute>) ((Element) element).attributes();
			for (Attribute a : attributes) {
				a.setValue(preprocessAction(action, a.getValue()));
			}
		}
		if (action.preprocessSubnodes) {
			List<Node> elements = element.selectNodes("./node()");
			for (Node n : elements) {
				preprocessAction(action, n);
			}
		}
	}
	
	void executeList(List<Element> elements, Action container) {
		for (Element actionElem : elements) {
			Action currentAction = Action.getAction((LocationAwareElement)actionElem, container);
			try {
				currentAction.controlledExecute();
			} catch (AlreadyLoggedException e) {
				throw e;
			} catch (Exception e) {
				executionController.problemOccurred(currentAction, container, e);	
			}
		}
	}
	
	/**
	 * Sleeps a number of seconds
	 * 
	 * @param seconds
	 *            - number of seconds to sleep
	 */
	public void sleep(float seconds) {
		try {
			Thread.currentThread().sleep((int) seconds * 1000);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getTestRootDir() {
		return isimoProperties.com.isimo.scenarios;
	}
	
	
	public LocationSAXReader newSAXReader() {
		LocationSAXReader retval = new LocationSAXReader();
		retval.setDocumentFactory(new LocatorAwareDocumentFactory());
		retval.setRootDir(getTestRootDir());
		return retval;
	}
	
	public LocationSAXReader getSAXReader() {
		try {
			if(saxreader == null) {
				saxreader = newSAXReader();
			}
			return saxreader;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	public File getReportDir(String scenarioName) {
		String reportDir = scenarioName.replaceAll("/", "_");
		File reportDirFile = new File(reportDir);
		
		reportDirFile.mkdirs();
		return reportDirFile;
	}
	
	public File getReportDir() {
		return getReportDir(scenarioName);
	}

	public void runAtomicAction(Action parent, final AtomicAction currentAction, int timeoutInSeconds) throws Exception {
		/* hard constraint on the duration of a given action (Action.ACTIONTIMEOUT) */
		Thread actionThread = new Thread() {
		   @Override 
		   public void run() {
			   try {
				   currentAction.executeAtomic(); // the actual execution takes place in the dedicated, time-constrained thread
			   } catch(InterruptedException e) {
				   log("Action thread interrupted", currentAction);
				   throw new RuntimeException(e);
			   } catch (Exception e) {
				   throw new RuntimeException(e);
			   }
		   }
		   
		   @Override
			public String toString() {
				return super.toString()+" "+currentAction.getDefinition().asXML();
			}
		};
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future future = executor.submit(actionThread);
		try {
			
			future.get(timeoutInSeconds, TimeUnit.SECONDS);
			actionThread.join();
		} catch(TimeoutException e) {
			future.cancel(true);
			throw e;
		} catch(InterruptedException e) {
			log("Interrupted", currentAction);
		} finally {
			executor.shutdownNow();
		}
	}
	
	Document parseScenario(String scenarioName) {
		String scenarioRoot = getTestRootDir();
		
		if(scenarioRoot==null)
			scenarioRoot = System.getProperty("user.dir");
		String MODEL_VALIDATION_URI = "http://isimo.com/model/validation";
		System.setProperty("javax.xml.validation.SchemaFactory:"+MODEL_VALIDATION_URI, "com.isimo.core.model.ModelSchemaFactory");
		
		System.out.println("Scenario Root Directory is "+scenarioRoot);
		File scenarioFile = new File(scenarioRoot+File.separator+scenarioName+".xml");
		org.w3c.dom.Document domDocument = null;
/*		Validator validator = null;
		Validator modelValidator = null;
		ValidatorHandler vh = new ValidatorHandler();*/
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/validation/dynamic", true);
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			domDocument = db.parse(scenarioFile);
			
/*			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SchemaFactory modelSchemaFactory = SchemaFactory.newInstance(MODEL_VALIDATION_URI);

			Schema schemaXSD = schemaFactory.newSchema(new StreamSource(ClassLoader.getSystemResourceAsStream("xsd/scenario.xsd")));
			Schema modelSchema = modelSchemaFactory.newSchema();*/
			/*validator = schemaXSD.newValidator();
			modelValidator = modelSchema.newValidator();
			validator.setErrorHandler(vh);
			modelValidator.setErrorHandler(vh);*/
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		try {
			DOMSource domSource = new DOMSource(domDocument); 
			/*validator.validate(domSource);
			modelValidator.validate(domSource);*/
		} catch(Exception e) {
			log("Warning! scenario is not valid: "+e.getMessage(), null);
		}
		SAXReader reader = getSAXReader();
		Document doc = null;
		try {
			doc = reader.read(scenarioFile);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return doc;
	}

	
	
	public void executeScenario(String scenarioName, Action parent, Map<String, Attribute> attrsXml) throws Exception {
		executionController.startScenario(scenarioName);
		Document doc = parseScenario(scenarioName);
		if(parent==null) {
			currentParent = doc.getRootElement().createCopy();
			currentParent.addAttribute("scenario", getScenarioName());
		}
		Document tags = DocumentHelper.createDocument();
		Element tagsElem = tags.addElement("tags");
		/* tags */
		if(parent == null) {
			List<Node> tagslist = doc.getRootElement().selectNodes("*[name()='tag']");
			for(Node tag: tagslist) {
				Element tagClone = (Element) tag.clone();
				tagClone.setQName(new QName(tagClone.getName()));
				tagsElem.add(tagClone);
			}
		}
		Properties currentattrs = properties;
		if(parent!=null)
			currentattrs = parent.getProperties();
		/* attribute defaults */
		List<Node> attrslist = doc.getRootElement().selectNodes("*[name()='attr']");
		for(Node attr: attrslist) {
			Element attrElem = (Element) attr;
			String name = "attr:"+attrElem.attributeValue("name");
			if(!attrsXml.containsKey(name))
				currentattrs.put(name, attrElem.attributeValue("default"));
		}
		if(tagsElem.elements().size() != 0)
			FileUtils.write(new File(getReportDir(scenarioName)+File.separator+"tags_include.xml"), tagsElem.asXML(), "UTF-8");
		List<Element> actions = doc.getRootElement().element("actions").elements();
		executeList(actions, parent);
		executionController.stopScenario(scenarioName);		
	}

	public IExecutionController getExecutionController() {
		return executionController;
	}

	public void setExecutionController(IExecutionController executionController) {
		this.executionController = executionController;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}






}
