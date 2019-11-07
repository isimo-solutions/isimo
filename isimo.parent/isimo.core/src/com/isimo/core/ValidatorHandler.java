package com.isimo.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.LocatorImpl;

public class ValidatorHandler extends DefaultHandler2 {
	@Autowired
	TestExecutionManager testExecutionManager;
	
	LocatorImpl locator = new LocatorImpl();
	public ValidatorHandler() {
		this.setDocumentLocator(locator = new LocatorImpl());
	}
	
	@Override
	public void startElement(String pParamString1, String pParamString2, String pParamString3, Attributes pParamAttributes) throws SAXException {
		super.startElement(pParamString1, pParamString2, pParamString3, pParamAttributes);
	}
	
	@Override
	public void fatalError(SAXParseException pParamSAXParseException) throws SAXException {
		testExecutionManager.log("FATAL: "+pParamSAXParseException.getMessage(), null);
		super.fatalError(pParamSAXParseException);
	}
	
	@Override
	public void error(SAXParseException pParamSAXParseException) throws SAXException {
		testExecutionManager.log("ERROR: "+pParamSAXParseException.getMessage(), null);
	}
	
	@Override
	public void warning(SAXParseException pParamSAXParseException) throws SAXException {
		testExecutionManager.log("WARNING: "+pParamSAXParseException.getMessage(), null);
	}
}
