package com.isimo.dependencies;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.dom4j.Document;

public class Scenario {
	private static Logger logger = Logger.getLogger(Scenario.class.getCanonicalName());
	String relativePath;
	File source;
	Document xml;
	Set<Dependency> includingScenarios = new HashSet<Dependency>(); // jest zawierany
	Set<Dependency> includedScenarios = new HashSet<Dependency>(); // zawiera
	boolean notFound = false;
	
	public Scenario() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean getNotFound() {
		return notFound;
	}
	
	public String getRelativePath() {
		return relativePath;
	}
	
	public boolean isRoot() {
		return includingScenarios.isEmpty();
	}
	
	public Set<Scenario> getIncludingRootScenarios() {
		return getIncludingRootScenarios(this);
	}
	
	public Set<Dependency> getIncludingScenarios() {
		return includingScenarios;
	}
	
	public Set<Dependency> getIncludedScenarios() {
		return includedScenarios;
	}
	
	public static Set<Scenario> getIncludingRootScenarios(Scenario s) {
		Set<Scenario> retval = new HashSet<Scenario>();
		if(s.includingScenarios.isEmpty()) {
			retval.add(s);
		} else {
			for(Dependency d: s.includingScenarios) {
				retval.addAll(getIncludingRootScenarios(d.source));
			}
		}
		return retval;
	}
}
