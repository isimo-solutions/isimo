package com.isimo.core.model;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ModelSchemaFactory extends SchemaFactory {
	ErrorHandler errorHandler = null;

	@Override
	public boolean isSchemaLanguageSupported(String pParamString) {
		return false;
	}

	@Override
	public void setErrorHandler(ErrorHandler pParamErrorHandler) {
		this.errorHandler = pParamErrorHandler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public void setResourceResolver(LSResourceResolver pParamLSResourceResolver) {

	}

	@Override
	public LSResourceResolver getResourceResolver() {
		return null;
	}

	@Override
	public Schema newSchema(Source[] pParamArrayOfSource) throws SAXException {
		return newSchema();
	}

	@Override
	public Schema newSchema() throws SAXException {
		// TODO Auto-generated method stub
		ModelSchema ms = new ModelSchema();
		ms.setErrorHandler(getErrorHandler());
		return ms;
	}

}
