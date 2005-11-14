/*
 * $Id: WebdavLocalResources.java,v 1.1 2005/11/14 17:14:08 tryggvil Exp $
 * Created on 11.10.2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.Enumeration;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;


/**
 * <p>
 * This class is an extension of the standard WebdavResources to perform some common 
 * operations locally (in the jvm) instead of going through http when communicating with
 * the built in WebDav server. This class is experimental only.
 * </p>
 *  Last modified: $Date: 2005/11/14 17:14:08 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class WebdavLocalResources extends WebdavResources {

	/**
	 * 
	 */
	public WebdavLocalResources() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public WebdavLocalResources(WebdavResource resource) {
		super(resource);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#addResource(java.lang.String, org.apache.webdav.lib.WebdavResource)
	 */
	public void addResource(String arg0, WebdavResource arg1) {
		// TODO Auto-generated method stub
		super.addResource(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#addResource(org.apache.webdav.lib.WebdavResource)
	 */
	public void addResource(WebdavResource arg0) {
		// TODO Auto-generated method stub
		super.addResource(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#getResource(java.lang.String)
	 */
	public WebdavResource getResource(String arg0) {
		// TODO Auto-generated method stub
		return super.getResource(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#getResourceNames()
	 */
	public Enumeration getResourceNames() {
		// TODO Auto-generated method stub
		return super.getResourceNames();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#getResources()
	 */
	public Enumeration getResources() {
		// TODO Auto-generated method stub
		return super.getResources();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#isThereResource(org.apache.webdav.lib.WebdavResource)
	 */
	public boolean isThereResource(WebdavResource arg0) {
		// TODO Auto-generated method stub
		return super.isThereResource(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#isThereResourceName(java.lang.String)
	 */
	public boolean isThereResourceName(String arg0) {
		// TODO Auto-generated method stub
		return super.isThereResourceName(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#list()
	 */
	public String[] list() {
		// TODO Auto-generated method stub
		return super.list();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#listResources()
	 */
	public WebdavResource[] listResources() {
		// TODO Auto-generated method stub
		return super.listResources();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#removeAll()
	 */
	public void removeAll() {
		// TODO Auto-generated method stub
		super.removeAll();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#removeResource(java.lang.String)
	 */
	public WebdavResource removeResource(String arg0) {
		// TODO Auto-generated method stub
		return super.removeResource(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResources#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
