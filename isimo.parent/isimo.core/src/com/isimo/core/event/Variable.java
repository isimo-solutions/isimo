package com.isimo.core.event;

public class Variable implements Comparable {
	private String name;
	private String value;
	public Variable(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Object o) {
		if(o==null || !(o instanceof Variable))
			return -1;
		return name.compareTo(((Variable)o).getName());
	}
	
	
}
