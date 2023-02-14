package com.isimo.web.predicate;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.isimo.core.Action;
import com.isimo.core.Predicate;
import com.isimo.core.annotations.IsimoPredicate;
import com.isimo.web.IsimoWebProperties;
import com.isimo.web.WebAction;
import com.isimo.web.WebDriverProvider;

@IsimoPredicate
public class TitlePredicate extends Predicate<String> {

	@Override
	public Pair<Boolean, String> evaluate(Action action) {
		String title = action.getDefinition().attributeValue("title");
		if(title != null) {
			action.log("TitlePredicate: expected title should be "+title);
			try {
				WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), Duration.ofSeconds(IsimoWebProperties.getInstance().isimo.asserttimeout));
				wait.until(ExpectedConditions.titleIs(action.getDefinition().attributeValue("title")));
				action.log("TitlePredicate: evaluated to true");
				return Pair.of(true, title);
			} catch(TimeoutException e) {
				action.log("TitlePredicate: evaluated to false");
				return Pair.of(false, title);
			}
		}
		String titlenotempty = action.getDefinition().attributeValue("titlenotempty");
		if("true".equals(titlenotempty)) {
			try {
				WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), Duration.ofSeconds(IsimoWebProperties.getInstance().isimo.asserttimeout));
				wait.until(titleIsNotEmpty());
				action.log("TitlePredicate: evaluated to true");
				return Pair.of(true, "<notempty>");
			} catch(TimeoutException e) {
				action.log("TitlePredicate: evaluated to false");
				return Pair.of(false, "<notempty>");
			}
		}
		return Pair.of(true, null);
	}
	
	public static ExpectedCondition<Boolean> titleIsNotEmpty() {
	    return new ExpectedCondition<Boolean>() {
	      private String currentTitle = "";

	      @Override
	      public Boolean apply(WebDriver driver) {
	        currentTitle = driver.getTitle();
	        return StringUtils.isNotEmpty(currentTitle);
	      }

	      @Override
	      public String toString() {
	        return String.format("title to be not empty. Current title: \"%s\"", currentTitle);
	      }
	    };
	  }

}
