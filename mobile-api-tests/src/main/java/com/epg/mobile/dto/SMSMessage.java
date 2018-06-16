package com.epg.mobile.dto;

import java.io.Serializable;

public class SMSMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1039620321170763510L;
	
	
	private String message;
	
	private String numbers;
	
	private String from;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNumbers() {
		return numbers;
	}

	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	
}
