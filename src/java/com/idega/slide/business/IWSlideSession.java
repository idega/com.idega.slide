/*
 * $Id: IWSlideSession.java,v 1.9 2004/12/21 18:25:29 eiki Exp $
 * Created on 14.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.http.HttpSessionBindingEvent;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.common.SlideToken;
import com.idega.business.IBOSession;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;


/**
 * 
 *  Last modified: $Date: 2004/12/21 18:25:29 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.9 $
 */
public interface IWSlideSession extends IBOSession {

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#valueBound
	 */
	public void valueBound(HttpSessionBindingEvent arg0) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#valueUnbound
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getIWSlideService
	 */
	public IWSlideService getIWSlideService() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavServerURI
	 */
	public String getWebdavServerURI() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserCredentials
	 */
	public UsernamePasswordCredentials getUserCredentials() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavRootResource
	 */
	public WebdavRootResource getWebdavRootResource() throws HttpException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavResource
	 */
	public WebdavExtendedResource getWebdavResource(String path) throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getApplicationServerRelativePath
	 */
	public String getApplicationServerRelativePath(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getExistence
	 */
	public boolean getExistence(String path) throws HttpException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#close
	 */
	public void close() throws java.rmi.RemoteException;

	/**
	 * @param slideToken
	 */
	public void setSlideToken(SlideToken slideToken);
}
