/*
 * $Id: WebdavRootResource.java,v 1.5 2006/04/09 11:44:15 laddi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.Subscription;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.apache.webdav.lib.properties.AclProperty;
import org.apache.webdav.lib.properties.LockDiscoveryProperty;
import org.apache.webdav.lib.properties.PrincipalCollectionSetProperty;
import org.apache.webdav.lib.properties.ResourceTypeProperty;


/**
 * 
 *  Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
 */
public class WebdavRootResource {
	private WebdavResource rootResource = null;
	
	private boolean isClosed = false;
	
	public WebdavRootResource(WebdavResource resource){
		this.rootResource = resource;
		/*try {
			//Set path to root to be sure
			rootResource.setPath(null);
		}
		catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	/**
	 * @return
	 */
	public static int getDefaultAction() {
		return WebdavResource.getDefaultAction();
	}
	/**
	 * @return
	 */
	public static int getDefaultDepth() {
		return WebdavResource.getDefaultDepth();
	}
	/**
	 * @param action
	 */
	public static void setDefaultAction(int action) {
		WebdavResource.setDefaultAction(action);
	}
	/**
	 * @param depth
	 */
	public static void setDefaultDepth(int depth) {
		WebdavResource.setDefaultDepth(depth);
	}
	/**
	 * @return
	 * @throws java.io.IOException
	 */
	public boolean abortTransaction() throws IOException {
		return this.rootResource.abortTransaction();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public AclProperty aclfindMethod() throws HttpException, IOException {
		return this.rootResource.aclfindMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public AclProperty aclfindMethod(String path) throws HttpException, IOException {
		return this.rootResource.aclfindMethod(path);
	}
	/**
	 * @param path
	 * @param aces
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean aclMethod(String path, Ace[] aces) throws HttpException, IOException {
		return this.rootResource.aclMethod(path, aces);
	}
	/**
	 * @param path
	 * @param properties
	 * @param reportType
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration aclReportMethod(String path, Collection properties, int reportType) throws HttpException,
			IOException {
		return this.rootResource.aclReportMethod(path, properties, reportType);
	}
	/**
	 * @param newBinding
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean bindMethod(String newBinding) throws HttpException, IOException {
		return this.rootResource.bindMethod(newBinding);
	}
	/**
	 * @param existingBinding
	 * @param newBinding
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean bindMethod(String existingBinding, String newBinding) throws HttpException, IOException {
		return this.rootResource.bindMethod(existingBinding, newBinding);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean checkinMethod() throws HttpException, IOException {
		return this.rootResource.checkinMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean checkinMethod(String path) throws HttpException, IOException {
		return this.rootResource.checkinMethod(path);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean checkoutMethod() throws HttpException, IOException {
		return this.rootResource.checkoutMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean checkoutMethod(String path) throws HttpException, IOException {
		return this.rootResource.checkoutMethod(path);
	}
	/**
	 * @throws java.io.IOException
	 */
	public void close() throws IOException {
		this.rootResource.close();
		this.isClosed = true;
	}
	/**
	 * @throws java.io.IOException
	 */
	public void closeSession() throws IOException {
		this.rootResource.closeSession();
		this.isClosed = true;
	}
	
	public boolean isClosed(){
		return this.isClosed;
	}
	/**
	 * @return
	 * @throws java.io.IOException
	 */
	public boolean commitTransaction() throws IOException {
		return this.rootResource.commitTransaction();
	}
	/**
	 * @param another
	 * @return
	 */
	public int compareTo(Object another) {
		return this.rootResource.compareTo(another);
	}
	/**
	 * @param another
	 * @return
	 */
	public int compareToWebdavResource(WebdavResource another) {
		return this.rootResource.compareToWebdavResource(another);
	}
	/**
	 * @param destination
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean copyMethod(String destination) throws HttpException, IOException {
		return this.rootResource.copyMethod(destination);
	}
	/**
	 * @param source
	 * @param destination
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean copyMethod(String source, String destination) throws HttpException, IOException {
		return this.rootResource.copyMethod(source, destination);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean deleteMethod() throws HttpException, IOException {
		return this.rootResource.deleteMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean deleteMethod(String path) throws HttpException, IOException {
		return this.rootResource.deleteMethod(path);
	}
	/**
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public void discoverOwnLocks() throws HttpException, IOException {
		this.rootResource.discoverOwnLocks();
	}
	/**
	 * @param owner
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public void discoverOwnLocks(String owner) throws HttpException, IOException {
		this.rootResource.discoverOwnLocks(owner);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return this.rootResource.equals(obj);
	}
	/**
	 * @param client
	 * @param method
	 * @return
	 * @throws java.io.IOException
	 * @throws org.apache.commons.httpclient.HttpException
	 */
	public int executeHttpRequestMethod(HttpClient client, HttpMethod method) throws IOException, HttpException {
		return this.rootResource.executeHttpRequestMethod(client, method);
	}
	/**
	 * @return
	 */
	public boolean exists() {
		return this.rootResource.exists();
	}
	/**
	 * @return
	 */
	public Enumeration getActiveLockOwners() {
		return this.rootResource.getActiveLockOwners();
	}
	/**
	 * @return
	 */
	public Enumeration getAllowedMethods() {
		return this.rootResource.getAllowedMethods();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public WebdavResources getChildResources() throws HttpException, IOException {
		return this.rootResource.getChildResources();
	}
	/**
	 * @return
	 */
	public long getCreationDate() {
		return this.rootResource.getCreationDate();
	}
	/**
	 * @return
	 */
	public Enumeration getDavCapabilities() {
		return this.rootResource.getDavCapabilities();
	}
	/**
	 * @return
	 */
	public String getDisplayName() {
		return this.rootResource.getDisplayName();
	}
	/**
	 * @return
	 */
	public boolean getExistence() {
		return this.rootResource.getExistence();
	}
	/**
	 * @return
	 */
	public boolean getFollowRedirects() {
		return this.rootResource.getFollowRedirects();
	}
	/**
	 * @return
	 */
	public long getGetContentLength() {
		return this.rootResource.getGetContentLength();
	}
	/**
	 * @return
	 */
	public String getGetContentType() {
		return this.rootResource.getGetContentType();
	}
	/**
	 * @return
	 */
	public String getGetEtag() {
		return this.rootResource.getGetEtag();
	}
	/**
	 * @return
	 */
	public long getGetLastModified() {
		return this.rootResource.getGetLastModified();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.URIException
	 */
	public String getHost() throws URIException {
		return this.rootResource.getHost();
	}
	/**
	 * @return
	 */
	public HttpURL getHttpURL() {
		return this.rootResource.getHttpURL();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.URIException
	 */
	public HttpURL getHttpURLExceptForUserInfo() throws URIException {
		return this.rootResource.getHttpURLExceptForUserInfo();
	}
	/**
	 * @return
	 */
	public boolean getIsCollection() {
		return this.rootResource.getIsCollection();
	}
	/**
	 * @return
	 */
	public boolean getIsHidden() {
		return this.rootResource.getIsHidden();
	}
	/**
	 * @return
	 */
	public LockDiscoveryProperty getLockDiscovery() {
		return this.rootResource.getLockDiscovery();
	}
	/**
	 * @param file
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean getMethod(File file) throws HttpException, IOException {
		return this.rootResource.getMethod(file);
	}
	/**
	 * @param path
	 * @param file
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean getMethod(String path, File file) throws HttpException, IOException {
		return this.rootResource.getMethod(path, file);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public InputStream getMethodData() throws HttpException, IOException {
		return this.rootResource.getMethodData();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public InputStream getMethodData(String path) throws HttpException, IOException {
		return this.rootResource.getMethodData(path);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public String getMethodDataAsString() throws HttpException, IOException {
		return this.rootResource.getMethodDataAsString();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public String getMethodDataAsString(String path) throws HttpException, IOException {
		return this.rootResource.getMethodDataAsString(path);
	}
	/**
	 * @return
	 */
	public String getName() {
		return this.rootResource.getName();
	}
	/**
	 * @return
	 */
	public boolean getOverwrite() {
		return this.rootResource.getOverwrite();
	}
	/**
	 * @return
	 */
	public String getOwner() {
		return this.rootResource.getOwner();
	}
	/**
	 * @return
	 */
	public String getPath() {
		return this.rootResource.getPath();
	}
	/**
	 * @return
	 */
	public ResourceTypeProperty getResourceType() {
		return this.rootResource.getResourceType();
	}
	/**
	 * @param httpURL
	 * @return
	 * @throws java.io.IOException
	 */
	public HttpClient getSessionInstance(HttpURL httpURL) throws IOException {
		return this.rootResource.getSessionInstance(httpURL);
	}
	/**
	 * @param httpURL
	 * @param reset
	 * @return
	 * @throws java.io.IOException
	 */
	public HttpClient getSessionInstance(HttpURL httpURL, boolean reset) throws IOException {
		return this.rootResource.getSessionInstance(httpURL, reset);
	}
	/**
	 * @return
	 */
	public int getStatusCode() {
		return this.rootResource.getStatusCode();
	}
	/**
	 * @return
	 */
	public String getStatusMessage() {
		return this.rootResource.getStatusMessage();
	}
	/**
	 * @return
	 */
	public String getSupportedLock() {
		return this.rootResource.getSupportedLock();
	}
	/**
	 * @return
	 * @throws java.io.IOException
	 */
	public String getTransactionHandle() throws IOException {
		return this.rootResource.getTransactionHandle();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.rootResource.hashCode();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean headMethod() throws HttpException, IOException {
		return this.rootResource.headMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean headMethod(String path) throws HttpException, IOException {
		return this.rootResource.headMethod(path);
	}
	/**
	 * @return
	 */
	public boolean isCollection() {
		return this.rootResource.isCollection();
	}
	/**
	 * @return
	 */
	public boolean isLocked() {
		return this.rootResource.isLocked();
	}
	/**
	 * @param labelname
	 * @param type
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean labelMethod(String labelname, int type) throws HttpException, IOException {
		return this.rootResource.labelMethod(labelname, type);
	}
	/**
	 * @param path
	 * @param labelname
	 * @param type
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean labelMethod(String path, String labelname, int type) throws HttpException, IOException {
		return this.rootResource.labelMethod(path, labelname, type);
	}
	/**
	 * @return
	 */
	public String[] list() {
		return this.rootResource.list();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Vector listBasic() throws HttpException, IOException {
		return this.rootResource.listBasic();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public WebdavResource[] listWebdavResources() throws HttpException, IOException {
		return this.rootResource.listWebdavResources();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod() throws HttpException, IOException {
		return this.rootResource.lockDiscoveryPropertyFindMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod(String path) throws HttpException, IOException {
		return this.rootResource.lockDiscoveryPropertyFindMethod(path);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod() throws HttpException, IOException {
		return this.rootResource.lockMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod(String path) throws HttpException, IOException {
		return this.rootResource.lockMethod(path);
	}
	/**
	 * @param owner
	 * @param timeout
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod(String owner, int timeout) throws HttpException, IOException {
		return this.rootResource.lockMethod(owner, timeout);
	}
	/**
	 * @param path
	 * @param owner
	 * @param timeout
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod(String path, String owner, int timeout) throws HttpException, IOException {
		return this.rootResource.lockMethod(path, owner, timeout);
	}
	/**
	 * @param path
	 * @param owner
	 * @param timeout
	 * @param lockType
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod(String path, String owner, int timeout, short lockType) throws HttpException, IOException {
		return this.rootResource.lockMethod(path, owner, timeout, lockType);
	}
	/**
	 * @param path
	 * @param owner
	 * @param timeout
	 * @param lockType
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean lockMethod(String path, String owner, int timeout, short lockType, int depth) throws HttpException,
			IOException {
		return this.rootResource.lockMethod(path, owner, timeout, lockType, depth);
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean mkcolMethod(String path) throws HttpException, IOException {
		return this.rootResource.mkcolMethod(path);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean mkWorkspaceMethod() throws HttpException, IOException {
		return this.rootResource.mkWorkspaceMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean mkWorkspaceMethod(String path) throws HttpException, IOException {
		return this.rootResource.mkWorkspaceMethod(path);
	}
	/**
	 * @param source
	 * @param destination
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean moveMethod(String source, String destination) throws HttpException, IOException {
		return this.rootResource.moveMethod(source, destination);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean optionsMethod() throws HttpException, IOException {
		return this.rootResource.optionsMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean optionsMethod(String path) throws HttpException, IOException {
		return this.rootResource.optionsMethod(path);
	}
	/**
	 * @param path
	 * @param type
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration optionsMethod(String path, int type) throws HttpException, IOException {
		return this.rootResource.optionsMethod(path, type);
	}
	/**
	 * @param path
	 * @param aMethod
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean optionsMethod(String path, String aMethod) throws HttpException, IOException {
		return this.rootResource.optionsMethod(path, aMethod);
	}
	/**
	 * @param httpURL
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration optionsMethod(HttpURL httpURL) throws HttpException, IOException {
		return this.rootResource.optionsMethod(httpURL);
	}
	/**
	 * @param httpURL
	 * @param type
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration optionsMethod(HttpURL httpURL, int type) throws HttpException, IOException {
		return this.rootResource.optionsMethod(httpURL, type);
	}
	/**
	 * @param contentLocation
	 * @param subscriptionId
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean pollMethod(String contentLocation, int subscriptionId) throws HttpException, IOException {
		return this.rootResource.pollMethod(contentLocation, subscriptionId);
	}
	/**
	 * @param subscription
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean pollMethod(Subscription subscription) throws HttpException, IOException {
		return this.rootResource.pollMethod(subscription);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod() throws HttpException, IOException {
		return this.rootResource.principalCollectionSetFindMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod(String path) throws HttpException,
			IOException {
		return this.rootResource.principalCollectionSetFindMethod(path);
	}
	/**
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(int depth) throws HttpException, IOException {
		return this.rootResource.propfindMethod(depth);
	}
	/**
	 * @param depth
	 * @param properties
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(int depth, Vector properties) throws HttpException, IOException {
		return this.rootResource.propfindMethod(depth, properties);
	}
	/**
	 * @param propertyName
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(String propertyName) throws HttpException, IOException {
		return this.rootResource.propfindMethod(propertyName);
	}
	/**
	 * @param path
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(String path, int depth) throws HttpException, IOException {
		return this.rootResource.propfindMethod(path, depth);
	}
	/**
	 * @param path
	 * @param depth
	 * @param properties
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(String path, int depth, Vector properties) throws HttpException, IOException {
		return this.rootResource.propfindMethod(path, depth, properties);
	}
	/**
	 * @param path
	 * @param propertyName
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(String path, String propertyName) throws HttpException, IOException {
		return this.rootResource.propfindMethod(path, propertyName);
	}
	/**
	 * @param path
	 * @param properties
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(String path, Vector properties) throws HttpException, IOException {
		return this.rootResource.propfindMethod(path, properties);
	}
	/**
	 * @param properties
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration propfindMethod(Vector properties) throws HttpException, IOException {
		return this.rootResource.propfindMethod(properties);
	}
	/**
	 * @param propertyName
	 * @param propertyValue
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(String propertyName, String propertyValue, boolean action) throws HttpException,
			IOException {
		return this.rootResource.proppatchMethod(propertyName, propertyValue, action);
	}
	/**
	 * @param path
	 * @param propertyName
	 * @param propertyValue
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(String path, String propertyName, String propertyValue, boolean action)
			throws HttpException, IOException {
		return this.rootResource.proppatchMethod(path, propertyName, propertyValue, action);
	}
	/**
	 * @param path
	 * @param properties
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(String path, Hashtable properties, boolean action) throws HttpException, IOException {
		return this.rootResource.proppatchMethod(path, properties, action);
	}
	/**
	 * @param path
	 * @param propertyName
	 * @param propertyValue
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(String path, PropertyName propertyName, String propertyValue, boolean action)
			throws HttpException, IOException {
		return this.rootResource.proppatchMethod(path, propertyName, propertyValue, action);
	}
	/**
	 * @param properties
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(Hashtable properties, boolean action) throws HttpException, IOException {
		return this.rootResource.proppatchMethod(properties, action);
	}
	/**
	 * @param propertyName
	 * @param propertyValue
	 * @param action
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean proppatchMethod(PropertyName propertyName, String propertyValue, boolean action)
			throws HttpException, IOException {
		return this.rootResource.proppatchMethod(propertyName, propertyValue, action);
	}
	/**
	 * @param data
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(byte[] data) throws HttpException, IOException {
		return this.rootResource.putMethod(data);
	}
	/**
	 * @param file
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(File file) throws HttpException, IOException {
		return this.rootResource.putMethod(file);
	}
	/**
	 * @param is
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(InputStream is) throws HttpException, IOException {
		return this.rootResource.putMethod(is);
	}
	/**
	 * @param data
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String data) throws HttpException, IOException {
		return this.rootResource.putMethod(data);
	}
	/**
	 * @param path
	 * @param data
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String path, byte[] data) throws HttpException, IOException {
		return this.rootResource.putMethod(path, data);
	}
	/**
	 * @param path
	 * @param file
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String path, File file) throws HttpException, IOException {
		return this.rootResource.putMethod(path, file);
	}
	/**
	 * @param path
	 * @param is
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String path, InputStream is) throws HttpException, IOException {
		return this.rootResource.putMethod(path, is);
	}
	/**
	 * @param path
	 * @param data
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String path, String data) throws HttpException, IOException {
		return this.rootResource.putMethod(path, data);
	}
	/**
	 * @param path
	 * @param url
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(String path, URL url) throws HttpException, IOException {
		return this.rootResource.putMethod(path, url);
	}
	/**
	 * @param url
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean putMethod(URL url) throws HttpException, IOException {
		return this.rootResource.putMethod(url);
	}
	/**
	 * @param newBinding
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean rebindMethod(String newBinding) throws HttpException, IOException {
		return this.rootResource.rebindMethod(newBinding);
	}
	/**
	 * @param existingBinding
	 * @param newBinding
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean rebindMethod(String existingBinding, String newBinding) throws HttpException, IOException {
		return this.rootResource.rebindMethod(existingBinding, newBinding);
	}
	/**
	 * @param httpURL
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration reportMethod(HttpURL httpURL, int depth) throws HttpException, IOException {
		return this.rootResource.reportMethod(httpURL, depth);
	}
	/**
	 * @param httpURL
	 * @param sQuery
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration reportMethod(HttpURL httpURL, String sQuery, int depth) throws HttpException, IOException {
		return this.rootResource.reportMethod(httpURL, sQuery, depth);
	}
	/**
	 * @param httpURL
	 * @param properties
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration reportMethod(HttpURL httpURL, Vector properties) throws HttpException, IOException {
		return this.rootResource.reportMethod(httpURL, properties);
	}
	/**
	 * @param httpURL
	 * @param properties
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration reportMethod(HttpURL httpURL, Vector properties, int depth) throws HttpException, IOException {
		return this.rootResource.reportMethod(httpURL, properties, depth);
	}
	/**
	 * @param httpURL
	 * @param properties
	 * @param histUri
	 * @param depth
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Enumeration reportMethod(HttpURL httpURL, Vector properties, Vector histUri, int depth)
			throws HttpException, IOException {
		return this.rootResource.reportMethod(httpURL, properties, histUri, depth);
	}
	/**
	 * @param depth
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public void setProperties(int depth) throws HttpException, IOException {
		this.rootResource.setProperties(depth);
	}
	/**
	 * @param action
	 * @param depth
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public void setProperties(int action, int depth) throws HttpException, IOException {
		this.rootResource.setProperties(action, depth);
	}
	/**
	 * @param path
	 * @param subscriptionId
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean subscribeMethod(String path, int subscriptionId) throws HttpException, IOException {
		return this.rootResource.subscribeMethod(path, subscriptionId);
	}
	/**
	 * @param path
	 * @param notificationType
	 * @param callback
	 * @param notificationDelay
	 * @param depth
	 * @param lifetime
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public Subscription subscribeMethod(String path, String notificationType, String callback, long notificationDelay,
			int depth, long lifetime) throws HttpException, IOException {
		return this.rootResource.subscribeMethod(path, notificationType, callback, notificationDelay, depth, lifetime);
	}
	/**
	 * @param subscription
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean subscribeMethod(Subscription subscription) throws HttpException, IOException {
		return this.rootResource.subscribeMethod(subscription);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.rootResource.toString();
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unbindMethod() throws HttpException, IOException {
		return this.rootResource.unbindMethod();
	}
	/**
	 * @param binding
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unbindMethod(String binding) throws HttpException, IOException {
		return this.rootResource.unbindMethod(binding);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean uncheckoutMethod() throws HttpException, IOException {
		return this.rootResource.uncheckoutMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean uncheckoutMethod(String path) throws HttpException, IOException {
		return this.rootResource.uncheckoutMethod(path);
	}
	/**
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unlockMethod() throws HttpException, IOException {
		return this.rootResource.unlockMethod();
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unlockMethod(String path) throws HttpException, IOException {
		return this.rootResource.unlockMethod(path);
	}
	/**
	 * @param path
	 * @param owner
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unlockMethod(String path, String owner) throws HttpException, IOException {
		return this.rootResource.unlockMethod(path, owner);
	}
	/**
	 * @param path
	 * @param subscriptionId
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unsubscribeMethod(String path, int subscriptionId) throws HttpException, IOException {
		return this.rootResource.unsubscribeMethod(path, subscriptionId);
	}
	/**
	 * @param subscription
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean unsubscribeMethod(Subscription subscription) throws HttpException, IOException {
		return this.rootResource.unsubscribeMethod(subscription);
	}
	/**
	 * @param target
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean updateMethod(String target) throws HttpException, IOException {
		return this.rootResource.updateMethod(target);
	}
	/**
	 * @param path
	 * @param target
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean updateMethod(String path, String target) throws HttpException, IOException {
		return this.rootResource.updateMethod(path, target);
	}
	/**
	 * @param path
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean versionControlMethod(String path) throws HttpException, IOException {
		return this.rootResource.versionControlMethod(path);
	}
	/**
	 * @param path
	 * @param target
	 * @return
	 * @throws org.apache.commons.httpclient.HttpException
	 * @throws java.io.IOException
	 */
	public boolean versionControlMethod(String path, String target) throws HttpException, IOException {
		return this.rootResource.versionControlMethod(path, target);
	}
}
