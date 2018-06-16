package com.epg.mobile.dto;

import java.io.Serializable;

public class Email implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -671622796274710907L;
	
	private String id;
	private String from;
	private String to;
	private String cc;
	private String subject;
	private String body;
	private Object attachment;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Object getAttachment() {
		return attachment;
	}
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
}
