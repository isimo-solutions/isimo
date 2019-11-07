package com.isimo.core.model;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.ErrorHandler;

public class ModelSchema extends Schema {
	
	ErrorHandler errorHandler = null;

	@Override
	public Validator newValidator() {
		ModelValidator mv = new ModelValidator();
		mv.setErrorHandler(getErrorHandler());
		return mv;
	}

	@Override
	public ValidatorHandler newValidatorHandler() {
		return null;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler pErrorHandler) {
		errorHandler = pErrorHandler;
	}
	
	

}
