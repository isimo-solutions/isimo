package com.isimo.core.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GenerateTestProperties extends Task {
	private File result;
	private String propertyfiles = "";
	
	public String getPropertyfiles() {
		return propertyfiles;
	}

	public void setPropertyfiles(String pPropertyFiles) {
		propertyfiles = pPropertyFiles;
	}

	public void setResult(File pResult) {
		result = pResult;
	}

	@Override
	public void execute() throws BuildException {
		PropertiesGenerator propsgen = new PropertiesGenerator();
		List<File> files = new ArrayList<File>();
		StringTokenizer st = new StringTokenizer(propertyfiles,",");
		while(st.hasMoreTokens()) {
			files.add(new File(st.nextToken()));
		}
		propsgen.setResultProperties(result);
		propsgen.setSequencePropertyFiles(files);
		propsgen.generateProperties();
	}
}
