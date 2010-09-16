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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideToken;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
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

import com.idega.slide.business.IWSimpleSlideService;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
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
	
	private Element emptyElement, collectionElement;
	
	private String displayName, name, contentType;
	
	private Long length;
	
	private Boolean collection, exists;
	
	private WebdavResources children;
	
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
        	NodeRevisionDescriptor rev = getSlideAPI().getRevisionDescriptor(resourcePath);
        	
        	@SuppressWarnings("unchecked")
			List<NodeProperty> nodeProperties = Collections.list(rev.enumerateProperties());
	        List<Property> properties = new ArrayList<Property>();
	        for (NodeProperty p: nodeProperties) {
	            String localName = p.getPropertyName().getName();
	            Property property = null;
	            
	            if (localName.equals(RESOURCETYPE)) {
	            	Object oValue = p.getValue();
		            String value = oValue == null ? null : oValue.toString();
		            
		            Element element = null;
		            if ("<collection/>".equals(value)) {
		            	element = getCollectionElement();
		            } else if (CoreConstants.EMPTY.equals(value)) {
		            	element = getEmptyElement();
		            } else {
			            Document doc = XmlUtil.getDocumentBuilder().newDocument();
			            String namespace = p.getNamespace();
			            String tagName = p.getName();
			            element = doc.createElementNS(namespace, tagName);
		                element.appendChild(doc.createTextNode(value));
		            }
                    property = new ResourceTypeProperty(response, element);
                } else if (localName.equals(LOCKDISCOVERY)) {
                	/*DocumentBuilderFactory factory =
	                DocumentBuilderFactory.newInstance();
	                factory.setNamespaceAware(true);
	                DocumentBuilder builder = factory.newDocumentBuilder();
	                Document doc = builder.newDocument();
	                Element element = doc.createElement("collection");
	                property = new LockDiscoveryProperty(response,element);*/
	                throw new RuntimeException("LockDiscoveryProperty not yet implemented for:"+path);
	            } else {
	            	LocalProperty lProperty = new LocalProperty(response);
		            property = lProperty;
		            lProperty.setName(p.getName());
		            lProperty.setNamespaceURI(p.getNamespace());
		            lProperty.setLocalName(p.getName());
		            Object oValue = p.getValue();
		            String value = oValue == null ? null : oValue.toString();
		            lProperty.setPropertyAsString(value);
	            }

	            if (property != null) {
	            	properties.add(property);
	            }
	        }
	        
	        if (!ListUtil.isEmpty(properties)) {
	        	response.setProperties(new Vector<Property>(properties));
	        }
        } catch (Exception e) {
	      	LOGGER.log(Level.WARNING, "Error getting properties for: ".concat(path) + ": " + e.getMessage(), e);
	       	
	       	if (e instanceof ObjectNotFoundException) {
	       		getSlideAPI().deletetDefinitionFile(((ObjectNotFoundException) e).getObjectUri());
		      	HttpException he = new HttpException("Resource on path: " + path + " not found");
	        	he.setReasonCode(WebdavStatus.SC_NOT_FOUND);
	        	throw he;
	       	}
	       	if (e instanceof RevisionDescriptorNotFoundException) {
	       		getSlideAPI().deletetDefinitionFile(((RevisionDescriptorNotFoundException) e).getObjectUri());
	       	}
	    }
		
		return responses.elements();
	}
	
	private Element getEmptyElement() {
		if (emptyElement == null) {
			Document doc = XmlUtil.getXMLBuilder().newDocument();
			emptyElement = doc.createElementNS("DAV:", "resourcetype");
			emptyElement.appendChild(doc.createTextNode(CoreConstants.EMPTY));
		}
		return emptyElement;
	}
	
	private Element getCollectionElement() {
		if (collectionElement == null) {
			Document doc = XmlUtil.getXMLBuilder().newDocument();
			collectionElement = doc.createElementNS("DAV:", "resourcetype");
			collectionElement.appendChild(doc.createElementNS("DAV:", "collection"));
		}
		return collectionElement;
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
		if (exists == null) {
			try {
				exists = getSlideAPI().checkExistance(httpURL.getPath());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error checking if resource exists: " + this, e);
			}
			if (exists == null) {
				exists = super.exists();
			}
		}
		return exists;
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
		if (length == null) {
			NodeRevisionDescriptor descriptor = null;
			try {
				descriptor = getSlideAPI().getRevisionDescriptor(httpURL.getPath());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error getting content length for resource: " + httpURL, e);
			}
			
			length = descriptor == null ? super.getGetContentLength() : descriptor.getContentLength();
		}
		return length;
	}
	
	@Override
	public String getGetContentType() {
		if (contentType == null) {
			NodeRevisionDescriptor descriptor = null;
			try {
				descriptor = getSlideAPI().getRevisionDescriptor(httpURL.getPath());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error getting content type for resource: " + httpURL,e );
			}
			
			contentType = descriptor == null ? super.getGetContentType() : descriptor.getContentType();
		}
		return contentType;
	}
	
	@Override
	public boolean isCollection() {
		if (collection == null) {
			NodeRevisionDescriptor descriptor = null;
			try {
				descriptor = getSlideAPI().getRevisionDescriptor(httpURL.getPath());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error resolving if resource is a directory: " + httpURL, e);
			}
			
			if (descriptor == null) {
				collection = super.isCollection();
			} else {
				String resourceType = descriptor.getResourceType();
				collection = !StringUtil.isEmpty(resourceType) && resourceType.indexOf("collection") != -1;
			}
		}
		return collection;
	}

	@Override
	public String getDisplayName() {
		if (displayName == null) {
			displayName = super.getDisplayName();
		}
		return displayName;
	}
	
	@Override
	public String getName() {
		if (name == null) {
			name = super.getName();
		}
		return name;
	}
	
	@Override
	public WebdavResources getChildResources() throws HttpException, IOException {
		if (children == null) {
			children = getSlideAPI().getResources(httpURL.getPath());
		}
		return children;
	}
}