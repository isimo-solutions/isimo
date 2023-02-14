package com.isimo.core.event;

import java.util.HashMap;
import java.util.Map;

public class EventType {
	private String name;

	public transient static Map<String, EventType> eventTypes = new HashMap<String, EventType>();
	
	public transient static EventType StartTestCase = new EventType("StartTestCase");
	public transient static EventType StopTestCase = new EventType("StopTestCase");
	public transient static EventType StartScenario = new EventType("StartScenario");
	public transient static EventType StopScenario = new EventType("StopScenario");
	public transient static EventType StartAction = new EventType("StartAction");
	public transient static EventType StopAction = new EventType("StopAction");
	public transient static EventType Error = new EventType("Error");
	public transient static EventType Terminated = new EventType("Terminated");
	public transient static EventType Finished = new EventType("Finished");
	public transient static EventType Comment = new EventType("Comment");
	
	
	private EventType(String name) {
		this.name = name;
		eventTypes.put(name, this);
	}
	
	public static EventType get(String name) {
		EventType et = eventTypes.get(name);
		if(et!=null)				
			return eventTypes.get(name);
		else
			return new EventType(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null)
			return false;
		return ((EventType)obj).name.equals(name);
	}
	
	
}
