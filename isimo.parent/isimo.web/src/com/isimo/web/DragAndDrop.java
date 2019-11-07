package com.isimo.web;


import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class DragAndDrop extends WebAction {

	DragAndDrop(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		boolean repeat;
		do {
			repeat = false;
			// TODO Auto-generated method stub
			WebElement source = findElement(By.xpath(getDefinition().attributeValue("source")));
			
			Point sourceLocation = source.getLocation();
			
			Dimension sourceSize = source.getSize();
			
			Actions builder = new Actions(getDriver());
	
			builder.moveToElement(source, (int) sourceSize.getWidth()/2, (int) sourceSize.getHeight()/2).perform();
			builder.clickAndHold().perform();
			WebElement target = findElement(By.xpath(getDefinition().attributeValue("target")));
			Dimension targetSize = target.getSize();
			Point targetLocation = target.getLocation();
			System.out.println("Sourceloc="+sourceLocation+";Targetloc="+targetLocation);
			System.out.println("Sourcesize="+sourceSize+";Targetsize="+targetSize);
			builder.moveByOffset(targetLocation.getX()-sourceLocation.getX(), targetLocation.getY()-sourceLocation.getY()).perform();
			builder.release().perform();
			//html5_DragAndDrop(driver, source, target, Position.Center, Position.Center);
			if(getDefinition().attributeValue("until")!=null) {
				try {
					WebDriverWait wait = new WebDriverWait(getDriver(), 3);
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(getDefinition().attributeValue("until"))));
				} catch(Exception e) {
					repeat = true;
				}
			}
		} while(repeat);
	}

}
