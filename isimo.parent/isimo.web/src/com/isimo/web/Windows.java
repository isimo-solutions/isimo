package com.isimo.web;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Windows extends WebAction {
	public Windows(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), Duration.ofSeconds(isimoWebProperties.isimo.shorttimeout));

		
		if(getDefinition().attribute("waitforcount")!=null) {
			wait.until(windowCountIs(Integer.parseInt(getDefinition().attributeValue("waitforcount"))));
		} else if(getDefinition().attribute("close")!=null) {
			String close = getDefinition().attributeValue("close");
			if("%%last%%".equals(close)) {
				getDriver().switchTo().window((String)getDriver().getWindowHandles().toArray()[getDriver().getWindowHandles().size()-1]);
				getDriver().close();
				getDriver().switchTo().window((String)getDriver().getWindowHandles().toArray()[getDriver().getWindowHandles().size()-1]);
			} else {
				wait.until(windowClosedAndFirstAvailableOpened(close));
			}
		}
	}
	
	static ExpectedCondition<Boolean> windowCountIs(int count) {
		ExpectedCondition<Boolean> cond = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				// TODO Auto-generated method stub
				return webDriver.getWindowHandles().size() == count;
			}
		};
		return cond;
	}
	
	static ExpectedCondition<Boolean> windowClosedAndFirstAvailableOpened(String title) {
		ExpectedCondition<Boolean> cond = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				boolean done = false;
				for(String handle: webDriver.getWindowHandles()) {
					if(title.equals(webDriver.switchTo().window(handle).getTitle())) {
						webDriver.close();
						for(String handle1: webDriver.getWindowHandles()) {
							webDriver.switchTo().window(handle1).getTitle();
						}
						done = true;
					};
				}
				return done;
			}
		};
		return cond;
	}

}
