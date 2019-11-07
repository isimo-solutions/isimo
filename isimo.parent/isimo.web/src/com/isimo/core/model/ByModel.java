package com.isimo.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Element;

import com.isimo.core.Action;
import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.TestExecutionManager;
import com.isimo.web.WebDriverProvider;

public class ByModel extends By {
	private String path;
	private Action action;
	private boolean checkvisible = false;

	private ByModel(String path, Action action, boolean checkvisible) {
		this.path = path;
		this.action = action;
		this.checkvisible = checkvisible;
	}

	@Override
	public List<WebElement> findElements(SearchContext searchContext) {
		try {
			while(true) {
				try {
					WebElement element = ByModel.getWebElementByModelPath(path, action, checkvisible);
					if(element!=null)
						return Arrays.asList(element);
					else
						return new ArrayList<WebElement>();
				} catch(Exception e) {
					if(!hasStaleElementReferenceException(e)) {
						throw e;
					}
				}
			}
		} catch(NoSuchElementException e) {
			return new ArrayList<WebElement>();
		} catch(Exception e) {
			throw e;
		}
	}
	
	public static boolean hasStaleElementReferenceException(Throwable e) {
		if(e instanceof StaleElementReferenceException)
			return true;
		if(e.getCause()!= null)
			return hasStaleElementReferenceException(e.getCause());
		else
			return false;
	}

	public static ByModel model(String path, Action action, String checkVisible) {
		return new ByModel(path, action, !"false".equals(checkVisible));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " ByModel:"+path+" ";
	}

	private static SearchContext getWebElementByContextAndElement(SearchContext context, Element pathelement, Action action) {
		String xpath = pathelement.hasAttribute("xpath")?pathelement.getAttribute("xpath"):null;
		String css = pathelement.hasAttribute("css")?pathelement.getAttribute("css"):null;
		SearchContext ctx = context;
		if(ctx == null)
			ctx = WebDriverProvider.getInstance().getWebDriver();
		By by = null;
		if(xpath!=null) {
			by = By.xpath(xpath);
		} else if(css!=null) {
			by = By.cssSelector(css);
		} else {
			return ctx;
		}
		List<WebElement> elements = ctx.findElements(by);
		if(elements.size() > 1) {
			for(WebElement e: elements) {
				if(e.isDisplayed() && e.isEnabled())
					return e;
			}
			return elements.get(0);
		} else if(elements.size() == 1) {
			return elements.get(0);
		} else {
			return null;
		}
	}

	public static WebElement getWebElementByModelPath(String path, Action action, boolean checkvisible) {
		List<Map.Entry<String,Element>> retval = ModelValidator.getDefinitionsFromPath(Model.getInstance().getModel(), path);
		SearchContext currentElement = WebDriverProvider.getInstance().getWebDriver();
		for(Map.Entry<String, Element> pathelem: retval) {
			if(pathelem.getValue()==null)
				throw new RuntimeException("Wrong path: "+path+"; element ["+pathelem.getKey()+"] not found");
			currentElement = getWebElementByContextAndElement(currentElement, pathelem.getValue(), action);
			SpringContext.getBean(TestExecutionManager.class).log("Returned WebElement ["+currentElement+"] for pathlement: ["+pathelem.getKey()+"]", action);
			if(currentElement == null)
				break;
		}
		if(currentElement instanceof WebElement) {
			WebElement elem = (WebElement) currentElement;
			if(checkvisible && !elem.isDisplayed())
				return null;
			return elem;
		} else
			return null;
	}
}
