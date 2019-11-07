package com.isimo.web;

import java.io.StringReader;

import com.isimo.core.Action;
import com.isimo.web.Closebrowser;
import com.isimo.core.ErrorHandler;
import com.isimo.core.IsimoProperties;
import com.isimo.core.SpringContext;
import com.isimo.web.Screenshot;
import com.isimo.core.TestCases;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.annotations.IsimoErrorHandler;
import com.isimo.core.xml.LocationAwareElement;

@IsimoErrorHandler
public class WebErrorHandler extends ErrorHandler {
	TestExecutionManager testExecutionManager;
	IsimoWebProperties isimoWebProperties;
	IsimoProperties isimoProperties;
	
	public WebErrorHandler() {
		this.testExecutionManager = SpringContext.getTestExecutionManager();
		this.isimoWebProperties = SpringContext.getBean(IsimoWebProperties.class);
		this.isimoProperties = SpringContext.getBean(IsimoProperties.class);
	}
	
	@Override
	public void handleError() throws Exception {
		if (isimoWebProperties.isimo.takescreenshotonerror) {
			LocationAwareElement root = (LocationAwareElement)testExecutionManager.getSAXReader().read(new StringReader("<screenshot file=\"errorScreenshot.png\"/>")).getRootElement();
			try {
				testExecutionManager.runAtomicAction(null, new Screenshot(root, null), isimoProperties.isimo.actiontimeout);
			} catch(Exception ee) {
				testExecutionManager.log("Failed taking screenshot, exception: "+ee.getMessage(), null);
			}
		}
		if (isimoWebProperties.isimo.closebrowseronerror) {
			if (WebDriverProvider.getInstance().getWebDriver() != null) {
				try {
					LocationAwareElement root = (LocationAwareElement)testExecutionManager.getSAXReader().read(new StringReader("<closebrowser/>")).getRootElement();
					testExecutionManager.runAtomicAction(null, new Closebrowser(root, null), isimoProperties.isimo.actiontimeout);
				} catch(Exception ee) {
					testExecutionManager.log("Failed closing browser, exception: "+ee.getMessage(), null);
				}
			}
		}
	}
}
