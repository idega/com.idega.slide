/*
 * $Id: IWSlideService.java,v 1.20 2005/10/12 22:43:18 tryggvil Exp $
 * Created on 21.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
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
import com.idega.business.IBOService;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;


/**
 * 
 *  Last modified: $Date: 2005/10/12 22:43:18 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.20 $
 */
public interface IWSlideService extends IBOService {

	
	/**
	 * <p>
	 * Gets the URI for the root of the slide repository.
	 * The repository is by default mapped on '/content' under the web application.<br/>
	 * This method returns the context path for the application so if it is e.g. mapped under '/cms' this method returns '/cms/content'.
	 * If the webapplication is mapped on '/' the method returns '/content'
	 * </p>
	 * @param path
	 * @return
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
	public UsernamePasswordCredentials getRootUserCredentials() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createSlideSchemas
	 */
	public void createSlideSchemas() throws java.rmi.RemoteException;

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
	 * @see com.idega.slide.business.IWSlideServiceBean#getURI(String path)
	 */
	public String getURI(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getPath(String uri)
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
	
	public String getUserHomeFolderPath(String loginName);
	
	public String createUniqueFileName(String scope);
	
	public Security getSecurityHelper();
	
	public AccessControlList getAccessControlList(String path) throws HttpException, IOException;
	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException;
	
	public AccessControlList getAccessControlList(String path, WebdavRootResource rResource) throws HttpException, IOException;
	public boolean storeAccessControlList(AccessControlList acl, WebdavRootResource rResource) throws RemoteException, HttpException, IOException;
	
	/**
	 * Creates all the folders in path 
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPath(String path,UsernamePasswordCredentials credentials) throws HttpException, RemoteException, IOException;
	/**
	 * Creates all the folders in path with credentatials of the root/administrator user.
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPathAsRoot(String path) throws HttpException, RemoteException, IOException;
	
	
	/**
	 * <p>
	 * Returns the WebdavResource for the "/" or root of the WebDav server.
	 * </p>
	 * @param credentials
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws RemoteException
	 */
	public WebdavResource getWebdavRootResource(UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException;
	
	public WebdavResource getWebdavResource(String path,UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException;
	
	public WebdavExtendedResource getWebdavExtendedResource(String path,UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException;

}
