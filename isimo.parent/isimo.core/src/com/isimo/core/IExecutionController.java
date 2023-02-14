package com.isimo.core;

import org.dom4j.Element;

import com.isimo.core.event.Event;

public interface IExecutionController {

	void terminate();
	
	void suspend(Element elem) throws InterruptedException;

	void problemOccurred(Action currentAction, Action containerAction, Exception e);
	
	void finalize();

	void startAction();

	Action getCurrentAction();

	void setCurrentAction(Action currentAction);

	void stopAction();
	
	void startScenario(String scenarioName);
	
	void stopScenario(String scenarioName);
	
	void startTestCase(String testCaseName);
	
	void stopTestCase(String testCaseName);	
	
	void comment(String comment);
}