package com.isimo.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.isimo.core.xml.LocationAwareElement;

public abstract class AtomicAction extends Action {
	public AtomicAction(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent, true);
	}

	@Override
	final void execute() {
		try {
			int timeout = isimoProperties.isimo.actiontimeout;
			try {
				timeout= Integer.parseInt(getDefinition().attributeValue("timeout"));
			} catch(Exception e) {
				//attribute not set or not readable
			}
			testExecutionManager.runAtomicAction(this.parent, this, timeout);
		} catch(Exception e) {
			throw new RuntimeException(getRealCause(e));
		}
	}

	Throwable getRealCause(Throwable e) {
		if(e instanceof ExecutionException && e.getCause()!=null)
			return getRealCause(e.getCause());
		else if(e instanceof TimeoutException && e.getCause()!=null)
			return getRealCause(e.getCause());
		else if(e instanceof RuntimeException && e.getCause()!=null)
			return getRealCause(e.getCause());
		else
			return e;
	}
	
	public void executeAtomic() throws Exception {
		super.execute();
			
	}
}
