package com.isimo.core.model;

public class ModelValidationException extends Exception {
	String message = null;
	public ModelValidationException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
