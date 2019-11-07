package com.isimo.core;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class Initializer {
	@Autowired
	TestExecutionManager testExecutionManager;
	
	public abstract void init();
}
