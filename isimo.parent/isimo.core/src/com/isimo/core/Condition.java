package com.isimo.core;

import org.dom4j.Element;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Condition extends Action {
	
	

	public Condition(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent, false);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void execute() {
		super.execute();
		prepareLog();
		Element previousParent = testExecutionManager.currentParent;
		if(evaluate().getLeft()) {
			testExecutionManager.currentParent = this.log;
			testExecutionManager.executeList(definition.elements(), this);
		} else {
			this.log.elements().clear();
		}
		testExecutionManager.currentParent = previousParent;
	}
	
	void prepareLog() {
		log.elements().clear();
	}
}
