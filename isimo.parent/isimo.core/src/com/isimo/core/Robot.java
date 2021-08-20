package com.isimo.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Robot extends AtomicAction {
	public static Clipboard sysClip;

	public Robot(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
		// TODO Auto-generated constructor stub
	}
	
	public static Clipboard getSysclip() {
		if(sysClip==null)
			sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		return sysClip;
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		System.setProperty("java.awt.headless","false");
		java.awt.Robot robot = new java.awt.Robot();
		robot.setAutoWaitForIdle(true);
		if (definition.attribute("texttotype") != null) {
			String text = definition.attributeValue("texttotype");

			StringSelection stringSelection = new StringSelection(text);
			int i = 0;
			while(true) {
				try {
					getSysclip().setContents(stringSelection, stringSelection);
					break;
				} catch(IllegalStateException e) {
					if(i++ > isimoProperties.isimo.actiontimeout)
						throw new RuntimeException(e);
					testExecutionManager.sleep(1);
				}
			}

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
		if ("true".equals(definition.attributeValue("sendenter"))) {
			robot.keyPress(KeyEvent.VK_ENTER);
			testExecutionManager.sleep(0.5f);
			robot.keyRelease(KeyEvent.VK_ENTER);
		}
		if ("true".equals(definition.attributeValue("sendtab"))) {
			robot.keyPress(KeyEvent.VK_TAB);
			testExecutionManager.sleep(0.5f);
			robot.keyRelease(KeyEvent.VK_TAB);
		}
		robot.waitForIdle();
	}
}
