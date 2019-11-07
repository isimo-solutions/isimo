package com.isimo.web;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Select extends WebAction {
	public final static String VISIBLE_TEXT = "visibleText";
	public final static String VALUE = "value";
	public final static String INDEX = "index";
	public final static String TAB = "tab";
	public org.openqa.selenium.support.ui.Select select = null;

	public Select(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		By by = getBy();
		select = null;
		if(getDefinition().attributeValue(VISIBLE_TEXT)!=null) {
			log("Selecting in the select with by "+by.toString()+" by visible text '"+getDefinition().attributeValue(VISIBLE_TEXT)+"'");
			select=select(by, getDefinition().attributeValue(VISIBLE_TEXT), VISIBLE_TEXT);
		} else if(getDefinition().attributeValue(VALUE)!=null) {
			log("Selecting in the select with by "+by.toString()+" by value '"+getDefinition().attributeValue(VALUE)+"'");
			select=select(by, getDefinition().attributeValue(VALUE), VALUE);
		} else if(getDefinition().attributeValue(INDEX)!=null) {
			log("Selecting in the select with by "+by.toString()+" by index '"+getDefinition().attributeValue(INDEX)+"'");
			select=select(by, getDefinition().attributeValue(INDEX), INDEX);
		} else {
			throw new RuntimeException("Select not configured properly");
		}
		if(!"false".equals(getDefinition().attributeValue("tab")))
			clickTabOnBy(by);
	}
}