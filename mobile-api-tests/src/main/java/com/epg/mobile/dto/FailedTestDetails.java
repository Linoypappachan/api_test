package com.epg.mobile.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FailedTestDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2846653404354228232L;
	
	private String testId = "";
	
	private String failedException = "";
	
	private String failedMessage = "";
	
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public String getFailedException() {
		return failedException;
	}
	public void setFailedException(String failedException) {
		this.failedException = failedException;
	}
	public String getFailedMessage() {
		return failedMessage;
	}
	public void setFailedMessage(String failedMessage) {
		this.failedMessage = failedMessage;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
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
		FailedTestDetails other = (FailedTestDetails) obj;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String s = "";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			s = objectMapper.writeValueAsString(this);
		} catch(Throwable t) {
			
		}
		return s;
	}
	
	/*
	completedInfo.setFailedException(t3.getString("full-stacktrace"));
	completedInfo.setFailedMessage(t3.getString("message"));
	*/
}
