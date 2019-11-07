package com.isimo.dependencies;

public class Dependency {
	Scenario source;
	Scenario target;
	int lineNumber;
	public Dependency() {
		// TODO Auto-generated constructor stub
	}
	
	public Scenario getSource() {
		return source;
	}
	
	public Scenario getTarget() {
		return target;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	
	
	
}
