package com.isimo.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;

import com.isimo.core.Action;

import net.minidev.json.annotate.JsonIgnore;

public class Event extends Message {
	EventType eventType = null;
	volatile Action currentAction = null;

	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public static Event event(EventType eventType, Action currentAction, String... pMetadataValues) {		
		ArrayList<String> metadataValues = new ArrayList<String>();
		metadataValues.add(eventType.getName());
		metadataValues.add(null);
		metadataValues.addAll(Arrays.asList(pMetadataValues).stream().map(x -> (String) x).collect(Collectors.toList()));
		Event ret = new Event(metadataValues.toArray(new String[metadataValues.size()]));
		ret.eventType = eventType;
		ret.currentAction = currentAction;
		return ret;
	}

	public Event(String... pMetadataValues) {
		super(pMetadataValues);
	}
	
	public static Event startTestCase(String testcaseName) {
		return Event.event(EventType.StartTestCase, null, "testName", testcaseName);
	}
	
	public static Event stopTestCase(String testcaseName) {
		return Event.event(EventType.StopTestCase, null, "testName", testcaseName);
	}
	
	public static Event startScenario(String scenarioName) {
		return Event.event(EventType.StartScenario, null,  "scenarioName", scenarioName);
	}
	
	public static Event stopScenario(String scenarioName) {
		return Event.event(EventType.StopScenario, null, "scenarioName", scenarioName);
	}
	
	public static Event comment(String comment) {
		return Event.event(EventType.Comment, null, "comment", comment);
	}
	
	public static Event startAction(Action action) {
		return Event.event(EventType.StartAction, action, "actionName", action.getDefinitionOrig().getName());
	}
	
	public static Event stopAction(Action action) {
		return Event.event(EventType.StopAction, action, "actionName", action.getDefinitionOrig().getName());
	}
	
	public static Event error(Action action) {
		return Event.event(EventType.Error, action, "actionName", action.getDefinitionOrig().getName());
	}
	
	public static Event terminated(Action action) {
		return Event.event(EventType.Terminated, action, "actionName", action.getDefinitionOrig().getName());
	}
	



	public EventType getEventType() {
		return eventType;
	}


	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	
	public Action getCurrentAction() {
		return currentAction;
	}


	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}
	
	
}
