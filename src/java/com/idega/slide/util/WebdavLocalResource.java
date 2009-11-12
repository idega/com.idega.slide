/*
 * $Id: WebdavLocalResource.java,v 1.5 2007/08/20 14:41:03 valdas Exp $
 * Created on 11.10.2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.xml.parsers.DocumentBuilder;

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
import org.apache.slide.security.NodePermission;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.slide.business.IWSimpleSlideService;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.XmlUtil;


/**
 * <p>
 * This class is an extension of the standard WebdavResource to perform some common 
 * operations locally (in the jvm) instead of going through http when communicating with
 * the built in WebDav server. This class is experimental only.
 * </p>
 *  Last modified: $Date: 2007/08/20 14:41:03 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class WebdavLocalResource extends WebdavExtendedResource {
	
	private static final Logger LOGGER = Logger.getLogger(WebdavLocalResource.class.getName());
	
	@Autowired
	private IWSimpleSlideService slideAPI;

	/**
	 * @param client - {@link HttpClient}
	 */
	public WebdavLocalResource(HttpClient client) {
		super(client);
		
		//	TODO: remove after testing!
		try {
			if (1 == 1) {
				throw new RuntimeException("Testing stack trace...");
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Testing", e);
		}
	}

	/**
	 * Create a new WebdavResource object (as a seperate method so that it can be overridden by subclasses.)
	 * @param client HttpClient to be used by this webdavresource.
	 * @return A new WebdavResource object.
	 */
	@Override
	protected WebdavResource createWebdavResource(HttpClient client) {
		LOGGER.info("Local resource called: " + httpURL);
		
		WebdavResource resource = new WebdavLocalResource(client);
		resource.setCredentials(this.hostCredentials);
		return resource;
	}

	//Special overridden low-level methods:
	
    /**
     * Set all properties for this resource.
     *
     * @param depth The depth
     */
    @Override
	protected void setAllProp(int depth) throws HttpException, IOException {
    	LOGGER.info("Local resource called: " + httpURL);
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
	@Override
	public Enumeration propfindMethod(String path, int depth) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return propfindMethod(path,depth,null);
	}
		
	@Override
	public Enumeration<LocalResponse> propfindMethod(String path, int depth, Vector presetProperties) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
        this.namespace = Domain.accessNamespace(new SecurityToken(CoreConstants.EMPTY), Domain.getDefaultNamespace());
        String userPrincipal = "root";
        this.token = new SlideTokenImpl(new CredentialsToken(userPrincipal));
        this.token.setForceStoreEnlistment(true);
        String resourcePath = getPath();
        if (resourcePath.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
        	resourcePath = resourcePath.substring(CoreConstants.WEBDAV_SERVLET_URI.length());
        }
        
        Vector<LocalResponse> responses = new Vector<LocalResponse>();
        LocalResponse response = new LocalResponse();
        response.setHref(path);
        responses.add(response);
        try {
			this.namespace.begin();
	        try {
	            Content c = this.namespace.getContentHelper();
	            
	            NodeRevisionDescriptors revs = c.retrieve(this.token, resourcePath);
	            NodeRevisionDescriptor rev = c.retrieve(this.token, revs, revs.getLatestRevision());
	           
	            Enumeration<NodeProperty> e = rev.enumerateProperties();
	            Vector<Property> properties = new Vector<Property>();
	            while (e.hasMoreElements()) {
	                NodeProperty p = e.nextElement();
	                String localName = p.getPropertyName().getName();
	                Property property = null;
	                if (localName.equals(RESOURCETYPE)) {
	                	DocumentBuilder builder = XmlUtil.getDocumentBuilder();
		                Object oValue = p.getValue();
		                String value = null;
		                if (oValue != null) {
		                	value=oValue.toString();
		                }
		                Document doc = builder.newDocument();
		                String namespace = p.getNamespace();
		                String tagName = p.getName();
		                Element element = doc.createElementNS(namespace,tagName);
		                Node child=null;
		                if (value.equals("<collection/>")) {
		                	child = doc.createElementNS(namespace,"collection");
		                }
		                else {
		                	child = doc.createTextNode(value);
		                }
		                element.appendChild(child);
	                    property = new ResourceTypeProperty(response, element);
	                }
	                else if (localName.equals(LOCKDISCOVERY)) {
	                    /*DocumentBuilderFactory factory =
	                        DocumentBuilderFactory.newInstance();
	                    factory.setNamespaceAware(true);
	                    DocumentBuilder builder = factory.newDocumentBuilder();
	                    Document doc = builder.newDocument();
	                    Element element = doc.createElement("collection");
	                    property = new LockDiscoveryProperty(response,element);*/
	                		throw new RuntimeException("LockDiscoveryProperty not yet implemented for:"+path);
	                }
	                else {
		                LocalProperty lProperty = new LocalProperty(response);
		                property = lProperty;
		                lProperty.setName(p.getName());
		                lProperty.setNamespaceURI(p.getNamespace());
		                lProperty.setLocalName(p.getName());
		                Object oValue = p.getValue();
		                String value = null;
		                if (oValue != null) {
		                	value = oValue.toString();
		                }
		                lProperty.setPropertyAsString(value);
	                }

	                properties.add(property);
	            }
	            response.setProperties(properties);
	        } catch (ObjectNotFoundException onfe) {
	        	HttpException he = new HttpException("Resource on path: "+path+" not found");
	        	he.setReasonCode(WebdavStatus.SC_NOT_FOUND);
	        	throw he;
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            this.namespace.rollback();
	        }
		}
		catch (NotSupportedException e) {
			e.printStackTrace();
		}
		catch (SystemException e) {
			e.printStackTrace();
		}
		
		return responses.elements();
	}

	@Override
	public boolean putMethod(byte[] data) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return putMethod(httpURL.getPathQuery(), data);
	}

	@Override
	public boolean putMethod(File file) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return putMethod(httpURL.getPathQuery(), file);
	}

	@Override
	public boolean putMethod(InputStream is) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return putMethod(httpURL.getPathQuery(), is);
	}

	@Override
	public boolean putMethod(String path, byte[] data) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return putMethod(path, new ByteArrayInputStream(data));
	}

//	TODO: implement
//	@Override
//	public void putMethod(String path, File file, String comment) throws IOException {
//		super.putMethod(path, file, comment);
//	}

	@Override
	public boolean putMethod(String path, File file) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return putMethod(path, new FileInputStream(file));
	}

	@Override
	public boolean putMethod(String path, InputStream is) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		if (!getSlideAPI().setContent(path, is)) {
			return super.putMethod(path, is);
		}
		
		return Boolean.TRUE;
	}

	@Override
	public boolean putMethod(String path, String data) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		try {
			return putMethod(path, StringHandler.getStreamFromString(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.putMethod(path, data);
	}

	@Deprecated
	@Override
	/**
	 * Use another method (like putMethod(String path, InputStream is)) to set content
	 */
	public boolean putMethod(String path, URL url) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.putMethod(path, url);
	}

	@Override
	public boolean putMethod(String data) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		try {
			return putMethod(StringHandler.getStreamFromString(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.putMethod(data);
	}

	@Deprecated
	@Override
	/**
	 * Use another method (like putMethod(String path, InputStream is)) to set content
	 */
	public boolean putMethod(URL url) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.putMethod(url);
	}

	@Override
	public InputStream getMethodData() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return getMethodData(httpURL.getPathQuery());
	}

	@Override
	public InputStream getMethodData(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		try {
			return getSlideAPI().getInputStream(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.getMethodData(path);
	}
	
	@Override
	public boolean exists() {
		LOGGER.info("Local resource called: " + httpURL);
		try {
			return getSlideAPI().checkExistance(httpURL.getPathQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.exists();
	}
	
	@Override
	public boolean getExistence() {
		LOGGER.info("Local resource called: " + httpURL);
		return exists();
	}
	
	private IWSimpleSlideService getSlideAPI() {
		if (slideAPI == null) {
			ELUtil.getInstance().autowire(this);
		}
		return slideAPI;
	}

	//	Not implemented starts
	@Override
	public boolean abortTransaction() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.abortTransaction();
	}

	@Override
	public AclProperty aclfindMethod() throws HttpException, IOException {
		return aclfindMethod(httpURL.getPathQuery());
	}

	@Override
	public AclProperty aclfindMethod(String path) throws HttpException, IOException {
		Enumeration<NodePermission> permissions = getSlideAPI().getPermissions(path);
		if (permissions == null || !permissions.hasMoreElements()) {
			return null;
		}
		
		return new LocalAclProperty(permissions);
	}

	@Override
	public boolean aclMethod(String path, Ace[] aces) throws HttpException, IOException {
		return getSlideAPI().setPermissions(path, aces);
	}

	@Override
	public Enumeration aclReportMethod(String arg0, Collection arg1, int arg2)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.aclReportMethod(arg0, arg1, arg2);
	}

	@Override
	public void addRequestHeader(String header, String value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.addRequestHeader(header, value);
	}

	@Override
	public boolean bindMethod(String existingBinding, String newBinding)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.bindMethod(existingBinding, newBinding);
	}

	@Override
	public boolean bindMethod(String newBinding) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.bindMethod(newBinding);
	}

	@Override
	public boolean checkinMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.checkinMethod();
	}

	@Override
	public boolean checkinMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.checkinMethod(path);
	}

	@Override
	public boolean checkoutMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.checkoutMethod();
	}

	@Override
	public boolean checkoutMethod(String path) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.checkoutMethod(path);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.clone();
	}

	@Override
	public void close() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.close();
	}

	@Override
	public void closeSession() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.closeSession();
	}

	@Override
	public synchronized void closeSession(HttpClient client) throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.closeSession(client);
	}

	@Override
	public boolean commitTransaction() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.commitTransaction();
	}

	@Override
	public int compareTo(Object another) {
		LOGGER.info("Local resource called: " + httpURL);
		return super.compareTo(another);
	}

	@Override
	public int compareToWebdavResource(WebdavResource arg0) {
		LOGGER.info("Local resource called: " + httpURL);
		return super.compareToWebdavResource(arg0);
	}

	@Override
	public boolean copyMethod(String source, String destination)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.copyMethod(source, destination);
	}

	@Override
	public boolean copyMethod(String destination) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.copyMethod(destination);
	}

	@Override
	public WebdavExtendedResource createUpdatedResource() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.createUpdatedResource();
	}

	@Override
	public boolean deleteMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.deleteMethod();
	}

	@Override
	public boolean deleteMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.deleteMethod(path);
	}

	@Override
	protected WebdavState discoverLock(String arg0, String arg1,
			WebdavState arg2) {
		LOGGER.info("Local resource called: " + httpURL);
		return super.discoverLock(arg0, arg1, arg2);
	}

	@Override
	public void discoverOwnLocks() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.discoverOwnLocks();
	}

	@Override
	public void discoverOwnLocks(String owner) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.discoverOwnLocks(owner);
	}

	@Override
	protected boolean endTransaction(String path, int transactionStatus)
			throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.endTransaction(path, transactionStatus);
	}

	@Override
	public boolean equals(Object obj) {
		LOGGER.info("Local resource called: " + httpURL);
		return super.equals(obj);
	}

	@Override
	public int executeHttpRequestMethod(HttpClient client, HttpMethod method)
			throws IOException, HttpException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.executeHttpRequestMethod(client, method);
	}

	@Override
	protected void finalize() throws Throwable {
		LOGGER.info("Local resource called: " + httpURL);
		super.finalize();
	}

	@Override
	protected void generateAdditionalHeaders(HttpMethod arg0) {
		LOGGER.info("Local resource called: " + httpURL);
		super.generateAdditionalHeaders(arg0);
	}

	@Override
	protected void generateIfHeader(HttpMethod arg0) {
		LOGGER.info("Local resource called: " + httpURL);
		super.generateIfHeader(arg0);
	}

	@Override
	protected void generateTransactionHeader(HttpMethod method) {
		LOGGER.info("Local resource called: " + httpURL);
		super.generateTransactionHeader(method);
	}

	@Override
	public Enumeration getActiveLockOwners() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getActiveLockOwners();
	}

	@Override
	public Enumeration getAllowedMethods() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getAllowedMethods();
	}

	@Override
	public String getCheckedIn() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getCheckedIn();
	}

	@Override
	public String getCheckedOut() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getCheckedOut();
	}

	@Override
	public WebdavResources getChildResources() throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getChildResources();
	}

	@Override
	public String getComment() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getComment();
	}

	@Override
	public long getCreationDate() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getCreationDate();
	}

	@Override
	public String getCreationDateString() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getCreationDateString();
	}

	@Override
	public Enumeration getDavCapabilities() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getDavCapabilities();
	}

	@Override
	public String getDecodedPath() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getDecodedPath();
	}

	@Override
	public String getDisplayName() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getDisplayName();
	}

	@Override
	public String getEncodedPath() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getEncodedPath();
	}

	@Override
	public boolean getFollowRedirects() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getFollowRedirects();
	}

	@Override
	public long getGetContentLength() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getGetContentLength();
	}

	@Override
	public String getGetContentType() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getGetContentType();
	}

	@Override
	public String getGetEtag() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getGetEtag();
	}

	@Override
	public long getGetLastModified() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getGetLastModified();
	}

	@Override
	public String getHost() throws URIException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getHost();
	}

	@Override
	public HttpURL getHttpURL() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getHttpURL();
	}

	@Override
	public HttpURL getHttpURLExceptForUserInfo() throws URIException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getHttpURLExceptForUserInfo();
	}

	@Override
	public boolean getIsCollection() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getIsCollection();
	}

	@Override
	public boolean getIsHidden() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getIsHidden();
	}

	@Override
	public LockDiscoveryProperty getLockDiscovery() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getLockDiscovery();
	}

	@Override
	public boolean getMethod(File file) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getMethod(file);
	}

	@Override
	public boolean getMethod(String arg0, File arg1) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getMethod(arg0, arg1);
	}

	@Override
	public String getMethodDataAsString() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getMethodDataAsString();
	}

	@Override
	public String getMethodDataAsString(String path) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getMethodDataAsString(path);
	}

	@Override
	public String getName() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getName();
	}

	@Override
	public boolean getOverwrite() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getOverwrite();
	}

	@Override
	public String getOwner() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getOwner();
	}

	@Override
	public String getParentPath() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getParentPath();
	}

	@Override
	public String getPath() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getPath();
	}

	@Override
	public ResourceTypeProperty getResourceType() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getResourceType();
	}

	@Override
	public HttpClient getSessionInstance(HttpURL arg0, boolean arg1)
			throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getSessionInstance(arg0, arg1);
	}

	@Override
	public HttpClient getSessionInstance(HttpURL httpURL) throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getSessionInstance(httpURL);
	}

	@Override
	public int getStatusCode() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getStatusCode();
	}

	@Override
	public String getStatusMessage() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getStatusMessage();
	}

	@Override
	public String getSupportedLock() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getSupportedLock();
	}

	@Override
	public String getTransactionHandle() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getTransactionHandle();
	}

	@Override
	public String getVersionName() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.getVersionName();
	}

	@Override
	public int hashCode() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.hashCode();
	}

	@Override
	public boolean headMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.headMethod();
	}

	@Override
	public boolean headMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.headMethod(path);
	}

	@Override
	public boolean isCollection() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.isCollection();
	}

	@Override
	public boolean isLocked() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.isLocked();
	}

	@Override
	protected synchronized boolean isTheClient() throws URIException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.isTheClient();
	}

	@Override
	public boolean labelMethod(String labelname, int type)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.labelMethod(labelname, type);
	}

	@Override
	public boolean labelMethod(String path, String labelname, int type)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.labelMethod(path, labelname, type);
	}

	@Override
	public String[] list() {
		LOGGER.info("Local resource called: " + httpURL);
		return super.list();
	}

	@Override
	public Vector listBasic() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.listBasic();
	}

	@Override
	public WebdavResource[] listWebdavResources() throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.listWebdavResources();
	}

	@Override
	public WebdavResources listWithDeltaV() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.listWithDeltaV();
	}

	@Override
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod()
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockDiscoveryPropertyFindMethod();
	}

	@Override
	public LockDiscoveryProperty lockDiscoveryPropertyFindMethod(String arg0)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockDiscoveryPropertyFindMethod(arg0);
	}

	@Override
	public boolean lockMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod();
	}

	@Override
	public boolean lockMethod(String owner, int timeout) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(owner, timeout);
	}

	@Override
	public boolean lockMethod(String owner, short timeout)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(owner, timeout);
	}

	@Override
	public boolean lockMethod(String path, String owner, int timeout,
			short lockType, int depth) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(path, owner, timeout, lockType, depth);
	}

	@Override
	public boolean lockMethod(String path, String owner, int timeout,
			short lockType) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(path, owner, timeout, lockType);
	}

	@Override
	public boolean lockMethod(String path, String owner, int timeout)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(path, owner, timeout);
	}

	@Override
	public boolean lockMethod(String path, String owner, short timeout)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(path, owner, timeout);
	}

	@Override
	public boolean lockMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethod(path);
	}

	@Override
	public boolean lockMethodNoTimeout() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.lockMethodNoTimeout();
	}

	@Override
	public boolean mkcolMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.mkcolMethod();
	}

	@Override
	public boolean mkcolMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.mkcolMethod(path);
	}

	@Override
	public boolean mkWorkspaceMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.mkWorkspaceMethod();
	}

	@Override
	public boolean mkWorkspaceMethod(String path) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.mkWorkspaceMethod(path);
	}

	@Override
	public boolean moveMethod(String source, String destination)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.moveMethod(source, destination);
	}

	@Override
	public boolean moveMethod(String destination) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.moveMethod(destination);
	}

	@Override
	public boolean optionsMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod();
	}

	@Override
	public Enumeration optionsMethod(HttpURL arg0, int arg1)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod(arg0, arg1);
	}

	@Override
	public Enumeration optionsMethod(HttpURL arg0) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod(arg0);
	}

	@Override
	public Enumeration optionsMethod(String arg0, int arg1)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod(arg0, arg1);
	}

	@Override
	public boolean optionsMethod(String path, String aMethod)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod(path, aMethod);
	}

	@Override
	public boolean optionsMethod(String arg0) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.optionsMethod(arg0);
	}

	@Override
	protected Date parseDate(String arg0) {
		LOGGER.info("Local resource called: " + httpURL);
		return super.parseDate(arg0);
	}

	@Override
	public boolean pollMethod(String contentLocation, int subscriptionId)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.pollMethod(contentLocation, subscriptionId);
	}

	@Override
	public boolean pollMethod(Subscription subscription) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.pollMethod(subscription);
	}

	@Override
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod()
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.principalCollectionSetFindMethod();
	}

	@Override
	public PrincipalCollectionSetProperty principalCollectionSetFindMethod(
			String arg0) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.principalCollectionSetFindMethod(arg0);
	}

	@Override
	protected void processProperty(Property property) {
		LOGGER.info("Local resource called: " + httpURL);
		super.processProperty(property);
	}

	@Override
	public Enumeration propfindMethod(int depth, Vector properties)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(depth, properties);
	}

	@Override
	public Enumeration propfindMethod(int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(depth);
	}

	@Override
	public Enumeration propfindMethod(String path, String propertyName)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(path, propertyName);
	}

	@Override
	public Enumeration propfindMethod(String arg0, Vector arg1)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(arg0, arg1);
	}

	@Override
	public Enumeration propfindMethod(String propertyName)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(propertyName);
	}

	@Override
	public Enumeration propfindMethod(Vector properties) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.propfindMethod(properties);
	}

	@Override
	public boolean proppatchMethod(Hashtable properties, boolean action)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(properties, action);
	}

	@Override
	public boolean proppatchMethod(Hashtable properties) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(properties);
	}

	@Override
	public boolean proppatchMethod(PropertyName propertyName,
			String propertyValue, boolean action) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(propertyName, propertyValue, action);
	}

	@Override
	public boolean proppatchMethod(PropertyName propertyName,
			String propertyValue) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(propertyName, propertyValue);
	}

	@Override
	public boolean proppatchMethod(String arg0, Hashtable arg1, boolean arg2)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(arg0, arg1, arg2);
	}

	@Override
	public boolean proppatchMethod(String path, Hashtable properties)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(path, properties);
	}

	@Override
	public boolean proppatchMethod(String path, PropertyName propertyName,
			String propertyValue, boolean action) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(path, propertyName, propertyValue, action);
	}

	@Override
	public boolean proppatchMethod(String path, PropertyName propertyName,
			String propertyValue) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(path, propertyName, propertyValue);
	}

	@Override
	public boolean proppatchMethod(String propertyName, String propertyValue,
			boolean action) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(propertyName, propertyValue, action);
	}

	@Override
	public boolean proppatchMethod(String path, String propertyName,
			String propertyValue, boolean action) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(path, propertyName, propertyValue, action);
	}

	@Override
	public boolean proppatchMethod(String path, String propertyName,
			String propertyValue) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(path, propertyName, propertyValue);
	}

	@Override
	public boolean proppatchMethod(String propertyName, String propertyValue)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.proppatchMethod(propertyName, propertyValue);
	}

	@Override
	public void putMethod(String path, File file, String comment)
			throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.putMethod(path, file, comment);
	}

	@Override
	public boolean rebindMethod(String existingBinding, String newBinding)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.rebindMethod(existingBinding, newBinding);
	}

	@Override
	public boolean rebindMethod(String newBinding) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.rebindMethod(newBinding);
	}

	@Override
	protected void refresh() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.refresh();
	}

	@Override
	public Enumeration reportMethod(HttpURL arg0, int arg1)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.reportMethod(arg0, arg1);
	}

	@Override
	public Enumeration reportMethod(HttpURL arg0, String arg1, int arg2)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.reportMethod(arg0, arg1, arg2);
	}

	@Override
	public Enumeration reportMethod(HttpURL arg0, Vector arg1, int arg2)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.reportMethod(arg0, arg1, arg2);
	}

	@Override
	public Enumeration reportMethod(HttpURL arg0, Vector arg1, Vector arg2,
			int arg3) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.reportMethod(arg0, arg1, arg2, arg3);
	}

	@Override
	public Enumeration reportMethod(HttpURL httpURL, Vector properties)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.reportMethod(httpURL, properties);
	}

	@Override
	public HttpClient retrieveSessionInstance() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.retrieveSessionInstance();
	}

	@Override
	protected void setBasicProperties(int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setBasicProperties(depth);
	}

	@Override
	protected void setCheckedIn(String value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCheckedIn(value);
	}

	@Override
	protected void setCheckedOut(String value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCheckedOut(value);
	}

	@Override
	protected void setClient() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setClient();
	}

	@Override
	protected synchronized void setClient(HttpURL httpURL) throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setClient(httpURL);
	}

	@Override
	public void setComment(String comment) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setComment(comment);
	}

	@Override
	public void setContentType(String contentType) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setContentType(contentType);
	}

	@Override
	protected void setCreationDate(long creationDate) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCreationDate(creationDate);
	}

	@Override
	protected void setCreationDate(String creationDate) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCreationDate(creationDate);
	}

	@Override
	protected void setCreationDateString(String value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCreationDateString(value);
	}

	@Override
	public void setCredentials(Credentials credentials) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setCredentials(credentials);
	}

	@Override
	public void setDebug(int debug) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setDebug(debug);
	}

	@Override
	protected void setDefaultProperties(int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setDefaultProperties(depth);
	}

	@Override
	protected void setDisplayName(String displayName) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setDisplayName(displayName);
	}

	@Override
	public void setEncodeURLs(boolean encodeURLs) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setEncodeURLs(encodeURLs);
	}

	@Override
	protected void setExistence(boolean exists) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setExistence(exists);
	}

	@Override
	public void setFinalHttpURL(HttpURL url) throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setFinalHttpURL(url);
	}

	@Override
	public void setFollowRedirects(boolean value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setFollowRedirects(value);
	}

	@Override
	protected void setGetContentLength(long getContentLength) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetContentLength(getContentLength);
	}

	@Override
	protected void setGetContentLength(String arg0) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetContentLength(arg0);
	}

	@Override
	protected void setGetContentType(String getContentType) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetContentType(getContentType);
	}

	@Override
	protected void setGetEtag(String getEtag) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetEtag(getEtag);
	}

	@Override
	protected void setGetLastModified(long getLastModified) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetLastModified(getLastModified);
	}

	@Override
	protected void setGetLastModified(String getLastModified) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setGetLastModified(getLastModified);
	}

	@Override
	public void setHttpURL(HttpURL arg0, int arg1, int arg2)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(arg0, arg1, arg2);
	}

	@Override
	public void setHttpURL(HttpURL httpURL, int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(httpURL, depth);
	}

	@Override
	public void setHttpURL(HttpURL httpURL, String additionalPath, int action,
			int depth) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(httpURL, additionalPath, action, depth);
	}

	@Override
	public void setHttpURL(HttpURL httpURL, String additionalPath, int action)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(httpURL, additionalPath, action);
	}

	@Override
	public void setHttpURL(HttpURL httpURL, String additionalPath)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(httpURL, additionalPath);
	}

	@Override
	public void setHttpURL(HttpURL httpURL) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(httpURL);
	}

	@Override
	public void setHttpURL(String escapedHttpURL) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setHttpURL(escapedHttpURL);
	}

	@Override
	protected void setIsCollection(boolean isCollection) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setIsCollection(isCollection);
	}

	@Override
	protected void setIsCollection(String isCollection) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setIsCollection(isCollection);
	}

	@Override
	protected void setIsHidden(boolean isHidden) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setIsHidden(isHidden);
	}

	@Override
	protected void setIsHidden(String isHidden) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setIsHidden(isHidden);
	}

	@Override
	protected void setLockDiscovery(LockDiscoveryProperty lockDiscovery) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setLockDiscovery(lockDiscovery);
	}

	@Override
	protected void setNamedProp(int depth, Vector propertyNames)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setNamedProp(depth, propertyNames);
	}

	@Override
	protected void setNameProperties(int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setNameProperties(depth);
	}

	@Override
	public void setOverwrite(boolean overwrite) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setOverwrite(overwrite);
	}

	@Override
	public void setPath(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setPath(path);
	}

	@Override
	public void setProperties() throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setProperties();
	}

	@Override
	public void setProperties(int action, int depth) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setProperties(action, depth);
	}

	@Override
	public void setProperties(int depth) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setProperties(depth);
	}

	@Override
	public void setProxy(String host, int port) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setProxy(host, port);
	}

	@Override
	public void setProxyCredentials(Credentials credentials) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setProxyCredentials(credentials);
	}

	@Override
	protected void setResourceType(ResourceTypeProperty resourceType) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setResourceType(resourceType);
	}

	@Override
	protected void setStatusCode(int statusCode, String message) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setStatusCode(statusCode, message);
	}

	@Override
	protected void setStatusCode(int statusCode) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setStatusCode(statusCode);
	}

	@Override
	protected void setSupportedLock(String supportedLock) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setSupportedLock(supportedLock);
	}

	@Override
	public void setUserInfo(String userName, String password)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setUserInfo(userName, password);
	}

	@Override
	protected void setVersionName(String value) {
		LOGGER.info("Local resource called: " + httpURL);
		super.setVersionName(value);
	}

	@Override
	protected void setWebdavProperties(Enumeration arg0) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		super.setWebdavProperties(arg0);
	}

	@Override
	public boolean startTransaction(String owner, int timeout)
			throws IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.startTransaction(owner, timeout);
	}

	@Override
	public boolean subscribeMethod(String path, int subscriptionId)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.subscribeMethod(path, subscriptionId);
	}

	@Override
	public Subscription subscribeMethod(String path, String notificationType,
			String callback, long notificationDelay, int depth, long lifetime)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.subscribeMethod(path, notificationType, callback,
				notificationDelay, depth, lifetime);
	}

	@Override
	public boolean subscribeMethod(Subscription subscription)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.subscribeMethod(subscription);
	}

	@Override
	public boolean unbindMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unbindMethod();
	}

	@Override
	public boolean unbindMethod(String binding) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unbindMethod(binding);
	}

	@Override
	public boolean uncheckoutMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.uncheckoutMethod();
	}

	@Override
	public boolean uncheckoutMethod(String path) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.uncheckoutMethod(path);
	}

	@Override
	public boolean unlockMethod() throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unlockMethod();
	}

	@Override
	public boolean unlockMethod(String path, String owner)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unlockMethod(path, owner);
	}

	@Override
	public boolean unlockMethod(String path) throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unlockMethod(path);
	}

	@Override
	public boolean unsubscribeMethod(String path, int subscriptionId)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unsubscribeMethod(path, subscriptionId);
	}

	@Override
	public boolean unsubscribeMethod(Subscription subscription)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.unsubscribeMethod(subscription);
	}

	@Override
	public boolean updateMethod(String path, String target)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.updateMethod(path, target);
	}

	@Override
	public boolean updateMethod(String target) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.updateMethod(target);
	}

	@Override
	public boolean versionControlMethod(String path, String target)
			throws HttpException, IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.versionControlMethod(path, target);
	}

	@Override
	public boolean versionControlMethod(String path) throws HttpException,
			IOException {
		LOGGER.info("Local resource called: " + httpURL);
		return super.versionControlMethod(path);
	}
	//	Not implemented ends
}