/*
 * $Id: IWSlideService.java,v 1.25 2006/05/24 16:52:33 thomas Exp $
 * Created on May 24, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.event.ContentEvent;
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
 *  Last modified: $Date: 2006/05/24 16:52:33 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.25 $
 */
public interface IWSlideService extends IBOService, IWSlideChangeListener {

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
	public String createUniqueFileName(String scope);

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
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName,
			String fileContentString, String contentType, boolean deletePredecessor) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadXMLFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName,
			String fileContentString) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadXMLFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName,
			String fileContentString, boolean deletePredecessor) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getIWSlideChangeListeners
	 */
	public IWSlideChangeListener[] getIWSlideChangeListeners() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setIWSlideChangeListeners
	 */
	public void setIWSlideChangeListeners(List iwSlideChangeListeners) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#addIWSlideChangeListeners
	 */
	public void addIWSlideChangeListeners(IWSlideChangeListener iwSlideChangeListener) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildCountExcludingFoldersAndHiddenFiles
	 */
	public int getChildCountExcludingFoldersAndHiddenFiles(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderCount
	 */
	public int getChildFolderCount(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildCount
	 */
	public int getChildCount(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#isHiddenFile
	 */
	public boolean isHiddenFile(String fileName);

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsExcludingFoldersAndHiddenFiles
	 */
	public List getChildPathsExcludingFoldersAndHiddenFiles(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderPaths
	 */
	public List getChildFolderPaths(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPaths
	 */
	public List getChildPaths(String folderURI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#invalidateCacheForAllFoldersInURIPath
	 */
	public void invalidateCacheForAllFoldersInURIPath(String URI) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderPathsCacheMap
	 */
	public Map getChildFolderPathsCacheMap() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildFolderPathsCacheMap
	 */
	public void setChildFolderPathsCacheMap(Map childFolderPathsCacheMap) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsCacheMap
	 */
	public Map getChildPathsCacheMap() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildPathsCacheMap
	 */
	public void setChildPathsCacheMap(Map childPathsCacheMap) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsExcludingFolderAndHiddenFilesCacheMap
	 */
	public Map getChildPathsExcludingFolderAndHiddenFilesCacheMap() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildPathsExcludingFolderAndHiddenFilesCacheMap
	 */
	public void setChildPathsExcludingFolderAndHiddenFilesCacheMap(Map childPathsExcludingFolderAndHiddenFilesCacheMap)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#onSlideChange
	 */
	public void onSlideChange(ContentEvent contentEvent); 

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getParentPath
	 */
	public String getParentPath(WebdavResource resource);

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getParentPath
	 */
	public String getParentPath(String resourcePath);
}
