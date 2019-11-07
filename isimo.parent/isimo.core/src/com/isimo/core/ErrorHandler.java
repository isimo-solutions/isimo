package com.isimo.core;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class ErrorHandler {
	@Autowired
	TestExecutionManager testExecutionManager;
	
	public abstract void handleError() throws Exception;
}
