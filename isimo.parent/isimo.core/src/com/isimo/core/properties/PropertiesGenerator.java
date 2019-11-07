package com.isimo.core.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class PropertiesGenerator {
	List<File> sequencePropertyFiles = null;
	File inputProperties, resultProperties;
	XProperties props = new XProperties();
	Logger LOGGER = Logger.getLogger(PropertiesGenerator.class.getName());
	boolean initialized = false;

	public PropertiesGenerator() {
	}

	public List<File> getSequencePropertyFiles() {
		return sequencePropertyFiles;
	}

	public void setSequencePropertyFiles(List<File> pSequencePropertyFiles) {
		sequencePropertyFiles = pSequencePropertyFiles;
	}
	
	

	public File getInputProperties() {
		return inputProperties;
	}

	public void setInputProperties(File inputProperties) {
		this.inputProperties = inputProperties;
	}

	public File getResultProperties() {
		return resultProperties;
	}

	public void setResultProperties(File pResultProperties) {
		resultProperties = pResultProperties;
	}
	
	public void setProperty(String name, String value) {
		props.setProperty(name, value);
	}
	
	public void initialize() {
		try {
			readPropertyInputStream(new FileInputStream(inputProperties), props);
			props.putAll(System.getProperties());
			if(getSequencePropertyFiles()==null) {
				InputStream is = this.getClass().getResource("standard.properties").openStream();
				readPropertyInputStream(is, props);
				props.putAll(System.getProperties());
				LOGGER.info("sequence of property files not set, reading from the special property isimo.propertyfiles.sequence");
				setSequencePropertyFiles(new ArrayList<File>());
				StringTokenizer st = new StringTokenizer(props.getProperty("isimo.propertyfiles.sequence"), ",");
				while(st.hasMoreTokens()) {
					getSequencePropertyFiles().add(new File(st.nextToken()));
				}
	
			}
			initialized = true;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void generateProperties() {
		try {
			if(!initialized)
				initialize();
			for(File propertyFile: getSequencePropertyFiles()) {
				readPropertyFile(propertyFile, props);
			}
			if(!resultProperties.getParentFile().exists()) {
				LOGGER.info("Creating directory "+resultProperties.getParentFile()+" as it doesn't exist");
				if(!resultProperties.getParentFile().mkdirs())
					throw new RuntimeException("Failed creating directory "+resultProperties.getParentFile());
			}
			LOGGER.info("Storing properties in the path"+resultProperties);
			props.store(new FileOutputStream(resultProperties), "");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void readPropertyInputStream(InputStream is, XProperties props) throws IOException {
		byte[] buffer = new byte[100000];
		int bytesRead = IOUtils.read(is, buffer);
		File tempFile = File.createTempFile("standard", ".properties");
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(buffer, 0, bytesRead);
		fos.flush();
		readPropertyFile(tempFile, props);
	}
	
	

	public void readPropertyFile(File propertyFile, XProperties props) throws IOException {
		if(!propertyFile.isAbsolute())
			propertyFile = new File(props.get("testdir")+File.separator+propertyFile.toString());
		if (!propertyFile.exists()) {
			LOGGER.warning("Property file " + propertyFile + " doesn't exist");
			return;
		}
		if (!propertyFile.canRead()) {
			LOGGER.warning("Can't read property file " + propertyFile);
			return;
		}
		LOGGER.info("Reading property file " + propertyFile);
		try {
			props.load(new FileInputStream(propertyFile));
		} catch (IOException e) {
			LOGGER.warning("Exception when reading property file " + propertyFile + ": " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		File input = new File(args[0]);
		File result = new File(args[1]);
		List<File> propfiles =null;
		if(args.length==3) {
			propfiles = new ArrayList<File>();
			StringTokenizer propfilesst = new StringTokenizer(args[1], ",");
			while(propfilesst.hasMoreTokens()) {
				propfiles.add(new File(propfilesst.nextToken()));
			}
		}
		PropertiesGenerator gen = new PropertiesGenerator();
		gen.setInputProperties(input);
		gen.setResultProperties(result);
		gen.setSequencePropertyFiles(propfiles);
		gen.generateProperties();
	}
}
