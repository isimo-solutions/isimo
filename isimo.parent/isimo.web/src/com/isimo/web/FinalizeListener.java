package com.isimo.web;

import org.springframework.stereotype.Component;

import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;

@Component
public class FinalizeListener implements ExecutionListener<Event> {
	@Override
	public void handleEvent(Event event) {
		if(event.getEventType() == EventType.StopTestCase) {
			System.out.println("Quitting web driver!");
			WebDriverProvider.getInstance().getWebDriver().quit();
		}
	}
}
