package com.isimo.core.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.isimo.core.Action;
import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.TestExecutionManager;

public class Model {
	private static Model _instance = null;
	Document model = null;
	Long modelLastModified = Long.MIN_VALUE;
	String modelpath = null;
	public static String MODEL_PATH_PROPERTY_NAME = "isimo.model.path";
	
	private Model(Properties props) {
		try {
			modelpath = props.getProperty(MODEL_PATH_PROPERTY_NAME);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Model(File model) {
		modelpath = model.getAbsolutePath();
		_instance = this;
	}
	
	
	public static Model getInstance() {
		
		if(_instance == null) {
			_instance = new Model(SpringContext.getBean(TestExecutionManager.class).getProperties());
		}
		return _instance;
	}
	
	public Document getModel() {
		File file = new File(modelpath);
		if(file.lastModified() > modelLastModified) {
			try {
				SpringContext.getTestExecutionManager().log("Reading model "+modelpath, null);
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				model = builder.parse(file);
				modelLastModified = file.lastModified();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return model;
	}
	
	
	public String pathExists(String path) {
		List<Map.Entry<String,Element>> defs = ModelValidator.getDefinitionsFromPath(getModel(),path);
		if(defs.isEmpty() || defs.get(defs.size()-1).getValue()==null) {
			StringBuffer retval = new StringBuffer();
			for(Map.Entry<String, Element> def: defs) {
				if(def.getValue()!=null)
					retval.append(def.getKey()+".");
			}
			String ret = retval.toString();
			if(ret.length()!=0)
				return ret.substring(0, ret.length()-1);
			return ret;
		}
		return path;
	}
}
