package com.isimo.core.xml;

import java.nio.file.Path;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;

import com.isimo.core.SpringContext;
import com.isimo.core.TestExecutionManager;

public class LocationAwareElement extends DefaultElement {
	private int lineNumber = -1;
	private Path systemid = null;

    public Path getSystemid() {
		return systemid;
	}

	public LocationAwareElement(QName qname) {
        super(qname);
        this.systemid=SpringContext.getBean(TestExecutionManager.class).getSAXReader().getLastReadRelative();
    }

    public LocationAwareElement(QName qname, int attributeCount) {
        super(qname, attributeCount);
    }

    public LocationAwareElement(String name, Namespace namespace) {
        super(name, namespace);
    }

    public LocationAwareElement(String name) {
        super(name);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj == null)
    		return false;
    	if(!(obj instanceof LocationAwareElement))
    		return false;
    	LocationAwareElement e = (LocationAwareElement) obj;
    	
    	return equalsNullAware(getName(), e.getName()) && equalsNullAware(getNamespace(),e.getNamespace()) && getLineNumber() == e.getLineNumber() && equalsNullAware(getIssue(),e.getIssue()) && equalsNullAware(getSystemid(), e.getSystemid());
    }
    
    boolean equalsNullAware(Object o1, Object o2) {
    	if(o1==null && o2==null)
    		return true;
    	else
    		return o1.equals(o2);
    }
    
    @Override
    public int hashCode() {
    	return getName().hashCode()+getLineNumber()+getIssue().hashCode();
    }
    
    public String getIssue() {
    	String issue = attributeValue("issue");
    	if(issue==null)
    		issue = "";
    	return issue;
    }
    
    @Override
    public Element createCopy() {
    	LocationAwareElement elem = (LocationAwareElement) super.createCopy();
    	elem.setLineNumber(getLineNumber());
    	return elem;
    }
}
