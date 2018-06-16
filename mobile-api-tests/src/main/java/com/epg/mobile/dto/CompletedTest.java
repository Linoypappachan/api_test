package com.epg.mobile.dto;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CompletedTest implements Serializable, Comparable<CompletedTest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7037721981447365059L;
	
	private String testId;
	private String resultLocation;
	private Long endTimestamp;
	private String status;
	private String testDuration;
	private String startTime;
	private String endTime;
	
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public String getResultLocation() {
		return resultLocation;
	}
	public void setResultLocation(String resultLocation) {
		this.resultLocation = resultLocation;
	}
	public Long getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(Long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
		
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
		
	public String getTestDuration() {
		return testDuration;
	}
	public void setTestDuration(String testDuration) {
		this.testDuration = testDuration;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
		CompletedTest other = (CompletedTest) obj;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(CompletedTest o) {
		int result = 0;
		if (this.getEndTimestamp() != null && o.getEndTimestamp() != null) {
			result = ((o.getEndTimestamp() - this.getEndTimestamp()) > 0) 
					? 1 : ((o.getEndTimestamp() - this.getEndTimestamp()) < 0) ? -1 : 0; 
		}
		return result;
	}
	@Override
	public String toString() {
		/*
		String escapedResultLocation = "";
		if (StringUtils.isNotEmpty(resultLocation)) {			
			escapedResultLocation = resultLocation.replace("\\","\\\\");
		}
		return "{\"testId\":\"" + testId + "\",\"resultLocation\":\"" + escapedResultLocation + "\",\"endTimestamp\":\""
				+ endTimestamp + "\",\"status\":\"" + status + "\",\"testCaseObjectString\":\"" + testCaseObjectString
				+ "\"}";
		*/
		if (StringUtils.isNotEmpty(this.resultLocation)) {	
			this.resultLocation = this.resultLocation.replace("\\","\\\\");
		}
		String s = "";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			s = objectMapper.writeValueAsString(this);
		} catch(Throwable t) {
			
		}
		return s;
	}
	
}
