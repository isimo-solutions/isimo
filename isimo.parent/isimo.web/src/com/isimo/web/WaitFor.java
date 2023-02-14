package com.isimo.web;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class WaitFor extends WebAction {

	WaitFor(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		if(getDefinition().attribute("title")!=null){
			new WebDriverWait(getDriver(), Duration.ofSeconds(isimoWebProperties.isimo.shorttimeout)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver pDriver) {
					return pDriver.getTitle().equals(getDefinition().attributeValue("title"));
				}
			});
		} else if(getDefinition().attribute("count")!=null){
			new WebDriverWait(getDriver(), Duration.ofSeconds(isimoWebProperties.isimo.shorttimeout)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver pDriver) {
					int actualcount = pDriver.findElements(By.xpath(getDefinition().attributeValue("xpath"))).size();
					log("Actual count="+actualcount+", excpected count="+getDefinition().attributeValue("count"));
					return actualcount==Integer.parseInt(getDefinition().attributeValue("count"));
				}
			});
		} else {
			boolean negative = negative();
			By by = getBy();
			log("Waiting for "+(negative?" negative ":"")+" by: "+by.toString());
			verify(by, !negative );
		};
	}

}
