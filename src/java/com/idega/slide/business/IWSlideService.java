/*
 * $Id: IWSlideService.java,v 1.21 2006/02/23 18:40:30 eiki Exp $
 * Created on Feb 23, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.rmi.RemoteException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.security.Security;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;


/**
 * 
 *  Last modified: $Date: 2006/02/23 18:40:30 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.21 $
 */
public interface IWSlideService extends IBOService {

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURI
	 */
	public String getWebdavServerURI() throws java.rmi.RemoteException;

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
	public UsernamePasswordCredentials getRootUserCredentials() throws IBOLookupException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createSlideSchemas
	 */
	public void createSlideSchemas() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavRootResource
	 */
	public WebdavResource getWebdavRootResource(UsernamePasswordCredentials credentials) throws HttpException,
			IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResource
	 */
	public WebdavResource getWebdavResource(String path, UsernamePasswordCredentials credentials) throws HttpException,
			IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavExtendedResource
	 */
	public WebdavExtendedResource getWebdavExtendedResource(String path, UsernamePasswordCredentials credentials)
			throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResourceAuthenticatedAsRoot
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path) throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResourceAuthenticatedAsRoot
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getURI
	 */
	public String getURI(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getPath
	 */
	public String getPath(String uri) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getExistence
	 */
	public boolean getExistence(String path) throws HttpException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#generateUserFolders
	 */
	public boolean generateUserFolders(String loginName) throws HttpException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#updateUserFolderPrivileges
	 */
	public void updateUserFolderPrivileges(String loginName) throws IOException, IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path) throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path, WebdavRootResource rResource) throws HttpException,
			IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl, WebdavRootResource rResource) throws HttpException,
			IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAuthenticationBusiness
	 */
	public AuthenticationBusiness getAuthenticationBusiness() throws IBOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getUserHomeFolderPath
	 */
	public String getUserHomeFolderPath(String loginName) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createUniqueFileName
	 */
	public String createUniqueFileName(String scope) ;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getSecurityHelper
	 */
	public Security getSecurityHelper() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createAllFoldersInPath
	 */
	public boolean createAllFoldersInPath(String path, UsernamePasswordCredentials credentials) throws HttpException,
			RemoteException, IOException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createAllFoldersInPathAsRoot
	 */
	public boolean createAllFoldersInPathAsRoot(String path) throws HttpException, RemoteException, IOException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName,
			String fileContentString, String contentType) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadXMLFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName,
			String fileContentString) throws java.rmi.RemoteException;
}
