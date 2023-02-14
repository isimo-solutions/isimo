package com.isimo.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import com.isimo.core.Action;
import com.isimo.core.IsimoProperties;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;
import com.isimo.core.xml.LocationAwareElement;

@Component
public class DefaultExecutionController implements IExecutionController {
	private Action currentAction = null;
	
	@Autowired
	IsimoProperties isimoProperties;
	
	@Autowired
	List<ExecutionListener<? extends Event>> executionListeners;
	
	
	@Autowired 
	protected TestExecutionManager testExecutionManager;

	
	@Override
	public void terminate() {
		
	}
	
	
	@Override
	public void suspend(Element elem) throws InterruptedException {
		
	}
		
	@Override
	public void finalize() {
		
	}
	
	@Override
	public void startAction() {
		publishEvent(Event.startAction(getCurrentAction()));
	}
	
	@Override
	public void stopAction() {
		publishEvent(Event.stopAction(getCurrentAction()));
	}


	@Override
	public Action getCurrentAction() {
		return currentAction;
	}


	@Override
	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}


	


	
	
	public void publishEvent(Event event) {
		synchronized(this) {
			for(ExecutionListener l: executionListeners) {
				l.handleEvent(event);
			}
		}
	}


	@Override
	public void problemOccurred(Action currentAction, Action containerAction, Exception e) {
		// TODO Auto-generated method stub
		if (containerAction != null && "true".equals(containerAction.getDefinition().attributeValue("ignoreerrors"))) {
			currentAction.log.addAttribute("ignorederror", e.getMessage());
			testExecutionManager.log("Exception: " + e.getMessage() + " has been ignored", containerAction);
		} else {
			publishEvent(Event.error(currentAction));
			Element actionWithProblemInfo = testExecutionManager.logProblem(e.getMessage(), currentAction);
			try {
				suspend(actionWithProblemInfo);
			} catch(InterruptedException intex) {
				throw new RuntimeException(intex);
			}
			throw new AlreadyLoggedException(e);
		}
		
	}


	@Override
	public void startScenario(String scenarioName) {
		// TODO Auto-generated method stub
		publishEvent(Event.startScenario(scenarioName));
	}


	@Override
	public void stopScenario(String scenarioName) {
		publishEvent(Event.stopScenario(scenarioName));
	}


	@Override
	public void startTestCase(String testcaseName) {
		publishEvent(Event.startTestCase(testcaseName));
	}
	
	@Override
	public void stopTestCase(String testcaseName) {
		publishEvent(Event.stopTestCase(testcaseName));
	}


	@Override
	public void comment(String comment) {
		publishEvent(Event.comment(comment));
	}
}
