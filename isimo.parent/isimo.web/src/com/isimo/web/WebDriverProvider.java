package com.isimo.web;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.isimo.core.SpringContext;
import com.isimo.core.TestExecutionManager;

@Component
public class WebDriverProvider {
	@Autowired
	IsimoWebProperties isimoWebProperties;
	
	@Autowired
	TestExecutionManager testExecutionManager;
	
	WebDriver driver;
	
	public WebDriver getWebDriver() {
		return driver;
	}
	
	public static WebDriverProvider getInstance() {
		return SpringContext.getBean(WebDriverProvider.class);
	}
	
	public void setWebDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	public JavascriptExecutor getJavascriptExecutor() {
		return (JavascriptExecutor) driver;
	}
	
	public WebDriverWait shortDriverWait() {
		WebDriverWait shortWait = new WebDriverWait(getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		return shortWait;
	}
	
	public void waitForPageLoad() {
		String html1, html2;
		boolean ok = false;
		while(!ok) {
			html1 = getPageSource();
			html2 = getPageSource();
			ok = html1.equals(html2);
			if(!ok)
				testExecutionManager.log("Two page sources not equal", null);
		}
		WebDriverWait wait = new WebDriverWait(WebDriverProvider.getInstance().getWebDriver(), isimoWebProperties.isimo.shorttimeout);
		Function<WebDriver, Boolean> pageLoaded = new Function<WebDriver, Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				testExecutionManager.log("Testing Ready Script...", null);
				String script = getIsReadyScript();
				if(script == null)
					return true;
				try {
					Object scriptResult = ((JavascriptExecutor)input).executeScript(script);
					if(!"ready".equals(scriptResult)) {
						testExecutionManager.log("Page is not ready: "+scriptResult, null);
						Pattern p = Pattern.compile("wait (\\d+)");
						Matcher m = p.matcher(scriptResult.toString());
						if(m.matches()) {
							int sleepSeconds = Integer.parseInt(m.group(1));
							testExecutionManager.sleep(sleepSeconds);
							testExecutionManager.log("Result: "+scriptResult, null);
							return true;
						}
						testExecutionManager.log("Page is not ready: "+scriptResult, null);
						return false;
					}
					return true;
				} catch(Exception e) {
					testExecutionManager.log("Problems with IsReadyScript: "+e.getMessage(), null);
					return false;
				}
				//getIsReadyScript()"return document.readyState").equals("complete");
			}
		};
		wait.until(pageLoaded);
	}
	
	String getPageSource() {
		String pageSource = null;
		int i = 0;
		while(pageSource==null) {
			try {
				//try several times to avoid some temporary runtime exceptions
				pageSource = WebDriverProvider.getInstance().getWebDriver().getPageSource();
			} catch(Exception e) {
				i++;
				if(i>10)
					throw e;
				testExecutionManager.sleep(1);
			}
		}
		return pageSource;
	}
	
	String getIsReadyScript() {
		String script = isimoWebProperties.isimo.ready.script;
		if(StringUtils.isBlank(script) || StringUtils.isEmpty(script))
			return null;
		String contents = "";
		try {
			contents = IOUtils.toString(new FileInputStream(script), "UTF-8");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return contents;
	}


}
