/*
 * $Id: WebdavResourceSession.java,v 1.1 2004/11/05 17:30:36 gummi Exp $
 * Created on 4.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import org.apache.webdav.lib.WebdavResource;


/**
 * Class that provides webdav resource client. After use it is important to call
 * the <code>close</code> method to inform the pooling system that one has stopped 
 * using the resource and the connection can be put in the pool.
 * 
 * 
 *  Last modified: $Date: 2004/11/05 17:30:36 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class WebdavResourceSession {

	private WebdavResource _resource;
	private boolean isInUse = false;
	/**
	 * 
	 */
	WebdavResourceSession(WebdavResource resource) {
		super();
		_resource=resource;
	}
	
	public WebdavResource getWebdavResource(){
		return _resource;
	}
	
	public void close(){
		isInUse = false;
	}
	
	boolean isInUse(){
		return isInUse;
	}
	
	void setAsInUse(){
		isInUse=true;
	}
	
}
