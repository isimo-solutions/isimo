package com.isimo.core;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;

import com.isimo.core.annotations.IsimoErrorHandler;
import com.isimo.core.xml.LocationAwareElement;

@IsimoErrorHandler
public class CoreErrorHandler extends ErrorHandler {
	
	@Override
	public void handleError() throws Exception {
		if ("true".equals(testExecutionManager.properties
				.get("isimo.commandlineonerror"))) {
			LocationAwareElement root = (LocationAwareElement)testExecutionManager.getSAXReader().read(new StringReader("<commandline/>")).getRootElement();
			new CommandLine(root, null).execute();
		}
	}
}
