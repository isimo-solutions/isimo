package com.isimo.core;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Sleep extends CompoundAction {

	Sleep(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	void execute() {
		super.execute();
		try {
			Thread.sleep(1000*Integer.parseInt(definition.attributeValue("seconds")));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
