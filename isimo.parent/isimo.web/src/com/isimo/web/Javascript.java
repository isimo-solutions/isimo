package com.isimo.web;

import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;

import com.isimo.core.Action;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Javascript extends WebAction {

	public Javascript(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		try {
			WebDriverProvider.getInstance().getJavascriptExecutor().executeScript(getDefinition().attributeValue("script"));
		} catch(JavascriptException ex) {
			throw new RuntimeException(ex);
		}
	}

}
