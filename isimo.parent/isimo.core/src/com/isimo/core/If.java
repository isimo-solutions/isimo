package com.isimo.core;

import org.dom4j.Element;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class If extends CompoundAction {

	public If(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	void execute() {
		super.execute();
		Element toexecute = null;
		Element previousparent = null;
		Element changedparent = null;
		prepareLog();
		if(evaluate().getLeft()) {
			toexecute = definition.element("then");
			changedparent = log.element("then");
		} else {
			
			if(definition.element("else")!=null) {
				toexecute = definition.element("else");
			}
			changedparent = log.element("else");
		}
		previousparent = testExecutionManager.currentParent; 
		testExecutionManager.currentParent = changedparent;
		if(toexecute!=null)
			testExecutionManager.executeList(toexecute.elements(), this);
		testExecutionManager.currentParent = previousparent;
	}
	
	void prepareLog() {
		log.element("then").elements().clear();
		if(log.element("else") != null)
			log.element("else").elements().clear();
	}
}
