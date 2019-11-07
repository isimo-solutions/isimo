package com.isimo.core;

import org.apache.commons.lang3.tuple.Pair;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Assert extends AtomicAction {
	Assert(LocationAwareElement definition, Action parent) {
		super(definition, parent);
	}

	public void executeAtomic() throws Exception {
		super.executeAtomic();
		Pair<Boolean, Object> result = evaluate();
		if(!result.getLeft()) {
			if("true".equals(getDefinition().attributeValue("terminate")))
				throw new RuntimeException("Terminating Assert: "+result.toString());
			else 
				testExecutionManager.logProblem(result.toString(), this);
		}
	}

}
