package com.isimo.web;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Screenshot extends WebAction {
	
	public Screenshot(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		TakesScreenshot driver = ((TakesScreenshot)getDriver());
		if(driver!=null) {
			File scrFile = driver.getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(scrFile, new File(testExecutionManager.getReportDir()+File.separator+getDefinition().attributeValue("file")));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			log("Driver is null, no screenshot");
		}
	}
}
