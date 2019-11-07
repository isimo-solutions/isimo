package com.isimo.core;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Predicate<T extends Object> {
	@Autowired
	TestExecutionManager testExecutionManager;
	
	public abstract Pair<Boolean, T> evaluate(Action action);
}
