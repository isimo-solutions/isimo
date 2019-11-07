package com.isimo.core;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Exec extends AtomicAction {
	Exec(LocationAwareElement definition, Action parent) {
		super(definition, parent);
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		Process process = Runtime.getRuntime().exec(getDefinition().attributeValue("executable"));
	}
}
