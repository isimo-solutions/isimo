package com.isimo.web;

import java.net.URLEncoder;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Store extends WebAction {

	Store(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		webDriverProvider.waitForPageLoad();
		if(getDefinition().attributeValue("type").equals("id") || getDefinition().attributeValue("type").equals("xpath") || getDefinition().attributeValue("type").equals("css") || getDefinition().attributeValue("type").equals("model")) {
			By by = getBy();
			WebElement elem = verifyAvailable(by);
			if(getDefinition().attributeValue("attribute")!=null) {
				log("Storing attribute '"+elem.getAttribute("attribute")+"' value of the element by '"+by.toString()+"' under the variable "+getDefinition().attributeValue("variable"));
				store(elem.getAttribute(getDefinition().attributeValue("attribute")));
			} else if("true".equals(getDefinition().attributeValue("content"))) {
				log("Storing text content of the element by '"+by.toString()+"' under the variable "+getDefinition().attributeValue("variable"));
				store(elem.getText());
			}
		} else if(getDefinition().attributeValue("type").equals("count")) {
			By by = getBy();
			log("Storing count result of By '"+by.toString()+"' under the variable "+getDefinition().attributeValue("variable"));
			List<WebElement> elements = getDriver().findElements(by);
			store(String.valueOf(elements.size()));
		} else if(getDefinition().attributeValue("type").equals("text")) {
			log("Storing the text '"+getDefinition().attributeValue("text")+"' under the variable "+getDefinition().attributeValue("variable"));
			store(getDefinition().attributeValue("text"));
		} else if(getDefinition().attributeValue("type").equals("expression")) {
			log("Storing the expression result from expression'"+getDefinition().attributeValue("expression")+"' under the variable "+getDefinition().attributeValue("variable"));
			store(evaluateExpression().toString());
		}
	}
	
	void store(String value) {
		String val = value;
		if(val==null)
			val = "";
		if("true".equals(getDefinition().attributeValue("urlencode")))
			try {
				val = URLEncoder.encode(val, "UTF-8");
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		testExecutionManager.getProperties().setProperty(getDefinition().attributeValue("variable"), val);
		log("Stored "+getDefinition().attributeValue("variable")+"="+val);
	}
}
