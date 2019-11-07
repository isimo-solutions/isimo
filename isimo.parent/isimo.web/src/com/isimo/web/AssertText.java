package com.isimo.web;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class AssertText extends WebAction {
	AssertText(LocationAwareElement definition, Action parent) {
		super(definition, parent);
	}

	public void executeAtomic() throws Exception {
		try {
			(new WebDriverWait(getDriver(), IsimoWebProperties.getInstance().isimo.shorttimeout)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(.,'"+getDefinition().attributeValue("pattern")+"')]")));
			log(" AssertText checking was successful!!  ");
		} catch(Exception e) {
			logProblem("xpath="+getDefinition().attributeValue("pattern"));
		}
	}

}
