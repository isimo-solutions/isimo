package com.isimo.core;

import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.annotations.IsimoPredicate;
import com.isimo.core.event.Event;
import com.isimo.core.event.ExecutionListener;
import com.isimo.core.xml.LocationAwareElement;

/**
 * 
 * @author Andrzej Dmoch
 * 
 *         The superclass for all action classes
 * 
 */

public abstract class Action {
	Element definition = null;
	LocationAwareElement definitionOrig = null;
	Element log = null;
	Properties properties = null;
	Action parent = null;
	Clock clock = null;
	Instant start,finish = null;
	float durationSeconds = -1;
	
	
	public static NumberFormat durationFormat = new DecimalFormat();
	public static Map<String, Class> availableActions = null;
	public static List<com.isimo.core.Predicate> availablePredicates = null;
	
	
	protected IsimoProperties isimoProperties;
	
	
	protected TestExecutionManager testExecutionManager;
	
	static {
		durationFormat.setMaximumFractionDigits(2);
	}

	boolean preprocessSubnodes = false;

	/**
	 * Constructor should be called by any Action, it contains the common initialization logic for all Actions
	 * 
	 * @param definition
	 *            XML Element defining the action
	 * @param parent
	 *            parent action Element or null
	 */
	public Action(LocationAwareElement definition, Action parent, boolean preprocessSubnodes) {
		isimoProperties = SpringContext.getBean(IsimoProperties.class);
		testExecutionManager = SpringContext.getBean(TestExecutionManager.class);
		this.preprocessSubnodes = preprocessSubnodes;
		this.definitionOrig = definition;
		this.parent = parent;
		properties = new Properties();
		Properties inherited = (parent==null)?testExecutionManager.properties:parent.getProperties();
		properties.putAll(inherited);
		this.definition = this.definitionOrig.createCopy();
		testExecutionManager.preprocessAction(this, this.definition);
		log = this.definition.createCopy();
	}

	/**
	 * Sets the Action's propeperty
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value) {
		System.out.println("Setting attribute " + name + ", value ='" + value + "'");
		properties.setProperty(name, value);
	}

	/**
	 * Returns the value of the Action's property
	 * 
	 * @param name
	 * @return property value
	 */
	public String getProperty(String name) {
		String returnValue;
		if (parent != null)
			returnValue = parent.getProperty(name);
		else
			returnValue = testExecutionManager.properties.getProperty(name);
		String thisValue = (String) properties.get(name);
		if (thisValue != null)
			returnValue = thisValue;
		if (returnValue == null)
			returnValue = "";
		System.out.println("getProperty name '" + name + "' returning '" + returnValue + "' from '" + this.definition.toString());
		return returnValue;
	}

	public String getGlobal(String name) {
		return testExecutionManager.properties.getProperty(name);
	}
	
	public void setGlobal(String name, String value) {
		testExecutionManager.properties.setProperty(name, value);
	}

	static Random random = new Random();

	/**
	 * Common preparation logic for all Action executions, should be called as a first step in the Action's execute method
	 */
	void execute() {
		
	}
	
	void controlledExecute() {
		testExecutionManager.executionController.setCurrentAction(this);
		testExecutionManager.executionController.startAction();
		try {
			testExecutionManager.log("ActionXML: " + definition.asXML(), this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		execute();
		testExecutionManager.executionController.setCurrentAction(this);
		testExecutionManager.executionController.stopAction();
	}

	void preprocessAction(Document doc) {
		testExecutionManager.preprocessAction(this, doc.getRootElement());
	}



	
	
	public static Map<String, Class> getAvailableActions() {
		if(availableActions==null) {
			availableActions = new HashMap<String, Class>();
			ClassPathScanningCandidateComponentProvider scanner =
					new ClassPathScanningCandidateComponentProvider(false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(IsimoAction.class));
			Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.isimo");
			for(BeanDefinition bd: beanDefinitions) {
				String className = bd.getBeanClassName();
				Class clazz = null;
				try {
					clazz = Class.forName(className);
				} catch(Throwable e) {
					throw new RuntimeException(e);
				}
				String lowercasename = clazz.getSimpleName().toLowerCase();
				availableActions.put(lowercasename, clazz);
			}
		}
		return availableActions;
	}
	
	public static List<com.isimo.core.Predicate> getAvailablePredicates() {
		if(availablePredicates==null) {
			availablePredicates = new ArrayList<com.isimo.core.Predicate>();
			ClassPathScanningCandidateComponentProvider scanner =
					new ClassPathScanningCandidateComponentProvider(false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(IsimoPredicate.class));
			Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.isimo");
			for(BeanDefinition bd: beanDefinitions) {
				String className = bd.getBeanClassName();
				com.isimo.core.Predicate predicate = null;
				try {
					predicate = (com.isimo.core.Predicate) Class.forName(className).newInstance();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				availablePredicates.add(predicate);
			}
		}
		return availablePredicates;
	}


	/**
	 * Static method acting as a constructor. Based on the element's name instantiates the proper Action's subclass
	 * 
	 * @param elem
	 *            XML Element defining the action
	 * @param parent
	 *            parent Action or null
	 * @return Instantiated Action object
	 */
	public static Action getAction(LocationAwareElement elem, Action parent) {
		String name = elem.getName();
		if (name.equals("action")) {
			try {
				Class clazz = Class.forName(elem.attributeValue("classname"));
				Constructor<Action> c = clazz.getConstructor(LocationAwareElement.class, Action.class);
				return c.newInstance(elem, parent);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			for(Map.Entry<String, Class> availableAction: getAvailableActions().entrySet()) {
				if(name.equals(availableAction.getKey())) {
					Constructor<Action> c = availableAction.getValue().getDeclaredConstructor(LocationAwareElement.class, Action.class);
					c.setAccessible(true);
					return c.newInstance(elem, parent);
				}
			};
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		throw new RuntimeException("Action " + name + " not found in classpath");
		
/*		if (name.equals("closebrowser")) {
			return new Closebrowser(elem, parent);
		} else if (name.equals("click")) {
			return new Click(elem, parent);
		} else if (name.equals("select")) {
			return new Select(elem, parent);
		} else if (name.equals("input")) {
			return new Input(elem, parent);
		} else if (name.equals("include")) {
			return new Include(elem, parent);
		} else if (name.equals("store")) {
			return new Store(elem, parent);
		} else if (name.equals("maximize")) {
			return new Maximize(elem, parent);
		} else if (name.equals("sleep")) {
			return new Sleep(elem, parent);
		} else if (name.equals("waitfor")) {
			return new WaitFor(elem, parent);
		} else if (name.equals("get")) {
			return new Get(elem, parent);
		} else if (name.equals("assert")) {
			return new Assert(elem, parent);
		} else if (name.equals("asserttext")) {
			return new AssertText(elem, parent);
		} else if (name.equals("commandline")) {
			return new CommandLine(elem, parent);
		} else if (name.equals("comment")) {
			return new Comment(elem, parent);
		} else if (name.equals("draganddrop")) {
			return new DragAndDrop(elem, parent);
		} else if (name.equals("handlealert")) {
			return new HandleAlert(elem, parent);
		} else if (name.equals("open")) {
			return new Open(elem, parent);
		} else if (name.equals("windows")) {
			return new Windows(elem, parent);
		} else if (name.equals("condition")) {
			return new Condition(elem, parent);
		} else if (name.equals("if")) {
			return new If(elem, parent);
		} else if (name.equals("while")) {
			return new While(elem, parent);
		} else if (name.equals("checkstyle")) {
			return new CheckStyle(elem, parent);
		} else if (name.equals("robot")) {
			return new Robot(elem, parent);
		} else if (name.equals("sql")) {
			return new Sql(elem, parent);
		} else if (name.equals("save")) {
			return new Save(elem, parent);
		} else if (name.equals("interactions")) {
			return new Interactions(elem, parent);
		} else if (name.equals("exec")) {
			return new Exec(elem, parent);
		} else if (name.equals("goto")) {
			return new Goto(elem, parent);
		} else if (name.equals("action")) {
			try {
				Class clazz = Class.forName(elem.attributeValue("classname"));
				Constructor<Action> c = clazz.getConstructor(LocationAwareElement.class, Action.class);
				return c.newInstance(elem, parent);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Action " + name + " not implemented yet");
		}*/
	}



	/**
	 * Sleeps a number of milliseconds
	 * 
	 * @param millis
	 *            - number of milliseconds to sleep
	 */
	public static void sleepMili(float millis) {
		try {
			Thread.currentThread().sleep((int) millis);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Chooses randomly the String from the list of strings submitted as a parameter
	 * 
	 * @param values
	 *            - list of Strings
	 * @return randomly chosen string
	 */
	public String randomText(String... values) {
		return values[random.nextInt(values.length)];
	}


	public Properties getProperties() {
		return properties;
	}


	static String randomNumberGeneration(String randomDigit) {
		String randomVal = "", val = "9";
		int loopRValue = Integer.parseInt(randomDigit);
		for (int i = 1; i <= loopRValue; i++) {

			randomVal = val + randomVal;

		}

		return randomVal;

	}

	static String randomStringGeneration(String randomDigit) {
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.,-'[]_()/=?;{}&";
		Random rnd = new Random();

		int loopRValue = Integer.parseInt(randomDigit);
		StringBuilder sb = new StringBuilder(loopRValue);
		for (int i = 0; i < loopRValue; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}


	
	
	

	protected Object evaluateExpression() {
		return testExecutionManager.evaluateExpression(this, null);
	}
	
	protected Object evaluateExpression(Properties props) {
		return testExecutionManager.evaluateExpression(this, props);
	}
	
	protected Pair<Boolean, Object> evaluate() {
		Pair<Boolean, Object> lastValidResult = Pair.of(true, null);
		for(com.isimo.core.Predicate p: getAvailablePredicates()) {
			Pair<Boolean, Object> result = p.evaluate(this);
			if(!result.getLeft())
				return result;
			if(result.getRight() != null)
				lastValidResult = result;
		}
		return lastValidResult;
	}



	public void log(String message) {
		testExecutionManager.log(message, this);
	}
	
	public void log(String message, Action action) {
		testExecutionManager.log(message, action);
	}


	

	
	

	public Element getDefinition() {
		return definition;
	}

	public LocationAwareElement getDefinitionOrig() {
		return definitionOrig;
	}

	public void setDefinition(Element pDefinition) {
		definition = pDefinition;
	}

	public Action getParent() {
		return parent;
	}

	public void setParent(Action pParent) {
		parent = pParent;
	}

	public void setProperties(Properties pProperties) {
		properties = pProperties;
	}
	
	
	public Element getLog() {
		return log;
	}
	
	public Element logProblem(String message) {
		return testExecutionManager.logProblem(message, this);
	}
	
	
	@Override
	public String toString() {
		return super.toString() + " " + this.definition.asXML();
	}


	
	@Override
	public boolean equals(Object pObj) {
		if(!(pObj instanceof Action))
			return false;
		Action compared = (Action) pObj;
		return compared.definitionOrig.getSystemid().equals(definitionOrig.getSystemid()) && compared.definitionOrig.getLineNumber() == definitionOrig.getLineNumber() && compared.definition.getName().equals(definitionOrig.getName());
	}

	public void initTimer() {
		start = Instant.now();
	}
	
	public float calcDuration() {
		finish = Instant.now();
		long duration = finish.toEpochMilli() - start.toEpochMilli();
		durationSeconds = (float) ((double) duration / (double) 1000);
		this.log.addAttribute("duration", durationFormat.format(durationSeconds));
		this.log.addAttribute("start", start.toString());
		return durationSeconds;
	}

	public float getDurationSeconds() {
		return durationSeconds;
	}
	
	
}
