/*
 * $Id: LocalResponse.java,v 1.2 2006/04/09 11:44:15 laddi Exp $
 * Created on 21.1.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.webdav.lib.ResponseEntity;

public class LocalResponse implements ResponseEntity{

	int statusCode;
	String href;
	Vector properties;
	Vector histories;
	Vector workspaces;

	/**
	 * @return Returns the histories.
	 */
	public Enumeration getHistories() {
		return this.histories.elements();
	}
	
	/**
	 * @param histories The histories to set.
	 */
	public void setHistories(Vector histories) {
		this.histories = histories;
	}
	
	/**
	 * @return Returns the href.
	 */
	public String getHref() {
		return this.href;
	}
	
	/**
	 * @param href The href to set.
	 */
	public void setHref(String href) {
		this.href = href;
	}
	
	/**
	 * @return Returns the properties.
	 */
	public Enumeration getProperties() {
		return this.properties.elements();
	}
	
	/**
	 * @param properties The properties to set.
	 */
	public void setProperties(Vector properties) {
		this.properties = properties;
	}
	
	/**
	 * @return Returns the statusCode.
	 */
	public int getStatusCode() {
		return this.statusCode;
	}
	
	/**
	 * @param statusCode The statusCode to set.
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * @return Returns the workspaces.
	 */
	public Enumeration getWorkspaces() {
		return this.workspaces.elements();
	}
	
	/**
	 * @param workspaces The workspaces to set.
	 */
	public void setWorkspaces(Vector workspaces) {
		this.workspaces = workspaces;
	}
	
	
}