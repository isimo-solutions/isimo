package com.isimo.core;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ExpressionEvaluator {
	JexlEngine jexlEngine = new JexlBuilder().create();

	@Bean	
	public JexlEngine jexlEngine() {
		return jexlEngine;
	}
}
