package com.isimo.web;

import com.isimo.core.Action;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Goto extends WebAction {
	Goto(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		WebDriverProvider.getInstance().getWebDriver().get(getDefinition().attributeValue("url"));
	}
}
