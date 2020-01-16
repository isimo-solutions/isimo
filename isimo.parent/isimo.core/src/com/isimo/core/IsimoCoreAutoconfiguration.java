package com.isimo.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.isimo.core.event.Event;
import com.isimo.core.event.ExecutionListener;

@ComponentScan(basePackageClasses = { TestExecutionManager.class,  IExecutionController.class})
public class IsimoCoreAutoconfiguration {
	@Autowired
	TestExecutionManager testExecutionManager;

	@Qualifier("defaultExecutionController")
	@Autowired
	DefaultExecutionController defaultExecutionController;
	
	@Bean
	@ConditionalOnMissingBean(name = "executionController")
	IExecutionController executionController() {
		return defaultExecutionController;
	}
	

}
