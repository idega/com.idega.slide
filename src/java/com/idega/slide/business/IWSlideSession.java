/*
 * $Id: IWSlideSession.java,v 1.15 2005/02/14 14:58:25 gummi Exp $
 * Created on 1.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
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
import com.idega.business.IBOSession;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;


/**
 * 
 *  Last modified: $Date: 2005/02/14 14:58:25 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.15 $
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
	 * @see com.idega.slide.business.IWSlideSessionBean#getURI(String path)
	 */
	public String getURI(String path) throws RemoteException;
	
	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getPath(String uri)
	 */
	public String getPath(String uri) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getExistence
	 */
	public boolean getExistence(String path) throws HttpException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#close
	 */
	public void close() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path) throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserHomeFolder
	 */
	public String getUserHomeFolder() throws java.rmi.RemoteException;
}
