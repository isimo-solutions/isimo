package com.isimo.core;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Save extends AtomicAction {

	public Save(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		File file = new File(definition.attributeValue("file"));
		log("Saving "+definition.getText()+" to file "+file.getAbsolutePath());
		FileUtils.write(file, definition.getText(),"UTF-8");
	}

}
