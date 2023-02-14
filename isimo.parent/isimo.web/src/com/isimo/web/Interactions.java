package com.isimo.web;

import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Locatable;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Interactions extends WebAction {
	
	public Interactions(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		List<Node> subnodes = getDefinition().selectNodes("node()");
		java.awt.Robot robot = new java.awt.Robot();
		robot.setAutoDelay(50);
		robot.setAutoWaitForIdle(false);
		for(Node e: subnodes) {
			if(!(e instanceof Element))
				continue;
			Element elem = (Element)e;
			if("clickandhold".equals(elem.getName())) {
				log("Executing clickandhold");
				robot.mousePress(InputEvent.BUTTON1_MASK);
			} else if("move".equals(elem.getName())) {
				log("Executing move to elem "+elem);
				move(elem, robot);
			} else if("click".equals(elem.getName())) {
				log("Executing mouseClick ");
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else if("release".equals(elem.getName())) {
				log("Executing mouseRelease");
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
		}
	}

	public void move(Element move, java.awt.Robot robot) throws Exception {
		By by = null;
		try {
			by = getBy(move, this);
		} catch(RuntimeException e) {
			// by nicht vorhanden am element
		}
		int offsetx = 0;
		int offsety = 0;
		int x = 0;
		int y = 0;
		if(move.attributeValue("offsetx")!=null)
			offsetx = Integer.parseInt(move.attributeValue("offsetx"));
		if(move.attributeValue("offsety")!=null)
			offsety = Integer.parseInt(move.attributeValue("offsety"));
		if(move.attributeValue("x")!=null)
			offsetx = Integer.parseInt(move.attributeValue("x"));
		if(move.attributeValue("y")!=null)
			offsety = Integer.parseInt(move.attributeValue("y"));
		String corner = "";
		if(move.attributeValue("corner")!=null)
			corner = move.attributeValue("corner");
		Point p = new Point(0,0);
		if(by != null) {
			WebElement elem = findElement(by);
			Rectangle rect = elem.getRect();
			Dimension dim = rect.getDimension();			
			int halfwidth = dim.getWidth() / 2;
			int halfheight = dim.getHeight() / 2;
			Locatable elementLocation = (Locatable) elem;
		    //Point p = elementLocation.getCoordinates().onPage();
		    JavascriptExecutor executor = (JavascriptExecutor) WebDriverProvider.getInstance().getWebDriver();
		    Point screenPos = getWindowPositionOnScreen();
		    Point elemPos = getElementPositionInWindow(elem);
		    p = screenPos.moveBy(elemPos.x, elemPos.y);
		    p.x += halfwidth;
		    p.y += halfheight;
			if("ul".equals(corner)) {
				p = p.moveBy(-halfwidth, -halfheight);
			} else if("ur".equals(corner))
				p = p.moveBy(halfwidth, -halfheight);
			else if("ll".equals(corner)) {
				p = p.moveBy(-halfwidth, halfheight);
			} else if("lr".equals(corner)) {
				p = p.moveBy(halfwidth, halfheight);
			}
		} else {
			p.x = x;
			p.y = y;
		}
		p = p.moveBy(offsetx, offsety);
		java.awt.Point point = MouseInfo.getPointerInfo().getLocation();
		log("currentPosition="+point);
		java.awt.Point nextPosition = new java.awt.Point(p.x, p.y);
		log("nextPosition="+nextPosition);
		robot.mouseMove(nextPosition.x, nextPosition.y);
	}
	
	public Point getWindowPositionOnScreen() {
		Point windowPos = new Point(0,0);
		JavascriptExecutor js = (JavascriptExecutor) WebDriverProvider.getInstance().getWebDriver();
		if("internetExplorer".equals(testExecutionManager.getProperties().get("isimo.browser"))) {
			Long x = (Long) js.executeScript("return window.screenLeft");
			Long y = (Long) js.executeScript("return window.screenTop");
			windowPos.x = x.intValue();
			windowPos.y = y.intValue();
		} else if(("firefox").equals(testExecutionManager.getProperties().get("isimo.browser"))) {
			Long x = (Long) js.executeScript("return window.mozInnerScreenX");
			Long y = (Long) js.executeScript("return window.mozInnerScreenY");
			windowPos.x = x.intValue();
			windowPos.y = y.intValue();
		}
		return windowPos;
	}
	
	public static Point getElementPositionInWindow(WebElement element) {
		Point windowPos = new Point(0,0);
		Rectangle rect = element.getRect();
		windowPos.x = rect.x;
		windowPos.y = rect.y;
		return windowPos;
	}
}
