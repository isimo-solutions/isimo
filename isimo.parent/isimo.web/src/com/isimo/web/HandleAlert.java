package com.isimo.web;

import org.openqa.selenium.Alert;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class HandleAlert extends WebAction {
	HandleAlert(LocationAwareElement definition, Action parent) {
		super(definition, parent);
	}
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		Alert alert = null;
		try {
			alert = getDriver().switchTo().alert();
		} catch(Exception e) {
			log("Alert not present");
			if("true".equals(getDefinition().attributeValue("ifpresent"))) {
				return;
			} else {
				throw new RuntimeException(e);
			}
		}
		if("accept".equals(getDefinition().attributeValue("action"))) {
			log("Accepting alert");
			alert.accept();
		}
		else if("dismiss".equals(getDefinition().attributeValue("action"))) {
			log("Dismissing alert");
			alert.dismiss();
		}
	}
}
