/*
 * $Id: IWSlideSession.java,v 1.10 2004/12/29 11:32:16 gimmi Exp $
 * Created on 28.12.2004
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
 *  Last modified: $Date: 2004/12/29 11:32:16 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.10 $
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
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserFullName
	 */
	public String getUserFullName() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserFolderName
	 */
	public String getUserFolderName() throws java.rmi.RemoteException;

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
	 * @see com.idega.slide.business.IWSlideSessionBean#getSlideToken
	 */
	public SlideToken getSlideToken() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#setSlideToken
	 */
	public void setSlideToken(SlideToken slideToken) throws java.rmi.RemoteException;
}
