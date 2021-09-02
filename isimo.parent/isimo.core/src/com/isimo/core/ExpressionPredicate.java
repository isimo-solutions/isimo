package com.isimo.core;

import org.apache.commons.lang3.tuple.Pair;

import com.isimo.core.annotations.IsimoPredicate;

@IsimoPredicate
public class ExpressionPredicate extends Predicate<String> {

	@Override
	public Pair<Boolean, String> evaluate(Action action) {
		boolean retval = true;
		String str = null;
		if (action.definition.attribute("expression") != null) {
			Object o = TestExecutionManager.getInstance().evaluateExpression(action);
			TestExecutionManager.getInstance().log("Condition evaluated to " + o.toString(), action);
			retval = ("true".equals(o.toString()));
			str = action.definition.attributeValue("expression");
		}
		return Pair.of(retval, str);
	}

}
