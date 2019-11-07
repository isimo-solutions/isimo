package com.isimo.web;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Closebrowser extends WebAction {

	public Closebrowser(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		log("Closing browser");
		getDriver().quit();
	}

}
