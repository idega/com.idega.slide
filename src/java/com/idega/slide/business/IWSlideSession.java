/*
 * $Id: IWSlideSession.java,v 1.2 2004/11/05 17:30:36 gummi Exp $
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
import com.idega.business.IBOSession;


/**
 * 
 *  Last modified: $Date: 2004/11/05 17:30:36 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
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
	
	public WebdavResourceSession getWebdavResource() throws HttpException, IOException, java.rmi.RemoteException;
	
	public void close() throws java.rmi.RemoteException;

}
