package com.isimo.web;


import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Click extends WebAction {

	Click(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		if(getDefinition().attributeValue("until")!=null) {
			int max = 5;
			String maxstr = getDefinition().attributeValue("max");
			if(maxstr!=null)
				max = Integer.parseInt(maxstr);
			int i = 0;
			while(true) {
				try {
					Function f = negative()?
							invisibilityOf(By.xpath(getDefinition().attributeValue("until"))):
							ExpectedConditions.visibilityOfElementLocated(By.xpath(getDefinition().attributeValue("until")));
					(new WebDriverWait(getDriver(), 1)).until(f);
					log("until path "+getDefinition().attributeValue("until")+" found");
					return;
				} catch (Exception e) {
					log("until path "+getDefinition().attributeValue("until")+" not found");
					i++;
					executeClick();
					if(i >= max)
						break;
					sleepMili(100);
				}
			}
		} else {
			executeClick();
		}
	}
	
	void executeClick() {
		click(this.getBy(), !"false".equals(getDefinition().attributeValue("checkvisible")));
	}

}
