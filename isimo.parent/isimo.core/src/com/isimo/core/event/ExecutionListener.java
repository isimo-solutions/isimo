package com.isimo.core.event;

import org.springframework.stereotype.Component;

@Component
public interface ExecutionListener<E extends Event> {
	public abstract void handleEvent(E event);
}
