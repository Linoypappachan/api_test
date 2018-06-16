package com.epg.mobile.testxml.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("test-cases")
public class TestCases implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5914576785390827100L;

	@XStreamImplicit
	private List<TestCase> testCases = new ArrayList<TestCase>();


	public List<TestCase> getTestCases() {
		return testCases;
	}


	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
}
