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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
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
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.security.NodePermission;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.apache.webdav.lib.properties.AclProperty;
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
	}

	/**
	 * Create a new WebdavResource object (as a seperate method so that it can be overridden by subclasses.)
	 * @param client HttpClient to be used by this webdavresource.
	 * @return A new WebdavResource object.
	 */
	@Override
	protected WebdavResource createWebdavResource(HttpClient client) {
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
		return propfindMethod(path,depth,null);
	}
		
	@Override
	public Enumeration<LocalResponse> propfindMethod(String path, int depth, Vector presetProperties) throws HttpException, IOException {
        this.namespace = Domain.accessNamespace(new SecurityToken(CoreConstants.EMPTY), Domain.getDefaultNamespace());
        String userPrincipal = "root";
        this.token = new SlideTokenImpl(new CredentialsToken(userPrincipal));
        this.token.setForceStoreEnlistment(true);
        String resourcePath = getPath();
        if (resourcePath.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
        	resourcePath = resourcePath.substring(CoreConstants.WEBDAV_SERVLET_URI.length());
        }
        
        try {
	        if (!getSlideAPI().checkExistance(resourcePath)) {
	        	return Collections.enumeration(new ArrayList<LocalResponse>());
	        }
        } catch (Exception e) {}
        
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
	        } catch (Exception e) {
	        	LOGGER.log(Level.WARNING, "Error getting properties for: ".concat(path), e);
	        	
	        	if (e instanceof ObjectNotFoundException) {
	        		getSlideAPI().deletetDefinitionFile(((ObjectNotFoundException) e).getObjectUri());
		        	HttpException he = new HttpException("Resource on path: "+path+" not found");
		        	he.setReasonCode(WebdavStatus.SC_NOT_FOUND);
		        	throw he;
	        	}
	        	if (e instanceof RevisionDescriptorNotFoundException) {
	        		getSlideAPI().deletetDefinitionFile(((RevisionDescriptorNotFoundException) e).getObjectUri());
	        	}
	        } finally {
	            this.namespace.rollback();
	        }
		}
		catch (NotSupportedException e) {
			LOGGER.log(Level.WARNING, "Error getting properties for: ".concat(path), e);
		}
		catch (SystemException e) {
			LOGGER.log(Level.WARNING, "Error getting properties for: ".concat(path), e);
		}
		
		return responses.elements();
	}

	@Override
	public boolean putMethod(byte[] data) throws HttpException, IOException {
		return putMethod(httpURL.getPath(), data);
	}

	@Override
	public boolean putMethod(File file) throws HttpException, IOException {
		return putMethod(httpURL.getPath(), file);
	}

	@Override
	public boolean putMethod(InputStream is) throws HttpException, IOException {
		return putMethod(httpURL.getPath(), is);
	}

	@Override
	public boolean putMethod(String path, byte[] data) throws HttpException, IOException {
		return putMethod(path, new ByteArrayInputStream(data));
	}

	@Override
	public boolean putMethod(String path, File file) throws HttpException, IOException {
		return putMethod(path, new FileInputStream(file));
	}

	@Override
	public boolean putMethod(String path, InputStream is) throws HttpException, IOException {
		if (!getSlideAPI().setContent(path, is)) {
			return super.putMethod(path, is);
		}
		
		return Boolean.TRUE;
	}

	@Override
	public boolean putMethod(String path, String data) throws HttpException, IOException {
		try {
			return putMethod(path, StringHandler.getStreamFromString(data));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error writing data '".concat(data).concat("' to: ").concat(path), e);
		}
		
		return super.putMethod(path, data);
	}

	@Deprecated
	@Override
	/**
	 * Use another method (like putMethod(String path, InputStream is)) to set content
	 */
	public boolean putMethod(String path, URL url) throws HttpException, IOException {
		return super.putMethod(path, url);
	}

	@Override
	public boolean putMethod(String data) throws HttpException, IOException {
		try {
			return putMethod(StringHandler.getStreamFromString(data));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error writing data '".concat(data).concat("' to: ").concat(httpURL.getPath()), e);
		}
		
		return super.putMethod(data);
	}

	@Deprecated
	@Override
	/**
	 * Use another method (like putMethod(String path, InputStream is)) to set content
	 */
	public boolean putMethod(URL url) throws HttpException, IOException {
		return super.putMethod(url);
	}

	@Override
	public InputStream getMethodData() throws HttpException, IOException {
		return getMethodData(httpURL.getPath());
	}

	@Override
	public InputStream getMethodData(String path) throws HttpException, IOException {
		try {
			return getSlideAPI().getInputStream(path);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting input stream from: ".concat(path), e);
		}
		
		return super.getMethodData(path);
	}
	
	@Override
	public boolean exists() {
		try {
			return getSlideAPI().checkExistance(httpURL.getPath());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error checking if resource exists: " + this, e);
		}
		
		return super.exists();
	}
	
	@Override
	public boolean getExistence() {
		return exists();
	}
	
	@Override
	public AclProperty aclfindMethod() throws HttpException, IOException {
		return aclfindMethod(httpURL.getPath());
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
	public boolean deleteMethod() throws HttpException, IOException {
		return deleteMethod(httpURL.getPath());
	}

	@Override
	public boolean deleteMethod(String path) throws HttpException, IOException {
		if (!getSlideAPI().delete(path)) {
			return super.deleteMethod(path);
		}
		
		return Boolean.TRUE;
	}
	
	private IWSimpleSlideService getSlideAPI() {
		if (slideAPI == null) {
			ELUtil.getInstance().autowire(this);
		}
		return slideAPI;
	}

	@Override
	public boolean mkcolMethod() throws HttpException, IOException {
		return mkcolMethod(httpURL.getPath());
	}

	@Override
	public boolean mkcolMethod(String path) throws HttpException, IOException {
		return getSlideAPI().createStructure(path);
	}
	
	@Override
	public long getGetContentLength() {
		NodeRevisionDescriptor descriptor = null;
		try {
			descriptor = getSlideAPI().getRevisionDescriptor(httpURL.getPath());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting content length for resource: " + httpURL);
		}
		if (descriptor == null) {
			return super.getGetContentLength();
		}
		
		return descriptor.getContentLength();
	}
	
	@Override
	public String getGetContentType() {
		NodeRevisionDescriptor descriptor = null;
		try {
			descriptor = getSlideAPI().getRevisionDescriptor(httpURL.getPath());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting content length for resource: " + httpURL);
		}
		if (descriptor == null) {
			return super.getGetContentType();
		}
		
		return descriptor.getContentType();
	}
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}
	
	@Override
	public String getName() {
		return super.getName();
	}
	
	@Override
	public WebdavResources getChildResources() throws HttpException, IOException {
		return getSlideAPI().getResources(httpURL.getPath());
	}
}