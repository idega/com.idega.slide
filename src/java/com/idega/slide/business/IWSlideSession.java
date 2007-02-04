package com.idega.slide.business;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.Privilege;

import com.idega.business.IBOService;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;

public interface IWSlideSession extends IBOService {
	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#valueBound
	 */
	public void valueBound(HttpSessionBindingEvent arg0) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#valueUnbound
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getIWSlideService
	 */
	public IWSlideService getIWSlideService() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserFullName
	 */
	public String getUserFullName() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavServerURI
	 */
	public String getWebdavServerURI() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserCredentials
	 */
	public UsernamePasswordCredentials getUserCredentials() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavRootResource
	 */
	public WebdavRootResource getWebdavRootResource() throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getWebdavResource
	 */
	public WebdavExtendedResource getWebdavResource(String path) throws HttpException, IOException, RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getURL
	 */
	public HttpURL getURL(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getFile
	 */
	public File getFile(String path) throws URIException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getInputStream
	 */
	public InputStream getInputStream(String path) throws IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getOutputStream
	 */
	public OutputStream getOutputStream(File file) throws IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getOutputStream
	 */
	public OutputStream getOutputStream(String path) throws IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getURI
	 */
	public String getURI(String path) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getPath
	 */
	public String getPath(String uri) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getExistence
	 */
	public boolean getExistence(String path) throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#close
	 */
	public void close() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path) throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#getUserHomeFolder
	 */
	public String getUserHomeFolder() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#setSlideToken
	 */
	public void setSlideToken(Object slideToken) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#hasPermission
	 */
	public boolean hasPermission(String resourcePath, Privilege privilege) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#createAllFoldersInPath
	 */
	public boolean createAllFoldersInPath(String path) throws HttpException, RemoteException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideSessionBean#isFolder
	 */
	public boolean isFolder(String folderURI) throws RemoteException;
}