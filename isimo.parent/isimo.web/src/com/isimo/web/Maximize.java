package com.isimo.web;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Maximize extends WebAction {

	Maximize(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		log("Maximizing window");
		getDriver().manage().window().maximize();
		if("100".equals(getDefinition().attributeValue("zoom")))
			WebDriverProvider.getInstance().getWebDriver().findElement(By.xpath("/html")).sendKeys(Keys.chord(Keys.CONTROL,"0"));
	}
}
