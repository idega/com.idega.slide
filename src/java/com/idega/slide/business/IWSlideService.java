/*
 * $Id: IWSlideService.java,v 1.8 2004/12/13 13:12:32 gummi Exp $
 * Created on 12.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavFile;
import com.idega.business.IBOService;


/**
 * 
 *  Last modified: $Date: 2004/12/13 13:12:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.8 $
 */
public interface IWSlideService extends IBOService {

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServletURL
	 */
	public String getWebdavServletURL() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(String path) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential, String path)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials, String path)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getRootUserCredentials
	 */
	public UsernamePasswordCredentials getRootUserCredentials() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createSlideSchemas
	 */
	public void createSlideSchemas() throws java.rmi.RemoteException;
}
