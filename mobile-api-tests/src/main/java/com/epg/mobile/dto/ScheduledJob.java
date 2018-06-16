package com.epg.mobile.dto;

import java.io.Serializable;

import com.epg.mobile.testxml.dto.TestCase;

public class ScheduledJob implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2776728388087448150L;
	
	private String testCaseId;
	
	private Long nextRun;
	
	private TestCase testCase;

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public Long getNextRun() {
		return nextRun;
	}

	public void setNextRun(Long nextRun) {
		this.nextRun = nextRun;
	}
	
	

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testCaseId == null) ? 0 : testCaseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduledJob other = (ScheduledJob) obj;
		if (testCaseId == null) {
			if (other.testCaseId != null)
				return false;
		} else if (!testCaseId.equals(other.testCaseId))
			return false;
		return true;
	}

}
