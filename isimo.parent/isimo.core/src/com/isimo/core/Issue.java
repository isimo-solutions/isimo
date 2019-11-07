package com.isimo.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isimo.core.xml.LocationAwareElement;

public class Issue {
	public static String FILE = "file", ACTIONNAME = "action", ACTIONNAMESPACE = "nsc", LINENUMBER = "linenumber",ISSUE = "issue"; 
	private Map<String, Object> issueMetadata = new HashMap<String, Object>();
	public static Issue fromLocationAwareElement(LocationAwareElement elem) {
		Issue issue = new Issue();
		issue.issueMetadata.put(FILE, elem.getSystemid().toString());
		issue.issueMetadata.put(ACTIONNAME, elem.getName());
		issue.issueMetadata.put(ACTIONNAMESPACE, elem.getNamespaceURI());
		issue.issueMetadata.put(LINENUMBER, new Integer(elem.getLineNumber()).toString());
		issue.issueMetadata.put(ISSUE, elem.getIssue());
		return issue;
	}
	
	@Override
	public boolean equals(Object is) {
		if(!(is instanceof Issue))
			return false;
		Issue issue = (Issue) is;
		// TODO Auto-generated method stub
		Set<String> keys1 = issueMetadata.keySet();
		Set<String> keys2 = issue.issueMetadata.keySet();
		Set<String> union = new HashSet<String>();
		union.addAll(keys1);
		union.addAll(keys2);
		for(String key: union) {
			if(!valuesEqual(issueMetadata.get(key), issue.issueMetadata.get(key)))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return issueMetadata.hashCode();
	}
	
	boolean valuesEqual(Object obj1, Object obj2) {
		if(obj1 == null && obj2!=null)
			return false; 
		if(obj2 == null && obj1!=null)
			return false; 
		if(obj1 == null && obj2==null)
			return true;
		return obj1.equals(obj2);
	}
	
	public Object getIssue() {
		return issueMetadata.get(ISSUE);
	}
	
	public Object getLineNumber() {
		return issueMetadata.get(LINENUMBER);
	}
	
	public Object getName() {
		return "{"+issueMetadata.get(ACTIONNAMESPACE) +"}"+ issueMetadata.get(ACTIONNAME);
	}
	
	public Object getSystemid() {
		return issueMetadata.get(FILE);
	}
}
