package com.isimo.core;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class While extends CompoundAction {

	public While(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	void execute() {
		super.execute();
		int maxiterations = 300;
		String maxiterationsStr = getDefinition().attributeValue("maxiterations");
		if(maxiterationsStr != null)
			maxiterations = Integer.parseInt(maxiterationsStr);
		int i = 0;
		while(evaluate().getLeft()) {
			testExecutionManager.executeList(definition.elements(), this);
			super.execute();
			if(i++ > maxiterations)
				throw new RuntimeException("Maxiterations exeeded "+i);
		}
	}
}
