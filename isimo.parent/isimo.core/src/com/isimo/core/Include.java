package com.isimo.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Include extends CompoundAction {

	Include(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	void execute() {
		super.execute();
		prepareLog();
		Element previousParent = testExecutionManager.currentParent;
		try {
			//read all attributes and insert them as properties
			Map<String, Attribute> attrsXml = new HashMap<String, Attribute>();
			List attrs = getDefinition().attributes();
			if(attrs!=null) {
				for(Object attrObj: attrs) {
					Attribute attr = (Attribute) attrObj;
					String name = "attr:"+attr.getName();
					setProperty(name, attr.getValue());
					attrsXml.put(name, attr);
				}
			}
			log("Executing subscenario '"+getDefinition().attributeValue("scenario")+"'");
			testExecutionManager.currentParent = this.log;
			testExecutionManager.executeScenario(getDefinition().attributeValue("scenario"), this, attrsXml);
		} catch(AlreadyLoggedException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		testExecutionManager.currentParent = previousParent;
	}
	
	void prepareLog() {
		log.elements().clear();
	}
}
