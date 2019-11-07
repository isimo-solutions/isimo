package com.isimo.web;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.SpringContext;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class CheckStyle extends WebAction {
	public CheckStyle(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	public void executeAtomic() throws Exception {
		// TODO Auto-generated method stub
		super.executeAtomic();
		for(Element byelem:getDefinition().element("elements").elements("element")) {
			By by = WebAction.getBy(byelem, this);
			WebElement elem = verify(by, true, WebAction.checkVisible(byelem));
			for(Element css: getDefinition().elements("css")) {
				checkExpected(elem, css, byelem, by);
			}
		}
	}
	
	void checkExpected(WebElement elem, Element css, Element byelem, By by) {
		List<String> possibleValues = new ArrayList<String>();
		Attribute expected = css.attribute("expected");
		if(expected!=null)
			possibleValues.add(expected.getValue());
		else {
			for(Node expnode : css.elements("expected")) {
				possibleValues.add(expnode.getText());
			}
		}
		verifyPossibleValues(elem, css.attributeValue("property"), possibleValues, byelem, by);
	}
	
	void verifyPossibleValues(WebElement elem, String cssProperty, List<String> possibleValues, Element byelem, By by) {
		String pValues = "";
		String actualValue = elem.getCssValue(cssProperty); 
		for(String value: possibleValues) {
			pValues += value+",";
			if(value.equals(actualValue))
				return;
		}
		if(!pValues.isEmpty())
			pValues = pValues.substring(0,pValues.length()-1);
		SpringContext.getTestExecutionManager().logProblem("Wrong css value: on element found by "+by+" expected: "+pValues+", found: "+actualValue, byelem, this);
	}
}
