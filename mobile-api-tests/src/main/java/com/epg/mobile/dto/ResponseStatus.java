package com.epg.mobile.dto;

import java.io.Serializable;

public class ResponseStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5182698892469679038L;
	
	private String status;
	private String message;
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	
	public ResponseStatus() {}
	public ResponseStatus(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String messge) {
		this.message = messge;
	}
}
