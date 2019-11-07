package com.isimo.core;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Comment extends Action{
	
	Comment(LocationAwareElement definition, Action parent) {
		super(definition, parent, true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		super.execute();
		log("Execution in process for the step:  '"+ getDefinition().getText());
	}
	
}
