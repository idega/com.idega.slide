/*
 * $Id: IWSlideSession.java,v 1.3 2004/11/12 16:27:51 joakim Exp $
 * Created on 25.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOSession;


/**
 * 
 *  Last modified: $Date: 2004/11/12 16:27:51 $ by $Author: joakim $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.3 $
 */
public interface IWSlideSession extends IBOSession {

	
	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavServletURI
	 */
	public String getWebdavServletURL() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getIWSlideService
	 */
	public IWSlideService getIWSlideService() throws java.rmi.RemoteException;
	
	public WebdavResource getWebdavResource() throws HttpException, IOException, java.rmi.RemoteException;

	public WebdavResource getWebdavResource(String path) throws HttpException, IOException, java.rmi.RemoteException;
	
	public void close() throws java.rmi.RemoteException;

}
