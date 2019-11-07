package com.isimo.web;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Input extends WebAction {

	Input(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		String suffix = ("date".equals(getDefinition().attributeValue("type")))?"__date":"";
		By by = getBy();
		log("Filling input identified by: '"+by.toString()+"' , with value '"+getDefinition().attributeValue("value")+"'");
		String transformer = getDefinition().attributeValue("transformer");
		String expected = getDefinition().attributeValue("value");
		String skipcheck = getDefinition().attributeValue("skipcheck");
		if("true".equals(skipcheck))
			expected = null;
		if(!StringUtils.isEmpty(transformer)) {
			int lastdot = transformer.lastIndexOf('.');
			String className = transformer.substring(0, lastdot);
			String methodName = transformer.substring(lastdot+1);
			Class clazz = Class.forName(className);
			Method m = clazz.getMethod(methodName, String.class);
			expected = (String) m.invoke(null, expected);
		}
		input(by,getDefinition().attributeValue("value"), expected, !"false".equals(getDefinition().attributeValue("clear")), "true".equals(getDefinition().attributeValue("tab")));		
	}

}
