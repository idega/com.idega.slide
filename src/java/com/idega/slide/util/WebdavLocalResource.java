/*
 * $Id: WebdavLocalResource.java,v 1.4 2006/04/09 11:44:15 laddi Exp $
 * Created on 11.10.2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
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
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.SlideTokenImpl;
import org.apache.slide.content.Content;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.Subscription;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.apache.webdav.lib.WebdavState;
import org.apache.webdav.lib.properties.AclProperty;
import org.apache.webdav.lib.properties.LockDiscoveryProperty;
import org.apache.webdav.lib.properties.PrincipalCollectionSetProperty;
import org.apache.webdav.lib.properties.ResourceTypeProperty;
import org.apache.webdav.lib.util.WebdavStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * <p>
 * This class is an extension of the standard WebdavResource to perform some common 
 * operations locally (in the jvm) instead of going through http when communicating with
 * the built in WebDav server. This class is experimental only.
 * </p>
 *  Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class WebdavLocalResource extends WebdavExtendedResource {

	/**
	 * @param urlStr
	 * @param cred
	 * @param followRedirects
	 * @throws IOException
	 */
	public WebdavLocalResource(String urlStr, Credentials cred, boolean followRedirects) throws IOException {
		super(urlStr, cred, followRedirects);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param client
	 */
	public WebdavLocalResource(HttpClient client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param url
	 * @throws HttpException
	 * @throws IOException
	 */
	public WebdavLocalResource(HttpURL url) throws HttpException, IOException {
		super(url);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#createUpdatedResource()
	 */
	public WebdavExtendedResource createUpdatedResource() {
		// TODO Auto-generated method stub
		return super.createUpdatedResource();
	}

	/**
	 * 
	 * Create a new WebdavResource object (as a seperate method so that it can
	 * be overridden by subclasses.)
	 * @param client HttpClient to be used by this webdavresource.
	 * @return A new WebdavResource object.
	 */
	protected WebdavResource createWebdavResource(HttpClient client) {
		WebdavResource resource = new WebdavLocalResource(client);
		resource.setCredentials(this.hostCredentials);
		//        resource.setProxy(proxyHost, proxyPort);
		//        resource.setProxyCredentials(proxyCredentials);
		return resource;
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getCheckedIn()
	 */
	public String getCheckedIn() {
		// TODO Auto-generated method stub
		return super.getCheckedIn();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getCheckedOut()
	 */
	public String getCheckedOut() {
		// TODO Auto-generated method stub
		return super.getCheckedOut();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getComment()
	 */
	public String getComment() {
		// TODO Auto-generated method stub
		return super.getComment();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getCreationDateString()
	 */
	public String getCreationDateString() {
		// TODO Auto-generated method stub
		return super.getCreationDateString();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getDecodedPath()
	 */
	public String getDecodedPath() {
		// TODO Auto-generated method stub
		return super.getDecodedPath();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getEncodedPath()
	 */
	public String getEncodedPath() {
		// TODO Auto-generated method stub
		return super.getEncodedPath();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getParentPath()
	 */
	public String getParentPath() {
		// TODO Auto-generated method stub
		return super.getParentPath();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#getVersionName()
	 */
	public String getVersionName() {
		// TODO Auto-generated method stub
		return super.getVersionName();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#listWithDeltaV()
	 */
	public WebdavResources listWithDeltaV() throws IOException {
		// TODO Auto-generated method stub
		return super.listWithDeltaV();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#processProperty(org.apache.webdav.lib.Property)
	 */
	protected void processProperty(Property property) {
		// TODO Auto-generated method stub
		super.processProperty(property);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#putMethod(java.lang.String, java.io.File, java.lang.String)
	 */
	public void putMethod(String path, File file, String comment) throws IOException {
		// TODO Auto-generated method stub
		super.putMethod(path, file, comment);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setCheckedIn(java.lang.String)
	 */
	protected void setCheckedIn(String value) {
		// TODO Auto-generated method stub
		super.setCheckedIn(value);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setCheckedOut(java.lang.String)
	 */
	protected void setCheckedOut(String value) {
		// TODO Auto-generated method stub
		super.setCheckedOut(value);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		// TODO Auto-generated method stub
		super.setComment(comment);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setCreationDateString(java.lang.String)
	 */
	protected void setCreationDateString(String value) {
		// TODO Auto-generated method stub
		super.setCreationDateString(value);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setFinalHttpURL(org.apache.commons.httpclient.HttpURL)
	 */
	public void setFinalHttpURL(HttpURL url) throws IOException {
		// TODO Auto-generated method stub
		super.setFinalHttpURL(url);
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setProperties()
	 */
	public void setProperties() throws IOException {
		// TODO Auto-generated method stub
		super.setProperties();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.util.WebdavExtendedResource#setVersionName(java.lang.String)
	 */
	protected void setVersionName(String value) {
		// TODO Auto-generated method stub
		super.setVersionName(value);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#generateTransactionHeader(org.apache.commons.httpclient.HttpMethod)
	 */
	protected void generateTransactionHeader(HttpMethod arg0) {
		// TODO Auto-generated method stub
		super.generateTransactionHeader(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#generateIfHeader(org.apache.commons.httpclient.HttpMethod)
	 */
	protected void generateIfHeader(HttpMethod arg0) {
		// TODO Auto-generated method stub
		super.generateIfHeader(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#generateAdditionalHeaders(org.apache.commons.httpclient.HttpMethod)
	 */
	protected void generateAdditionalHeaders(HttpMethod arg0) {
		// TODO Auto-generated method stub
		super.generateAdditionalHeaders(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#parseDate(java.lang.String)
	 */
	protected Date parseDate(String arg0) {
		// TODO Auto-generated method stub
		return super.parseDate(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setNameProperties(int)
	 */
	protected void setNameProperties(int arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setNameProperties(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setBasicProperties(int)
	 */
	protected void setBasicProperties(int arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setBasicProperties(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setDefaultProperties(int)
	 */
	protected void setDefaultProperties(int arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setDefaultProperties(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setNamedProp(int, java.util.Vector)
	 */
	protected void setNamedProp(int arg0, Vector arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setNamedProp(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setFollowRedirects(boolean)
	 */
	public void setFollowRedirects(boolean arg0) {
		// TODO Auto-generated method stub
		super.setFollowRedirects(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getFollowRedirects()
	 */
	public boolean getFollowRedirects() {
		// TODO Auto-generated method stub
		return super.getFollowRedirects();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#isTheClient()
	 */
	protected synchronized boolean isTheClient() throws URIException {
		// TODO Auto-generated method stub
		return super.isTheClient();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setClient()
	 */
	protected void setClient() throws IOException {
		// TODO Auto-generated method stub
		super.setClient();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setClient(org.apache.commons.httpclient.HttpURL)
	 */
	protected synchronized void setClient(HttpURL arg0) throws IOException {
		// TODO Auto-generated method stub
		super.setClient(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL, int, int)
	 */
	public void setHttpURL(HttpURL arg0, int arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL, int)
	 */
	public void setHttpURL(HttpURL arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL, java.lang.String, int, int)
	 */
	public void setHttpURL(HttpURL arg0, String arg1, int arg2, int arg3) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL, java.lang.String, int)
	 */
	public void setHttpURL(HttpURL arg0, String arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL, java.lang.String)
	 */
	public void setHttpURL(HttpURL arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(org.apache.commons.httpclient.HttpURL)
	 */
	public void setHttpURL(HttpURL arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setHttpURL(java.lang.String)
	 */
	public void setHttpURL(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setHttpURL(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getHttpURL()
	 */
	public HttpURL getHttpURL() {
		// TODO Auto-generated method stub
		return super.getHttpURL();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getHttpURLExceptForUserInfo()
	 */
	public HttpURL getHttpURLExceptForUserInfo() throws URIException {
		// TODO Auto-generated method stub
		return super.getHttpURLExceptForUserInfo();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setPath(java.lang.String)
	 */
	public void setPath(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setPath(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getPath()
	 */
	public String getPath() {
		// TODO Auto-generated method stub
		return super.getPath();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getHost()
	 */
	public String getHost() throws URIException {
		// TODO Auto-generated method stub
		return super.getHost();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setUserInfo(java.lang.String, java.lang.String)
	 */
	public void setUserInfo(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setUserInfo(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#addRequestHeader(java.lang.String, java.lang.String)
	 */
	public void addRequestHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		super.addRequestHeader(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getDisplayName()
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return super.getDisplayName();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setDisplayName(java.lang.String)
	 */
	protected void setDisplayName(String arg0) {
		// TODO Auto-generated method stub
		super.setDisplayName(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getGetContentLength()
	 */
	public long getGetContentLength() {
		// TODO Auto-generated method stub
		return super.getGetContentLength();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetContentLength(long)
	 */
	protected void setGetContentLength(long arg0) {
		// TODO Auto-generated method stub
		super.setGetContentLength(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetContentLength(java.lang.String)
	 */
	protected void setGetContentLength(String arg0) {
		// TODO Auto-generated method stub
		super.setGetContentLength(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getResourceType()
	 */
	public ResourceTypeProperty getResourceType() {
		// TODO Auto-generated method stub
		return super.getResourceType();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setResourceType(org.apache.webdav.lib.properties.ResourceTypeProperty)
	 */
	protected void setResourceType(ResourceTypeProperty arg0) {
		// TODO Auto-generated method stub
		super.setResourceType(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#isCollection()
	 */
	public boolean isCollection() {
		// TODO Auto-generated method stub
		return super.isCollection();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getGetContentType()
	 */
	public String getGetContentType() {
		// TODO Auto-generated method stub
		return super.getGetContentType();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetContentType(java.lang.String)
	 */
	protected void setGetContentType(String arg0) {
		// TODO Auto-generated method stub
		super.setGetContentType(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		super.setContentType(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getGetLastModified()
	 */
	public long getGetLastModified() {
		// TODO Auto-generated method stub
		return super.getGetLastModified();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetLastModified(long)
	 */
	protected void setGetLastModified(long arg0) {
		// TODO Auto-generated method stub
		super.setGetLastModified(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetLastModified(java.lang.String)
	 */
	protected void setGetLastModified(String arg0) {
		// TODO Auto-generated method stub
		super.setGetLastModified(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getCreationDate()
	 */
	public long getCreationDate() {
		// TODO Auto-generated method stub
		return super.getCreationDate();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setCreationDate(long)
	 */
	protected void setCreationDate(long arg0) {
		// TODO Auto-generated method stub
		super.setCreationDate(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setCreationDate(java.lang.String)
	 */
	protected void setCreationDate(String arg0) {
		// TODO Auto-generated method stub
		super.setCreationDate(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getGetEtag()
	 */
	public String getGetEtag() {
		// TODO Auto-generated method stub
		return super.getGetEtag();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setGetEtag(java.lang.String)
	 */
	protected void setGetEtag(String arg0) {
		// TODO Auto-generated method stub
		super.setGetEtag(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getOwner()
	 */
	public String getOwner() {
		// TODO Auto-generated method stub
		return super.getOwner();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getSupportedLock()
	 */
	public String getSupportedLock() {
		// TODO Auto-generated method stub
		return super.getSupportedLock();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setSupportedLock(java.lang.String)
	 */
	protected void setSupportedLock(String arg0) {
		// TODO Auto-generated method stub
		super.setSupportedLock(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getLockDiscovery()
	 */
	public LockDiscoveryProperty getLockDiscovery() {
		// TODO Auto-generated method stub
		return super.getLockDiscovery();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setLockDiscovery(org.apache.webdav.lib.properties.LockDiscoveryProperty)
	 */
	protected void setLockDiscovery(LockDiscoveryProperty arg0) {
		// TODO Auto-generated method stub
		super.setLockDiscovery(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getActiveLockOwners()
	 */
	public Enumeration getActiveLockOwners() {
		// TODO Auto-generated method stub
		return super.getActiveLockOwners();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#isLocked()
	 */
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return super.isLocked();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getIsHidden()
	 */
	public boolean getIsHidden() {
		// TODO Auto-generated method stub
		return super.getIsHidden();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setIsHidden(boolean)
	 */
	protected void setIsHidden(boolean arg0) {
		// TODO Auto-generated method stub
		super.setIsHidden(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setIsHidden(java.lang.String)
	 */
	protected void setIsHidden(String arg0) {
		// TODO Auto-generated method stub
		super.setIsHidden(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getIsCollection()
	 */
	public boolean getIsCollection() {
		// TODO Auto-generated method stub
		return super.getIsCollection();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setIsCollection(boolean)
	 */
	protected void setIsCollection(boolean arg0) {
		// TODO Auto-generated method stub
		super.setIsCollection(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setIsCollection(java.lang.String)
	 */
	protected void setIsCollection(String arg0) {
		// TODO Auto-generated method stub
		super.setIsCollection(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setProperties(int, int)
	 */
	public void setProperties(int arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setProperties(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setProperties(int)
	 */
	public void setProperties(int arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setProperties(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#refresh()
	 */
	protected void refresh() throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.refresh();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#exists()
	 */
	public boolean exists() {
		// TODO Auto-generated method stub
		return super.exists();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setExistence(boolean)
	 */
	protected void setExistence(boolean arg0) {
		// TODO Auto-generated method stub
		super.setExistence(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getExistence()
	 */
	public boolean getExistence() {
		// TODO Auto-generated method stub
		return super.getExistence();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setOverwrite(boolean)
	 */
	public void setOverwrite(boolean arg0) {
		// TODO Auto-generated method stub
		super.setOverwrite(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getOverwrite()
	 */
	public boolean getOverwrite() {
		// TODO Auto-generated method stub
		return super.getOverwrite();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub
		super.close();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getStatusMessage()
	 */
	public String getStatusMessage() {
		// TODO Auto-generated method stub
		return super.getStatusMessage();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getStatusCode()
	 */
	public int getStatusCode() {
		// TODO Auto-generated method stub
		return super.getStatusCode();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setStatusCode(int)
	 */
	protected void setStatusCode(int arg0) {
		// TODO Auto-generated method stub
		super.setStatusCode(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setStatusCode(int, java.lang.String)
	 */
	protected void setStatusCode(int arg0, String arg1) {
		// TODO Auto-generated method stub
		super.setStatusCode(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getAllowedMethods()
	 */
	public Enumeration getAllowedMethods() {
		// TODO Auto-generated method stub
		return super.getAllowedMethods();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getDavCapabilities()
	 */
	public Enumeration getDavCapabilities() {
		// TODO Auto-generated method stub
		return super.getDavCapabilities();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getChildResources()
	 */
	public WebdavResources getChildResources() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getChildResources();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#listWebdavResources()
	 */
	public WebdavResource[] listWebdavResources() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.listWebdavResources();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#list()
	 */
	public String[] list() {
		// TODO Auto-generated method stub
		return super.list();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#listBasic()
	 */
	public Vector listBasic() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.listBasic();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setEncodeURLs(boolean)
	 */
	public void setEncodeURLs(boolean arg0) {
		// TODO Auto-generated method stub
		super.setEncodeURLs(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#retrieveSessionInstance()
	 */
	public HttpClient retrieveSessionInstance() throws IOException {
		// TODO Auto-generated method stub
		return super.retrieveSessionInstance();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#executeHttpRequestMethod(org.apache.commons.httpclient.HttpClient, org.apache.commons.httpclient.HttpMethod)
	 */
	public int executeHttpRequestMethod(HttpClient arg0, HttpMethod arg1) throws IOException, HttpException {
		// TODO Auto-generated method stub
		return super.executeHttpRequestMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#aclMethod(java.lang.String, org.apache.webdav.lib.Ace[])
	 */
	public boolean aclMethod(String arg0, Ace[] arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.aclMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#aclfindMethod()
	 */
	public AclProperty aclfindMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.aclfindMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#aclfindMethod(java.lang.String)
	 */
	public AclProperty aclfindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.aclfindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#principalCollectionSetFindMethod()
	 */
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.principalCollectionSetFindMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#principalCollectionSetFindMethod(java.lang.String)
	 */
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.principalCollectionSetFindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockDiscoveryPropertyFindMethod()
	 */
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockDiscoveryPropertyFindMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockDiscoveryPropertyFindMethod(java.lang.String)
	 */
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockDiscoveryPropertyFindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethodData()
	 */
	public InputStream getMethodData() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethodData();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethodData(java.lang.String)
	 */
	public InputStream getMethodData(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethodData(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethodDataAsString()
	 */
	public String getMethodDataAsString() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethodDataAsString();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethodDataAsString(java.lang.String)
	 */
	public String getMethodDataAsString(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethodDataAsString(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethod(java.io.File)
	 */
	public boolean getMethod(File arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getMethod(java.lang.String, java.io.File)
	 */
	public boolean getMethod(String arg0, File arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.getMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(byte[])
	 */
	public boolean putMethod(byte[] arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String, byte[])
	 */
	public boolean putMethod(String arg0, byte[] arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.io.InputStream)
	 */
	public boolean putMethod(InputStream arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String, java.io.InputStream)
	 */
	public boolean putMethod(String arg0, InputStream arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String)
	 */
	public boolean putMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String, java.lang.String)
	 */
	public boolean putMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.io.File)
	 */
	public boolean putMethod(File arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String, java.io.File)
	 */
	public boolean putMethod(String arg0, File arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.net.URL)
	 */
	public boolean putMethod(URL arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#putMethod(java.lang.String, java.net.URL)
	 */
	public boolean putMethod(String arg0, URL arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.putMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod()
	 */
	public boolean optionsMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod(java.lang.String)
	 */
	public boolean optionsMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod(java.lang.String, java.lang.String)
	 */
	public boolean optionsMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod(org.apache.commons.httpclient.HttpURL)
	 */
	public Enumeration optionsMethod(HttpURL arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod(org.apache.commons.httpclient.HttpURL, int)
	 */
	public Enumeration optionsMethod(HttpURL arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#optionsMethod(java.lang.String, int)
	 */
	public Enumeration optionsMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.optionsMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#labelMethod(java.lang.String, int)
	 */
	public boolean labelMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.labelMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#labelMethod(java.lang.String, java.lang.String, int)
	 */
	public boolean labelMethod(String arg0, String arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.labelMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#reportMethod(org.apache.commons.httpclient.HttpURL, int)
	 */
	public Enumeration reportMethod(HttpURL arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.reportMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#reportMethod(org.apache.commons.httpclient.HttpURL, java.util.Vector)
	 */
	public Enumeration reportMethod(HttpURL arg0, Vector arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.reportMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#reportMethod(org.apache.commons.httpclient.HttpURL, java.util.Vector, int)
	 */
	public Enumeration reportMethod(HttpURL arg0, Vector arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.reportMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#reportMethod(org.apache.commons.httpclient.HttpURL, java.util.Vector, java.util.Vector, int)
	 */
	public Enumeration reportMethod(HttpURL arg0, Vector arg1, Vector arg2, int arg3) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.reportMethod(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#reportMethod(org.apache.commons.httpclient.HttpURL, java.lang.String, int)
	 */
	public Enumeration reportMethod(HttpURL arg0, String arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.reportMethod(arg0, arg1, arg2);
	}


	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(int, java.util.Vector)
	 */
	public Enumeration propfindMethod(int arg0, Vector arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.propfindMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(java.lang.String)
	 */
	public Enumeration propfindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.propfindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(java.lang.String, java.lang.String)
	 */
	public Enumeration propfindMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.propfindMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(java.util.Vector)
	 */
	public Enumeration propfindMethod(Vector arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.propfindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(java.lang.String, java.util.Vector)
	 */
	public Enumeration propfindMethod(String arg0, Vector arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.propfindMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.lang.String)
	 */
	public boolean proppatchMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.lang.String, boolean)
	 */
	public boolean proppatchMethod(String arg0, String arg1, boolean arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(org.apache.webdav.lib.PropertyName, java.lang.String)
	 */
	public boolean proppatchMethod(PropertyName arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(org.apache.webdav.lib.PropertyName, java.lang.String, boolean)
	 */
	public boolean proppatchMethod(PropertyName arg0, String arg1, boolean arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean proppatchMethod(String arg0, String arg1, String arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public boolean proppatchMethod(String arg0, String arg1, String arg2, boolean arg3) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, org.apache.webdav.lib.PropertyName, java.lang.String)
	 */
	public boolean proppatchMethod(String arg0, PropertyName arg1, String arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, org.apache.webdav.lib.PropertyName, java.lang.String, boolean)
	 */
	public boolean proppatchMethod(String arg0, PropertyName arg1, String arg2, boolean arg3) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.util.Hashtable)
	 */
	public boolean proppatchMethod(Hashtable arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.util.Hashtable, boolean)
	 */
	public boolean proppatchMethod(Hashtable arg0, boolean arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.util.Hashtable)
	 */
	public boolean proppatchMethod(String arg0, Hashtable arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#proppatchMethod(java.lang.String, java.util.Hashtable, boolean)
	 */
	public boolean proppatchMethod(String arg0, Hashtable arg1, boolean arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#headMethod()
	 */
	public boolean headMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.headMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#headMethod(java.lang.String)
	 */
	public boolean headMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.headMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#deleteMethod()
	 */
	public boolean deleteMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.deleteMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#deleteMethod(java.lang.String)
	 */
	public boolean deleteMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.deleteMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#moveMethod(java.lang.String)
	 */
	public boolean moveMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.moveMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#moveMethod(java.lang.String, java.lang.String)
	 */
	public boolean moveMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.moveMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#copyMethod(java.lang.String)
	 */
	public boolean copyMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.copyMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#copyMethod(java.lang.String, java.lang.String)
	 */
	public boolean copyMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.copyMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#mkcolMethod()
	 */
	public boolean mkcolMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.mkcolMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#mkcolMethod(java.lang.String)
	 */
	public boolean mkcolMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.mkcolMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod()
	 */
	public boolean lockMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, int)
	 */
	public boolean lockMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String)
	 */
	public boolean lockMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, java.lang.String, int)
	 */
	public boolean lockMethod(String arg0, String arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, java.lang.String, int, short)
	 */
	public boolean lockMethod(String arg0, String arg1, int arg2, short arg3) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, java.lang.String, int, short, int)
	 */
	public boolean lockMethod(String arg0, String arg1, int arg2, short arg3, int arg4) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1, arg2, arg3, arg4);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, short)
	 */
	public boolean lockMethod(String arg0, short arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#lockMethod(java.lang.String, java.lang.String, short)
	 */
	public boolean lockMethod(String arg0, String arg1, short arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.lockMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#startTransaction(java.lang.String, int)
	 */
	public boolean startTransaction(String arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return super.startTransaction(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#getTransactionHandle()
	 */
	public String getTransactionHandle() throws IOException {
		// TODO Auto-generated method stub
		return super.getTransactionHandle();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#commitTransaction()
	 */
	public boolean commitTransaction() throws IOException {
		// TODO Auto-generated method stub
		return super.commitTransaction();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#abortTransaction()
	 */
	public boolean abortTransaction() throws IOException {
		// TODO Auto-generated method stub
		return super.abortTransaction();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#endTransaction(java.lang.String, int)
	 */
	protected boolean endTransaction(String arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return super.endTransaction(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unlockMethod()
	 */
	public boolean unlockMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unlockMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unlockMethod(java.lang.String)
	 */
	public boolean unlockMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unlockMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unlockMethod(java.lang.String, java.lang.String)
	 */
	public boolean unlockMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unlockMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#discoverOwnLocks()
	 */
	public void discoverOwnLocks() throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.discoverOwnLocks();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#discoverOwnLocks(java.lang.String)
	 */
	public void discoverOwnLocks(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.discoverOwnLocks(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#discoverLock(java.lang.String, java.lang.String, org.apache.webdav.lib.WebdavState)
	 */
	protected WebdavState discoverLock(String arg0, String arg1, WebdavState arg2) {
		// TODO Auto-generated method stub
		return super.discoverLock(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#updateMethod(java.lang.String)
	 */
	public boolean updateMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.updateMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#updateMethod(java.lang.String, java.lang.String)
	 */
	public boolean updateMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.updateMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#versionControlMethod(java.lang.String)
	 */
	public boolean versionControlMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.versionControlMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#versionControlMethod(java.lang.String, java.lang.String)
	 */
	public boolean versionControlMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.versionControlMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#mkWorkspaceMethod()
	 */
	public boolean mkWorkspaceMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.mkWorkspaceMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#mkWorkspaceMethod(java.lang.String)
	 */
	public boolean mkWorkspaceMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.mkWorkspaceMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#compareToWebdavResource(org.apache.webdav.lib.WebdavResource)
	 */
	public int compareToWebdavResource(WebdavResource arg0) {
		// TODO Auto-generated method stub
		return super.compareToWebdavResource(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return super.compareTo(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#checkinMethod()
	 */
	public boolean checkinMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.checkinMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#checkinMethod(java.lang.String)
	 */
	public boolean checkinMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.checkinMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#checkoutMethod()
	 */
	public boolean checkoutMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.checkoutMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#checkoutMethod(java.lang.String)
	 */
	public boolean checkoutMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.checkoutMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#uncheckoutMethod()
	 */
	public boolean uncheckoutMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.uncheckoutMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#uncheckoutMethod(java.lang.String)
	 */
	public boolean uncheckoutMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.uncheckoutMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#aclReportMethod(java.lang.String, java.util.Collection, int)
	 */
	public Enumeration aclReportMethod(String arg0, Collection arg1, int arg2) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.aclReportMethod(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#bindMethod(java.lang.String)
	 */
	public boolean bindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.bindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#bindMethod(java.lang.String, java.lang.String)
	 */
	public boolean bindMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.bindMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unbindMethod()
	 */
	public boolean unbindMethod() throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unbindMethod();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unbindMethod(java.lang.String)
	 */
	public boolean unbindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unbindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#rebindMethod(java.lang.String)
	 */
	public boolean rebindMethod(String arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.rebindMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#rebindMethod(java.lang.String, java.lang.String)
	 */
	public boolean rebindMethod(String arg0, String arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.rebindMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#subscribeMethod(java.lang.String, java.lang.String, java.lang.String, long, int, long)
	 */
	public Subscription subscribeMethod(String arg0, String arg1, String arg2, long arg3, int arg4, long arg5) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.subscribeMethod(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#subscribeMethod(java.lang.String, int)
	 */
	public boolean subscribeMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.subscribeMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#subscribeMethod(org.apache.webdav.lib.Subscription)
	 */
	public boolean subscribeMethod(Subscription arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.subscribeMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unsubscribeMethod(java.lang.String, int)
	 */
	public boolean unsubscribeMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unsubscribeMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#unsubscribeMethod(org.apache.webdav.lib.Subscription)
	 */
	public boolean unsubscribeMethod(Subscription arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.unsubscribeMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#pollMethod(java.lang.String, int)
	 */
	public boolean pollMethod(String arg0, int arg1) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.pollMethod(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#pollMethod(org.apache.webdav.lib.Subscription)
	 */
	public boolean pollMethod(Subscription arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		return super.pollMethod(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#setDebug(int)
	 */
	public void setDebug(int arg0) {
		// TODO Auto-generated method stub
		super.setDebug(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#getSessionInstance(org.apache.commons.httpclient.HttpURL)
	 */
	public HttpClient getSessionInstance(HttpURL arg0) throws IOException {
		// TODO Auto-generated method stub
		return super.getSessionInstance(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#getSessionInstance(org.apache.commons.httpclient.HttpURL, boolean)
	 */
	public HttpClient getSessionInstance(HttpURL arg0, boolean arg1) throws IOException {
		// TODO Auto-generated method stub
		return super.getSessionInstance(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#setCredentials(org.apache.commons.httpclient.Credentials)
	 */
	public void setCredentials(Credentials arg0) {
		// TODO Auto-generated method stub
		super.setCredentials(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#setProxy(java.lang.String, int)
	 */
	public void setProxy(String arg0, int arg1) {
		// TODO Auto-generated method stub
		super.setProxy(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#setProxyCredentials(org.apache.commons.httpclient.Credentials)
	 */
	public void setProxyCredentials(Credentials arg0) {
		// TODO Auto-generated method stub
		super.setProxyCredentials(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#closeSession()
	 */
	public void closeSession() throws IOException {
		// TODO Auto-generated method stub
		super.closeSession();
	}

	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavSession#closeSession(org.apache.commons.httpclient.HttpClient)
	 */
	public synchronized void closeSession(HttpClient arg0) throws IOException {
		// TODO Auto-generated method stub
		super.closeSession(arg0);
	}
	
	//Special overridden low-level methods:
	
    /**
     * Set all properties for this resource.
     *
     * @param depth The depth
     */
    protected void setAllProp(int depth)
        throws HttpException, IOException {
    		
    		//super.setAllProp(arg0);
    	
        Enumeration responses = propfindMethod(depth);
        setWebdavProperties(responses);
    }
	
    /** Default Namespace */
    private NamespaceAccessToken namespace;
    /** Security Token */
    private SlideToken token;
    
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#propfindMethod(java.lang.String, int)
	 */
	public Enumeration propfindMethod(String path, int depth) throws HttpException, IOException {
		return propfindMethod(path,depth,null);
	}
		
	public Enumeration propfindMethod(String path, int depth,Vector presetProperties) throws HttpException, IOException {
		// TODO Auto-generated method stub
		//return super.propfindMethod(path, depth,presetProperties);
	
        this.namespace = Domain.accessNamespace(new SecurityToken(""), Domain.getDefaultNamespace());
        String userPrincipal = "root";
        this.token = new SlideTokenImpl(new CredentialsToken(userPrincipal));
        this.token.setForceStoreEnlistment(true);
        String resourcePath = getPath();
        String contextPath = "/uppsala";
        String servletPath = "/content";
        if(resourcePath.startsWith(contextPath)){
        		resourcePath = resourcePath.substring(contextPath.length());
        }
        if(resourcePath.startsWith(servletPath)){
        		resourcePath = resourcePath.substring(servletPath.length());
        }
        
        Vector responses = new Vector();
        LocalResponse response = new LocalResponse();
        response.setHref(path);
        responses.add(response);
        try {
			this.namespace.begin();
	        try {
	            Content c = this.namespace.getContentHelper();
	            NodeRevisionDescriptors revs = c.retrieve(this.token, resourcePath);
	            NodeRevisionDescriptor rev = c.retrieve(this.token, revs, revs
	                    .getLatestRevision());
	            Enumeration e = rev.enumerateProperties();
	            Vector properties = new Vector();
	            while (e.hasMoreElements()) {
	                NodeProperty p = (NodeProperty) e.nextElement();
	                //System.out.println(p.getName()+" = "+p.getValue());
	                String localName = p.getPropertyName().getName();
	                //else if (property.getLocalName().equals(RESOURCETYPE)) {
	                Property property=null;
	                if(localName.equals(RESOURCETYPE)){
	                
	                    DocumentBuilderFactory factory =
	                        DocumentBuilderFactory.newInstance();
	                    factory.setNamespaceAware(true);
	                    factory.setValidating(false);
	                    DocumentBuilder builder = factory.newDocumentBuilder();
		                Object oValue = p.getValue();
		                String value=null;
		                if(oValue!=null){
		                	value=oValue.toString();
		                }
		                Document doc = builder.newDocument();
		                //doc.setPrefix(p.getNamespace());
		                String namespace = p.getNamespace();
		                String tagName = p.getName();
		                Element element = doc.createElementNS(namespace,tagName);
		                Node child=null;
		                if(value.equals("<collection/>")){
		                		child =doc.createElementNS(namespace,"collection");
		                }
		                else{
		                		child = doc.createTextNode(value);
		                }
		                //element.setNodeValue(value);
		                element.appendChild(child);
	                    /*String elLocalName = */element.getLocalName();
	                    property = new ResourceTypeProperty(response,element);
	                    
	                }
	                else if(localName.equals(LOCKDISCOVERY)){
	                    /*DocumentBuilderFactory factory =
	                        DocumentBuilderFactory.newInstance();
	                    factory.setNamespaceAware(true);
	                    DocumentBuilder builder = factory.newDocumentBuilder();
	                    Document doc = builder.newDocument();
	                    Element element = doc.createElement("collection");
	                    property = new LockDiscoveryProperty(response,element);*/
	                		throw new RuntimeException("LockDiscoveryProperty not yet implemented for:"+path);
	                }
	                else{
		                LocalProperty lProperty = new LocalProperty(response);
		                property=lProperty;
		                lProperty.setName(p.getName());
		                lProperty.setNamespaceURI(p.getNamespace());
		                lProperty.setLocalName(p.getName());
		                Object oValue = p.getValue();
		                String value=null;
		                if(oValue!=null){
		                	value=oValue.toString();
		                }
		                lProperty.setPropertyAsString(value);
	                }

	                properties.add(property);
	            }
	            response.setProperties(properties);
	        }catch(ObjectNotFoundException onfe){
	        		HttpException he = new HttpException("Resource on path: "+path+" not found");
	        		he.setReasonCode(WebdavStatus.SC_NOT_FOUND);
	        		throw he;
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            this.namespace.rollback();
	        }
		}
		catch (NotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return responses.elements();
        
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.WebdavResource#setWebdavProperties(java.util.Enumeration)
	 */
	protected void setWebdavProperties(Enumeration arg0) throws HttpException, IOException {
		// TODO Auto-generated method stub
		super.setWebdavProperties(arg0);
	}
	
}
