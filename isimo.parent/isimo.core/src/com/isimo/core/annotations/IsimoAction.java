package com.isimo.core.annotations;

import org.springframework.stereotype.Component;

public @interface IsimoAction {
	String tagname() default "";
}
