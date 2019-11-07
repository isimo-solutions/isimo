package com.isimo.core;

import org.springframework.stereotype.Component;

import com.isimo.core.event.Event;
import com.isimo.core.event.EventType;
import com.isimo.core.event.ExecutionListener;

@Component
public class TimerListener implements ExecutionListener<Event> {
	@Override
	public void handleEvent(Event pEvent) {
		if(pEvent.getEventType() == EventType.Error || pEvent.getEventType() == EventType.StopAction)
			pEvent.getCurrentAction().calcDuration();
		else if(pEvent.getEventType() == EventType.StartAction)
			pEvent.getCurrentAction().initTimer();
	}
}
