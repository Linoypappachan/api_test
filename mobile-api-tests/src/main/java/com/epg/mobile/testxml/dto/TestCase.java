package com.epg.mobile.testxml.dto;

import java.io.Serializable;

import com.epg.mobile.testxml.dto.Params;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("test-case")
public class TestCase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7114882723098687555L;
	
	@XStreamAlias("id")
	@XStreamAsAttribute
	private String id;
	
	@XStreamAlias("priority")
	@XStreamAsAttribute
	private Integer priority;
	
	@XStreamAlias("active")
	@XStreamAsAttribute
	private String active;
	
	@XStreamAlias("login")
	@XStreamAsAttribute
	private String login;
	
	@XStreamAlias("session")
	@XStreamAsAttribute
	private String session;
	
	@XStreamAlias("proxy")
	@XStreamAsAttribute
	private String proxy;
	
	@XStreamAlias("name")
	private String name;
	
	@XStreamAlias("description")
	private String description;
	
	@XStreamAlias("schedule")
	private String schedule;
	
	@XStreamAlias("verb")
	private String verb;
	
	@XStreamAlias("url")
	private String url;
	
	@XStreamAlias("params")
	private Params params;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Params getParams() {
		return params;
	}
	public void setParams(Params params) {
		this.params = params;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	@Override
	public String toString() {
		String s = "{\"id\":\"" + id + "\",\"priority\":\"" + priority + "\",\"name\":\"" + name + "\",\"description\":\""
				+ description + "\",\"schedule\":\"" + schedule + "\",\"verb\":\"" + verb + "\",\"url\":\"" + url
				+ "\",\"params\":\"" + params + "\"}";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			s = objectMapper.writeValueAsString(this);
		} catch(Throwable t) {
			
		}
		return s;
	}
}
