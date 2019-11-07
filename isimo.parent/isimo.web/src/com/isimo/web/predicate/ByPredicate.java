package com.isimo.web.predicate;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Lists;
import com.isimo.core.Action;
import com.isimo.core.Predicate;
import com.isimo.core.SpringContext;
import com.isimo.core.annotations.IsimoPredicate;
import com.isimo.web.IsimoWebProperties;
import com.isimo.web.WebAction;
import com.isimo.web.WebDriverProvider;

@IsimoPredicate
public class ByPredicate extends Predicate<Object> {

	@Override
	public Pair<Boolean, Object> evaluate(Action action) {
		ExpectedCondition<Pair<Boolean, Object>> cond = null;
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), IsimoWebProperties.getInstance().isimo.asserttimeout);
		wait.pollingEvery(Duration.ofMillis(100));
		By by = WebAction.getByNoException(action.getDefinition(), action);
		if(by != null) {
			
			if(action instanceof WebAction)
				SpringContext.getBean(WebDriverProvider.class).waitForPageLoad();
			int maxcounter = IsimoWebProperties.getInstance().isimo.shorttimeout;
			String maxcounterstr = action.getDefinition().attributeValue("maxcounter");
			if(maxcounterstr!=null)
				maxcounter = Integer.parseInt(maxcounterstr);
			cond = stableConditionTest(byCondition(action, by), maxcounter, action);
			action.log("ByPredicate: Cond = "+cond);
			Pair<Boolean, Object> result = Pair.of(false, null);
			try {
				result = wait.until(cond);
				action.log("ByPredicate: Cond evaluated to true");
			} catch(TimeoutException e) {
				throw new RuntimeException(e);
			}
			return result;
		}
		return Pair.of(true, null);
	}
	
	ByPredicateConditionWrapper byCondition(Action action, By by) {
		if(action.getDefinition().attribute("positive")!=null && action.getDefinition().attribute("negative")!=null)
			throw new RuntimeException("Both positive and negative attribute can't be specified in one by predicate!");
		boolean negative = "true".equals(action.getDefinition().attributeValue("negative"));
		if(action.getDefinition().attribute("positive")!=null)
			negative = "false".equals(action.getDefinition().attributeValue("positive"));
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), IsimoWebProperties.getInstance().isimo.asserttimeout);
		ExpectedCondition cond = ExpectedConditions.presenceOfElementLocated(by);
		ByPredicateConditionWrapper.ConditionType type = ByPredicateConditionWrapper.ConditionType.SINGLEELEMENT;
		if(negative) {
			//cond = ExpectedConditions.numberOfElementsToBe(by, 0);
			type = ByPredicateConditionWrapper.ConditionType.NEGATIVE;
		}
		else if("true".equals(action.getDefinition().attributeValue("visible"))) {
			cond = ExpectedConditions.and(cond, ExpectedConditions.elementToBeClickable(by));
			type = ByPredicateConditionWrapper.ConditionType.BOOLEAN;
		}
		SpringContext.getTestExecutionManager().log("Condition to be evaluated: "+cond, action);
		return new ByPredicateConditionWrapper(cond, type);
	}
	
	static ExpectedCondition<List<WebElement>> isByInSearchContext(By by, By searchContextBy, final boolean negative, final Action action) {

		return new ExpectedCondition<List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebDriver webdriver) {
				SearchContext searchContext = WebDriverProvider.getInstance().getWebDriver();
				if(searchContextBy!=null)
					searchContext = searchContext.findElement(searchContextBy);
				// TODO Auto-generated method stub
				action.log("Testing by "+by);
				List<WebElement> result = searchContext.findElements(by);
				if(!negative && !result.isEmpty())
					return result;
				else if(negative && result.isEmpty())
					return result;
				else
					return null;
			}
		};
	}
	
	static ExpectedCondition<Pair<Boolean,Object>> stableConditionTest(final ByPredicateConditionWrapper totest, int maxcounter, final Action action) {
		return new ExpectedCondition<Pair<Boolean,Object>>() {
			Boolean result = false;
			Object detailresult = null;
			int counter = 0;
			@Override
			public Pair<Boolean,Object> apply(WebDriver webdriver) {
				Object retvalobj = null;
				try {
					retvalobj = totest.getCondition().apply(webdriver);
				} catch(WebDriverException e) {
					action.log("Exception: "+e.getMessage());
				}
				if(ByPredicateConditionWrapper.ConditionType.BOOLEAN.equals(totest.getType())) {
					Boolean ret = (Boolean) retvalobj;
					Boolean lastretval = (ret!=null)&&ret;
					incBoolean(lastretval);
				} else if(ByPredicateConditionWrapper.ConditionType.LISTTEST.equals(totest.getType())
						|| ByPredicateConditionWrapper.ConditionType.NEGATIVE.equals(totest.getType())) {
					if(retvalobj instanceof WebElement)
						incSingleElement((WebElement) retvalobj);
					else {
						List<WebElement> list = (List<WebElement>) retvalobj;
						incList(list);
					}
				} else if(ByPredicateConditionWrapper.ConditionType.SINGLEELEMENT.equals(totest.getType())) {
					WebElement elem = (WebElement) retvalobj;
					incSingleElement(elem);
				}
				action.log("Counter="+counter+";"+this.toString());
				if(counter >= maxcounter) {
					if(ByPredicateConditionWrapper.ConditionType.NEGATIVE.equals(totest.getType()))
						result = !result;
					action.log("Result:  " + result + " Detailed: " + detailresult);
					return Pair.of(result, detailresult);
				} else
					return null;
			}
				
			public void incBoolean(Boolean val) {
				if(val.equals(detailresult)) {
					counter++;
				} else {
					result = val;
					detailresult = val;
					counter = 0;
				}
			}
			
			void incList(List<WebElement> val) {
				List<WebElement> listdetailresult = (List<WebElement>) detailresult;
				if(val==null && listdetailresult==null) {
					action.log("List is empty");
					counter++;
				} else if((val != null && detailresult != null) && listdetailresult.size() == val.size()){
					action.log("List has "+val.size()+" elements");
					counter++;
				} else {
					result = (val!=null) && !val.isEmpty();
					detailresult = val;
					counter = 0;
				}
			}
			
			void incSingleElement(WebElement val) {
				if((val==null && detailresult==null) || (val!=null && detailresult!=null)) {
					counter++;
				} else {
					result = (val!=null);
					counter = 0;
					detailresult = val;
				}
			}
		};
	}
	
	static boolean listsSizeEqual(List l1, List l2) {
		if(l1 == null && l2 ==null)
			return true;
		if((l1 != null && l2 == null) || (l1 == null && l2 != null))
			return false;
		return l1.size() == l2.size();
	}
	
	

	public static Pair<Boolean, List<WebElement>> testIfByPresent(By by, By searchContextBy, boolean negative, int mcount, Action a) {		
		try {
			WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), IsimoWebProperties.getInstance().isimo.shorttimeout);
			ExpectedCondition<Pair<Boolean, Object>> testIfByPresentStable= stableConditionTest(new ByPredicateConditionWrapper(isByInSearchContext(by, searchContextBy, negative, a), ByPredicateConditionWrapper.ConditionType.LISTTEST), mcount, a);
			Pair<Boolean, Object> waitresult = wait.until(testIfByPresentStable);
			return Pair.of(waitresult.getLeft(), (List<WebElement>)waitresult.getRight());
		} catch(WebDriverException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}
		

}
