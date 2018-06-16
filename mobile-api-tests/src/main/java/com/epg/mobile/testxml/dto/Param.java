package com.epg.mobile.testxml.dto;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("param")
public class Param implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7747627443458883360L;
	
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;
	
	@XStreamAlias("value")
	@XStreamAsAttribute
	private String value;
	
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
	public String toString() {
		return "{\"name\":\"" + name + "\",\"value\":\"" + value + "\"}";
	}
}
