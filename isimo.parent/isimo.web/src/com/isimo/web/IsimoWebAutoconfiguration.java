package com.isimo.web;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.isimo.core.DefaultExecutionController;
import com.isimo.core.IsimoCoreAutoconfiguration;
import com.isimo.core.TestExecutionManager;
import com.isimo.core.model.Model;
import com.isimo.web.predicate.ByPredicate;

@Configuration
@ComponentScan(basePackageClasses = { WebAction.class, ByPredicate.class, Model.class })
public class IsimoWebAutoconfiguration {
	
}
