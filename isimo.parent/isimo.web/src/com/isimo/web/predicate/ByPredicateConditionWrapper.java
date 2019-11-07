package com.isimo.web.predicate;

import org.openqa.selenium.support.ui.ExpectedCondition;

public class ByPredicateConditionWrapper {
	ExpectedCondition condition;
	enum ConditionType { NEGATIVE, LISTTEST, SINGLEELEMENT, BOOLEAN };
	ConditionType type;
	public ByPredicateConditionWrapper() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ByPredicateConditionWrapper(ExpectedCondition condition, ConditionType type) {
		super();
		this.condition = condition;
		this.type = type;
	}


	public ExpectedCondition getCondition() {
		return condition;
	}
	public void setCondition(ExpectedCondition condition) {
		this.condition = condition;
	}
	public ConditionType getType() {
		return type;
	}
	public void setType(ConditionType type) {
		this.type = type;
	}
}
