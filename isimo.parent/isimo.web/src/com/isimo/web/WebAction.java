package com.isimo.web;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.base.Function;
import com.isimo.core.Action;
import org.openqa.selenium.interactions.Actions;
import com.isimo.core.AtomicAction;
import com.isimo.core.SpringContext;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import com.isimo.core.TestCases;
import com.isimo.core.model.ByModel;

import org.openqa.selenium.WebDriverException;
import com.isimo.core.xml.LocationAwareElement;
import com.isimo.web.predicate.ByPredicate;

public abstract class WebAction extends AtomicAction {
	
	protected IsimoWebProperties isimoWebProperties;
	protected WebDriverProvider webDriverProvider;

	public WebAction(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
		isimoWebProperties = SpringContext.getBean(IsimoWebProperties.class);
		webDriverProvider = SpringContext.getBean(WebDriverProvider.class);
	}

	/**
	 * Checks if the WebElement given by by the By-selector is visible (or not) in the given SearchContext
	 * 
	 * @param ctx
	 *            - SearchContext to be searched
	 * @param by
	 *            - by-Selector to find the WebElement
	 * @param visible
	 *            - if true checks if the WebElement is visible, if false - if not visible (doesn't exist on the page or is not visible)
	 * @param action
	 *            Action during which this method is executed
	 * @param checkvisible
	 *            - is the visibility of the element should be tested
	 * @return WebElement if visible is set to true, if visible is set to false returns null
	 */
	public WebElement verify(SearchContext ctx, final By by, boolean visible, boolean checkvisible) {
		WebElement elem = null;
		SearchContext sc = null;
		if (ctx == null)
			sc = WebDriverProvider.getInstance().getWebDriver();
		else
			sc = ctx;
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		webDriverProvider.waitForPageLoad();
		if (visible) {
			log("Checking if element " + by.toString() + " is available");
			elem = verifyAvailable(by, sc);
			log("OK, element " + by.toString() + " is available");
			//			elem = "false".equals(TestCases.getInstance().getProperties().getProperty("isimo.waitsequential"))?waitForElementSequential(action, sc, by):waitForElementParallel(elements, action, sc, by);
			if (checkvisible) {
				log("Checking if element " + by.toString() + " is " + (visible ? "" : " not ") + "visible");
				elem = waitForElementSequential(sc, by);
				log("OK, element " + by.toString() + " is visible");
			}
			if (elem == null) {
				throw new RuntimeException("None of the found elements has become visible");
			}
		} else {
			WebDriverWait wait2 = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
			log("Waiting for invisibility of by: " + by);
			wait2.until(invisibilityOf(by));
			log("OK, element " + by.toString() + " is not visible");
		}
	
		return elem;
	}

	/**
	 * Finds an element specified by the by object in the given SearchContext
	 * 
	 * @param by
	 *            by-selector to find an element
	 * @param ctx
	 *            SearchContext to be searched, if null the whole page will be searched
	 * @return WebElement found element
	 */
	public WebElement findElement(By by, SearchContext ctx) {
		int i = 0;
		SearchContext ctxtosearch;
		if (ctx == null)
			ctxtosearch = WebDriverProvider.getInstance().getWebDriver();
		else
			ctxtosearch = ctx;
		ExpectedCondition<Boolean> elementPresent = new ExpectedCondition<Boolean>() {
			WebElement retval = null;
			Exception lastException = null;
			@Override
			public Boolean apply(WebDriver pArg0) {
				// TODO Auto-generated method stub try
				try {
					List<WebElement> elements = ctxtosearch.findElements(by);
					if(!elements.isEmpty()) {
						retval = elements.get(0);
						lastException = null;
						log("Waiting, element present!", null);
						return true;
					}
				} catch(Exception e) {
					lastException = e;
					log("Waiting, element not present - exception!", null);
					return false;
				}
				log("Waiting, element not present!", null);
				return false;
			}
			public WebElement getRetval() {
				return retval;
			}
			public Exception getLastException() {
				return lastException;
			}
			
		};
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		wait.until(elementPresent);
		try {
			Method m = elementPresent.getClass().getMethod("getRetval");
			WebElement elem = (WebElement) m.invoke(elementPresent);
			if(elem!=null)
				return elem;
			Method mGetLastException = elementPresent.getClass().getMethod("getLastException");
			Exception lastException = (Exception) mGetLastException.invoke(elementPresent);
			throw new TimeoutException("Element identified by " + by + " not present", lastException);
		} catch(TimeoutException e) {
			
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds an element specified by the by object in the whole page
	 * 
	 * @param by
	 * @return WebElement found element
	 * @exception RuntimeException
	 *                when the element can't be found
	 */
	public WebElement findElement(By by) {
		return findElement(by, null);
	}

	/**
	 * Static version of the {@link Action#verifyAvailable(By, SearchContext)} method
	 * 
	 * @param by
	 *            by-Selector to be used when searching for the WebElement
	 * @param ctx
	 *            context to be searched, if null the whole page is searched
	 * @param action
	 *            Action during which the method is executed
	 * @return the available WebElement found
	 */
	public WebElement verifyAvailable(By by, SearchContext ctx) {
		WebElement elem = findElement(by, ctx);
		return elem;
	}
	
	

	/**
	 * Static version of the {@link Action#click(By, boolean)} method
	 * 
	 * @param by
	 *            - By selector
	 * @param action
	 *            - action during which this method is executed
	 * @param checkvisible
	 *            - if set to true it's required that the element should be visible, otherwise click will take place based on the coordinates of the element
	 */
	WebElement click(By by, boolean checkvisible) {
		Exception ex = null;
		for (int i = 0; i < isimoWebProperties.isimo.retrycount; i++) {
			try {
				webDriverProvider.waitForPageLoad();
				WebElement element;
				if (checkvisible) {
					element = (WebElement) verifyVisible(by);
					element.click();
				} else {
					element = WebDriverProvider.getInstance().getWebDriver().findElement(by);
					Actions actions = new Actions(WebDriverProvider.getInstance().getWebDriver());
					actions.moveToElement(element).click().build().perform();
				}
				return element;
			} catch (Exception e) {
				ex = e;
			}
		}
		throw new RuntimeException(ex);
	}
	
	
	
	

	/**
	 * Checks if the element is available on the whole page using the given by-Selector
	 * 
	 * @param by
	 *            by-Selector to be used when searching for the WebElement
	 * @return the available WebElement found
	 */
	WebElement verifyAvailable(By by) {
		webDriverProvider.waitForPageLoad();
		return verifyAvailable(by, null);
	}



	/**
	 * Static version of the {@link Action#verifyVisible(By)} method
	 * 
	 * @param by
	 *            by-Selector
	 * @param action
	 *            Action during which this method is executed
	 * @return found WebElement if visible
	 */
	public WebElement verifyVisible(By by) {
		return verify(null, by, true, this.checkVisible());
	}

	public static boolean checkVisible(Element elem) {
		return !"false".equals(elem.attributeValue("checkvisible"));
	}

	public boolean checkVisible() {
		return checkVisible(getDefinition());
	}



	/**
	 * Checks if the WebElement given by the By-selector is visible (or not) on the whole page
	 * 
	 * @param by
	 *            - by-Selector to find the WebElement
	 * @param visible
	 *            - if true checks if the WebElement is visible, if false - if not visible (doesn't exist on the page or is not visible)
	 * @return WebElement if visible is set to true, if visible is set to false returns null
	 */
	public WebElement verify(By by, boolean visible) {
		return verify(null, by, visible, checkVisible());
	}

	/**
	 * Checks if the WebElement given by the By-selector is visible (or not) on the whole page
	 * 
	 * @param by
	 *            - by-Selector to find the WebElement
	 * @param visible
	 *            - if true checks if the WebElement is visible, if false - if not visible (doesn't exist on the page or is not visible)
	 * @return WebElement if visible is set to true, if visible is set to false returns null
	 */
	public WebElement verify(By by, boolean visible, boolean checkVisible) {
		return verify(null, by, visible, checkVisible);
	}

	/**
	 * Creates the FluentWait Function for checking if the element specified by the given By is not found on the page
	 * 
	 * @param by
	 *            by-Selector using which the Element should be found
	 * @return Function checking if the Element specified by the By expression is not visible
	 */
	public Function<WebDriver, Boolean> invisibilityOf(final By by) {
		return new Function<WebDriver, Boolean>() {
			int secondsInARow = 0;
			boolean notvisible = false;
			WebDriver driver;
			@Override
			public Boolean apply(WebDriver driver) {
				this.driver = driver;
				try {
					List<WebElement> webElements = null;
					try {
						webElements = driver.findElements(by);
					} catch (Exception e) {
						webElements = new ArrayList<WebElement>();
					}
					if (webElements.size() == 0) {
						returnOrCheckAgain();
					} else {
						for (WebElement webElement : webElements) {
							if (webElement != null && webElement.isDisplayed())
								return false;
						}
						returnOrCheckAgain();
					}
				} catch (WebDriverException e) {
					return false;
				}
				return notvisible;
			}
			
			void returnOrCheckAgain() {
				if(secondsInARow > isimoWebProperties.isimo.invisibilitytimeout)
					notvisible = true;
				else {
					secondsInARow++;
					log("invisibilityOf, slept "+secondsInARow+" ...", null);
					testExecutionManager.sleep(1);
					apply(driver);
				}
			}
		};
		
		
	}

	/**
	 * Creates the FluentWait Function for checking if the element specified by the given By is present on the page
	 * 
	 * @param by
	 *            by-Selector using which the WebElement should be found
	 * @param sc
	 *            the SearchContext in which the WebElement should be found
	 * @return
	 */
	public static Function<WebDriver, Boolean> presenceOf(final By by, final SearchContext sc) {
		return new Function<WebDriver, Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> webElements = sc.findElements(by);
					if (webElements.size() > 0)
						return true;
					return false;
				} catch (WebDriverException e) {
					return false;
				}
			}
		};
	}

	/**
	 * Waits until one of the elements defined by by-Selector becomes visible, this function is particularly useful when multiple copies of the element are present on the page but only one is visible
	 * 
	 * @param action
	 *            Action during which this method is executed
	 * @param sc
	 *            SearchContext in which
	 * @param by
	 *            By-selector to identify the element
	 * @return The visible WebElement if found
	 * @exception TimeoutException
	 *                if not found
	 */
	public WebElement waitForElementSequential(SearchContext sc, By by) {
		ExpectedCondition<Boolean> elementVisible = new ExpectedCondition<Boolean>() {
			WebElement retval = null;
			@Override
			public Boolean apply(WebDriver pArg0) {
				// TODO Auto-generated method stub
				try {
					List<WebElement> elements = sc.findElements(by);
					for (WebElement element : elements) {
						if (element != null && ExpectedConditions.and(ExpectedConditions.visibilityOf(element), ExpectedConditions.elementToBeClickable(element)).apply(WebDriverProvider.getInstance().getWebDriver())) {						
							retval = element;
							return true;
						}
					}
					return false;
				} catch(StaleElementReferenceException e) {
					//log("StaleElementReferenceException: "+e, action);
					return false;
				}
			}
			
			public WebElement getRetval() {
				return retval;
			}
		};
		
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		wait.until(elementVisible);
		try {
			Method m = elementVisible.getClass().getMethod("getRetval");
			WebElement elem = (WebElement) m.invoke(elementVisible);
			if(elem!=null)
				return elem;
			throw new TimeoutException("Element identified by " + by + " not visible");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Static version of the {@link Action#select(By, String, String)} select method allowing to pass the action context explicitly
	 * 
	 * @param s
	 * @param option
	 * @param type
	 * @param action
	 */
	public org.openqa.selenium.support.ui.Select select(By by, String option, String type) {
		webDriverProvider.waitForPageLoad();
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		if (!Select.VISIBLE_TEXT.equals(type) && !Select.VALUE.equals(type) && !Select.INDEX.equals(type)) {
			throw new RuntimeException("Select type unknown");
		}
		org.openqa.selenium.support.ui.Select select = wait.until(getSelectCondition(by, type, option));
		return select;
	}
	
	
	public ExpectedCondition<org.openqa.selenium.support.ui.Select> getSelectCondition(final By byselect, final String type, final String option) {
		ExpectedCondition<org.openqa.selenium.support.ui.Select> evaluatedConditionStable = new ExpectedCondition<org.openqa.selenium.support.ui.Select>() {
			@Override
			public org.openqa.selenium.support.ui.Select apply(WebDriver webDriver) {
				try {
					org.openqa.selenium.support.ui.Select s = new org.openqa.selenium.support.ui.Select(findElement(byselect));
					if(Select.VISIBLE_TEXT.equals(type)) {
						s.selectByVisibleText(option);
					} else if(Select.VALUE.equals(type)) {
						s.selectByValue(option);
					} else if(Select.INDEX.equals(type)) {
						s.selectByIndex(Integer.parseInt(option));
					} else {
						throw new RuntimeException("Select type unknown - shoud not happen");
					}
				} catch(NoSuchElementException e) {
					return null;
				}
				org.openqa.selenium.support.ui.Select s = new org.openqa.selenium.support.ui.Select(findElement(byselect));
				log("Selected: " + s.getFirstSelectedOption().getAttribute("value"), null);
				return s;
			}
		};
		return evaluatedConditionStable;
	}
	

	public void clickTabOnBy(By by) throws Exception {
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		MutableObject<Exception> hex = new MutableObject<Exception>();
		Function<WebDriver, Boolean> clicked = new Function<WebDriver, Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					findElement(by).sendKeys(Keys.TAB);
					hex.setValue(null);
					return true;
				} catch(Exception e) {
					hex.setValue(e);
					return false;
				}
			}
		};
	    wait.until(clicked);
		if(hex.getValue()!=null)
			throw hex.getValue();
	}
	
	public WebDriver getDriver() {
		return WebDriverProvider.getInstance().getWebDriver();
	}
	
	
	public static By getByNoException(Element elem, Action action) {
		try {
			return getBy(elem, action);
		} catch(NoByException e) {
			return null;
		}
	}
	
	
	public static By getBy(Element elem, Action action) {
		if (elem.attribute("xpath") != null)
			return By.xpath(elem.attribute("xpath").getValue());
		else if (elem.attribute("id") != null)
			return By.cssSelector("*[id$=\"" + elem.attribute("id").getValue() + "\"]");
		else if (elem.attribute("css") != null)
			return By.cssSelector(elem.attribute("css").getValue());
		else if (elem.attribute("model") != null)
			return ByModel.model(elem.attribute("model").getValue(), action, elem.attributeValue("checkvisible"));
		else
			throw new NoByException("No By attribute found!");
	}

	protected By getBy() {
		return getBy(getDefinition(), this);
	}
	
	public static boolean negative(Element elem) {
		return "true".equals(elem.attributeValue("negative"));
	}

	public boolean negative() {
		return negative(getDefinition());
	}


	

	public void input(By by, String text, String compareText, boolean clear, boolean tab) {
		Exception ex = null;
		webDriverProvider.waitForPageLoad();
		for (int i = 0; i < isimoWebProperties.isimo.retrycount; i++) {
			try {
				WebElement input = verifyVisible(by);
				WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
				if (clear) {
					input.click();
					input.clear();
					wait.until(ExpectedConditions.textToBePresentInElementValue(by, ""));
					log("Wait for element to be cleared completed");
				}
				
				input = waitForElementSequential(WebDriverProvider.getInstance().getWebDriver(),by);	
				log("Wait for element to be clickable completed");
				input.click();
				
				while(true) {
					try {
						if ("".equals(text) || text == null) {
							input.sendKeys(Keys.BACK_SPACE);
						} else {
							input.sendKeys(text);
						}
						break;
					} catch(StaleElementReferenceException e) {
						log("!!!Warn: " + e);
						input = verifyVisible(by);
					}
				}
				if (tab) {
					testExecutionManager.sleep(1);
					input.sendKeys(Keys.TAB);
					webDriverProvider.waitForPageLoad();
				}
				if(compareText!=null) {
					waitForElementSequential(WebDriverProvider.getInstance().getWebDriver(),by);
					wait.until(ExpectedConditions.textToBePresentInElementValue(by, compareText));												
					log("Wait for element to be [" + compareText + "] completed");
				}
				/*
				 * String value = input.getAttribute("value"); if(value==null || !((String)value).contains(compareText)) { System.out.println("entered value '"+value+"' not equal to "+compareText);
				 * throw new RuntimeException("entered value not equal to "+compareText); }
				 */
				/*
				 * String enteredValue = input.getAttribute("value"); System.out.println("tagname="+input.getTagName()); if(!compareText.equals(enteredValue)) {
				 * 
				 * }
				 */
				return;
			} catch (Exception e) {
				System.out.println("Exception: " + e.toString());
				ex = e;
			}
		}
		throw new RuntimeException(ex);
	}
	
	public void executeAtomic() throws Exception {
		if(WebDriverProvider.getInstance().getWebDriver()!=null) {
			while(true) {
				try {
					getLog().addAttribute("log_url", WebDriverProvider.getInstance().getWebDriver().getCurrentUrl());
					getLog().addAttribute("log_title", WebDriverProvider.getInstance().getWebDriver().getTitle());
					getLog().addAttribute("log_testcase", testExecutionManager.getScenarioName());
					List<WebElement> activeTab = WebDriverProvider.getInstance().getWebDriver().findElements(By.xpath("//bm-tabs/ul/li[contains(@class,'state-active')]/a"));
					if(!activeTab.isEmpty())
						getLog().addAttribute("log_tab", activeTab.get(0).getText());
					break;
				} catch(WebDriverException e) {
					log("!!Warn: " + e.getMessage(),null);
					// retry
				}
			}
		}
		super.executeAtomic();
	}


}
