package com.isimo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;

@Component
public class FinalizeListener implements ExecutionListener<Event> {
	@Autowired
	IsimoWebProperties properties;
	
	@Override
	public void handleEvent(Event event) {
		if(event.getEventType() == EventType.StopTestCase &&
				WebDriverProvider.getInstance().getWebDriver()!=null && properties.isimo.closebrowseraftertest) {
			System.out.println("Quitting web driver!");
			WebDriverProvider.getInstance().getWebDriver().close();
			WebDriverProvider.getInstance().getWebDriver().quit();
		}
	}
}
