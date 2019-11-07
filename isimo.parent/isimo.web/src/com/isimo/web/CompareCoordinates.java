package com.isimo.web;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.dom4j.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.isimo.core.Action;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class CompareCoordinates extends WebAction {

	public CompareCoordinates(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		boolean vertical = "vertical".equals(getDefinition().attributeValue("orientation"));
		List<Element> locators = getDefinition().elements(); 
		List<By> bylocators = locators.stream().map(l -> getBy(l, this)).collect(Collectors.toList());
		Function<WebElement, Integer> getcoord;
		if(vertical) {
			getcoord = elem -> elem.getLocation().getX();
		} else {
			getcoord = elem -> elem.getLocation().getY();
		}
		List<WebElement> webelems = bylocators.stream().map(by -> findElement(by)).collect(Collectors.toList());
		List<Integer> positions = webelems.stream().map(w -> getcoord.apply(w)).collect(Collectors.toList());
		int firstposition = -1;
		int i = 1;
		for(Integer pos: positions) {
			if(i==1)
				firstposition = pos;
			if(firstposition!=pos)
				testExecutionManager.logProblem("Position of the first element ("+bylocators.get(0)+","+firstposition+") not equal to the position of element "+i+" ("+bylocators.get(i-1)+","+pos+")", this);
			i++;
		}
	}
}
