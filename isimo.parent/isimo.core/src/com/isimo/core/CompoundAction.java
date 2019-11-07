package com.isimo.core;

import com.isimo.core.xml.LocationAwareElement;

public abstract class CompoundAction extends Action {
	public CompoundAction(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent, false);
	}
}
