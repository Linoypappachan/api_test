package com.epg.mobile.testxml.dto;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("params")
public class Params implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -490125698188187945L;
	
	@XStreamAlias("header")
	private List<Param> header;
	
	@XStreamAlias("query")
	private List<Param> query;
	
	@XStreamAlias("formdata")
	private List<Param> form;
	
	@XStreamAlias("url")
	private List<Param> url;

	public List<Param> getHeader() {
		return header;
	}

	public void setHeader(List<Param> header) {
		this.header = header;
	}

	public List<Param> getQuery() {
		return query;
	}

	public void setQuery(List<Param> query) {
		this.query = query;
	}

	public List<Param> getForm() {
		return form;
	}

	public void setForm(List<Param> form) {
		this.form = form;
	}

	public List<Param> getUrl() {
		return url;
	}

	public void setUrl(List<Param> url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{\"header\":\"" + header + "\",\"query\":\"" + query + "\",\"form\":\"" + form + "\",\"url\":\"" + url
				+ "\"}";
	}

}
