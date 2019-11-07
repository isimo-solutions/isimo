package com.isimo.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.isimo.core.Action;
import com.isimo.core.Include;

public class StackTrace {
	public String scenarioPath;
	public String actionName;
	public int lineNumber;
	public StackTrace parentStackTrace;
	public boolean canStepInto,canStepOver,canStepReturn;
	public List<Variable> variables;
	
	public static StackTrace getStacktraceFromAction(Action action) {
		if(action==null)
			return null;
		StackTrace st = new StackTrace();
		st.actionName = action.getDefinitionOrig().getName();
		st.scenarioPath = action.getDefinitionOrig().getSystemid().toString();
		st.lineNumber = action.getDefinitionOrig().getLineNumber();
		st.parentStackTrace =  getStacktraceFromAction(action.getParent());
		st.canStepInto = action instanceof Include;
		st.canStepOver = true;
		st.canStepReturn = action.getParent() != null;
		st.variables = variableListFromProperties(action.getProperties());
		return st;
	}
	
	static List<Variable> variableListFromProperties(Properties props) {
		List<Variable> retval = new ArrayList<Variable>();
		for(Object key: props.keySet()) {
			String keystr = (String) key;
			retval.add(new Variable(keystr, props.getProperty(keystr)));
		}
		return retval;
	}
}
