package com.idega.slide.business;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.SlideTokenImpl;
import org.apache.slide.content.Content;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.event.VetoException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.NodePermission;
import org.apache.slide.security.Security;
import org.apache.slide.structure.LinkedObjectNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.Structure;
import org.apache.slide.structure.SubjectNode;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.webdavservlet.DomainConfig;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

/**
* Simple API of Slide implementation. It improves performance without breaking business logic.
* 
* @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
* @version $Revision: 1.2 $
*
* Last modified: $Date: 2009/05/08 08:10:02 $ by: $Author: valdas $
*/
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWSimpleSlideServiceImp extends DefaultSpringBean implements IWSimpleSlideService, IWSlideChangeListener {

	private static final long serialVersionUID = 8065146986117553218L;
	private static final Logger LOGGER = Logger.getLogger(IWSimpleSlideServiceImp.class.getName());
	
	private static final String CACHE_RESOURCE_EXISTANCE_NAME = "slide_resource_existance_cache";
	private static final String CACHE_RESOURCE_DESCRIPTOR_NAME = "slide_resource_descriptor_cache";
	private static final String CACHE_RESOURCE_DESCRIPTORS_NAME = "slide_resource_descriptors_cache";
	
	private long THREE_MINUTES = 60 * 3;
	
	@Autowired
	private DomainConfig domainConfig;
	
	private NamespaceAccessToken namespace;
	private Structure structure;
	private Content content;
	private Security security;
	
	private AuthenticationBusiness authenticationBusiness;
	
	private boolean initialized;
	
	private synchronized void initializeSimpleSlideServiceBean() {
		if (initialized) {
			initialized = !(namespace == null || structure == null || content == null || security == null);
		}
		if (initialized) {
			return;
		}
		
		initialized = true;
		try {
			if (!Domain.isInitialized()) {
				domainConfig.initialize();
			}
			
			namespace = namespace == null ? Domain.accessNamespace(new SecurityToken(CoreConstants.EMPTY), Domain.getDefaultNamespace()) : namespace;
			structure = structure == null ? namespace.getStructureHelper() : structure;
			content = content == null ? namespace.getContentHelper(): content;
			security = security == null ? namespace.getSecurityHelper() : security;
		} catch(Throwable e) {
			initialized = false;
			LOGGER.warning("Error while initializing Simple Slide API, will try again on next request");
		}
	}
	
	private AuthenticationBusiness getAuthenticationBusiness() {
		if (authenticationBusiness == null) {
			authenticationBusiness = getServiceInstance(AuthenticationBusiness.class);
		}
		return authenticationBusiness;
	}
	
	private SlideToken getContentToken() {
		initializeSimpleSlideServiceBean();
		
		String userPrincipals = null;
		
		try {
			AuthenticationBusiness ab = getAuthenticationBusiness();
			userPrincipals = ab.getRootUserCredentials().getUserName();
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting user's principals", e);
		}
		
		SlideToken token = new SlideTokenImpl(new CredentialsToken(userPrincipals));
		token.setForceStoreEnlistment(true);
		return token;
	}
	
	private String getAuthorsXML(User user) {
		String firstName = null;
		String lastName = null;
		String unknown = "Unknown";
		if (user != null) {
			firstName = user.getFirstName();
			lastName = user.getLastName();
		}
		
		String authors = new StringBuilder("<authors><author><firstname>").append(firstName == null ? unknown : firstName).append("</firstname><lastname>")
						.append(lastName == null ? unknown : lastName).append("</lastname></author></authors>").toString();
		return authors;
	}
	
	private String computeEtag(String uri, NodeRevisionDescriptor nrd) throws Exception {
		StringBuffer result = new StringBuffer(String.valueOf(System.currentTimeMillis())).append(CoreConstants.UNDER).append(uri.hashCode())
			.append(CoreConstants.UNDER).append(nrd.getLastModified()).append(CoreConstants.UNDER).append(nrd.getContentLength());
		return DigestUtils.md5Hex(result.toString());
	}
	
	@SuppressWarnings("unchecked")
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user, boolean closeStream) throws Exception {
		if (stream == null || uploadPath == null || fileName == null) {
			return false;
		}
		
		uploadPath = uploadPath.concat(fileName);
		uploadPath = getNormalizedPath(uploadPath);

		NodeRevisionDescriptor descriptor = null;
		try {
			descriptor = getNodeRevisionDescriptor(uploadPath);
		} catch (Throwable t) {}
		if (descriptor != null) {
			//	Modifying existing file!
			return setContent(uploadPath, stream);
		}
		
		if (!startTransaction()) {
			return false;
		}
		
		SlideToken token = getContentToken();
		try {
			NodeRevisionNumber lastRevision = null;
			try {
				structure.retrieve(token, uploadPath);
				lastRevision = content.retrieve(token, uploadPath).getLatestRevision();
			} catch (ObjectNotFoundException e) {
				SubjectNode subjectNode = new SubjectNode();
				structure.create(token, subjectNode, uploadPath);
			}
			if (lastRevision == null) {
				lastRevision = new NodeRevisionNumber();
			}
			else {
				lastRevision = new NodeRevisionNumber(lastRevision, false);
			}
			
			//	Node revision descriptor
			IWTimestamp now = IWTimestamp.RightNow();
			NodeRevisionDescriptor revisionDescriptor = new NodeRevisionDescriptor(lastRevision, NodeRevisionDescriptors.MAIN_BRANCH, new Vector(),
					new ArrayList());
			revisionDescriptor.setResourceType(CoreConstants.EMPTY);
			revisionDescriptor.setSource(CoreConstants.EMPTY);
			revisionDescriptor.setContentLanguage(Locale.ENGLISH.getLanguage());
			revisionDescriptor.setLastModified(now.getDate());
			revisionDescriptor.setETag(computeEtag(uploadPath, revisionDescriptor));
			revisionDescriptor.setCreationDate(now.getDate());
			
			//	Owner
			String creator = ((SubjectNode) security.getPrincipal(token)).getPath().lastSegment();
			revisionDescriptor.setCreationUser(creator);
			revisionDescriptor.setOwner(creator);
			if (contentType != null) {
				revisionDescriptor.setContentType(contentType);
			}
			
			//	Properties (for now - just owner)
			NodeProperty newProperty = new NodeProperty("authors", getAuthorsXML(user));
			revisionDescriptor.setProperty(newProperty);
			
			//	Create content
			NodeRevisionContent revisionContent = new NodeRevisionContent();
			revisionContent.setContent(stream);
			
			//	Important to create NodeRevisionDescriptors separately to be able to tell it to use versioning
			if (lastRevision.toString().equals("1.0")) {
				content.create(token, uploadPath, true);
			}
			
			content.create(token, uploadPath, revisionDescriptor, revisionContent);
			
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, uploadPath, Boolean.TRUE);
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, uploadPath, revisionDescriptor);
			return true;
		} catch(Throwable t) {
			LOGGER.log(Level.WARNING, "Error while uploading: " + uploadPath, t);
			rollbackTransaction();
		}
		finally {
			if (closeStream) {
				IOUtil.closeInputStream(stream);
			}
			finishTransaction();
		}
		
		return false;
	}
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user) throws Exception {
		return upload(stream, uploadPath, fileName, contentType, user, true);
	}

	private NodeRevisionDescriptors getNodeRevisionDescriptors(String pathToNode) {
		if (StringUtil.isEmpty(pathToNode)) {
			return null;
		}
		
		NodeRevisionDescriptors descriptors = getValueFromCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, pathToNode);
		if (descriptors != null) {
			return descriptors;
		}
		
		SlideToken rootToken = getContentToken();
		if (rootToken == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		pathToNode = getNormalizedPath(pathToNode);
		
		try {
			descriptors = content.retrieve(rootToken, pathToNode);
			
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, pathToNode, descriptors);
			return descriptors;
		} catch (Throwable e) {
		} finally {
			rollbackTransaction();
		}
		
		return null;
	}
	
	private NodeRevisionDescriptor getNodeRevisionDescriptor(String path)
		throws ObjectNotFoundException, AccessDeniedException, LinkedObjectNotFoundException, RevisionDescriptorNotFoundException, ObjectLockedException,
			ServiceAccessException, VetoException {
		
		NodeRevisionDescriptors revisionDescriptors = getNodeRevisionDescriptors(path);
		return getNodeRevisionDescriptor(revisionDescriptors);
	}
	
	public NodeRevisionDescriptor getRevisionDescriptor(String path) {
		path = getNormalizedPath(path);
		if (StringUtil.isEmpty(path)) {
			return null;
		}
		
		try {
			return getNodeRevisionDescriptor(path);
		} catch (Throwable t) {
			LOGGER.warning("Error getting node revision descriptor for: " + path);
		}
		return null;
	}
	
	private NodeRevisionDescriptor getRevisionDescriptor(NodeRevisionDescriptors revisionDescriptors) {
		try {
			return getNodeRevisionDescriptor(revisionDescriptors);
		} catch (Throwable t) {
			LOGGER.warning("Error getting node revision descriptor!");
		}
		return null;
	}
	
	private NodeRevisionDescriptor getNodeRevisionDescriptor(NodeRevisionDescriptors revisionDescriptors)
		throws ObjectNotFoundException, AccessDeniedException, LinkedObjectNotFoundException, RevisionDescriptorNotFoundException, ObjectLockedException,
				ServiceAccessException, VetoException {
		
		String path = getNormalizedPath(revisionDescriptors.getUri());
		NodeRevisionDescriptor descriptor = getValueFromCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path);
		if (descriptor != null) {
			return descriptor;
		}
		
		SlideToken rootToken = getContentToken();
		if (rootToken == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		try {
			descriptor = content.retrieve(rootToken, revisionDescriptors);
			
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path, descriptor);
			return descriptor;
		} finally {
			rollbackTransaction();
		}
	}
	
	public boolean checkExistance(String pathToFile) {
		pathToFile = getNormalizedPath(pathToFile);
		
		Boolean cachedAnswer = getValueFromCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, pathToFile);
		if (cachedAnswer != null) {
			return cachedAnswer;
		}
		
		SlideToken rootToken = getContentToken();
		if (rootToken == null) {
			return false;
		}
		
		if (!startTransaction()) {
			return false;
		}

		boolean rollback = true;
		ObjectNode node = null;
		try {
			node = structure.retrieve(rootToken, pathToFile);
			
			finishTransaction();
			rollback = false;
			
			Boolean exists = node == null ? false : true;
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, pathToFile, exists);
			return exists;
		} catch (ObjectNotFoundException e) {
		} catch (LinkedObjectNotFoundException e) {
		} catch (AccessDeniedException e) {
			LOGGER.warning("Current user can not access " + pathToFile + " - access denied!");
		} catch (ServiceAccessException e) {
			LOGGER.log(Level.WARNING, "Error accessing " + pathToFile, e);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Some error occurred while trying to retrieve " + pathToFile, t);
		} finally {
			if (rollback) {
				rollbackTransaction();
			}
		}
		
		return false;
	}
	
	private NodeRevisionContent getNodeContent(String pathToFile) {
		NodeRevisionDescriptors revisionDescriptors = getNodeRevisionDescriptors(pathToFile);
		if (revisionDescriptors == null || !revisionDescriptors.hasRevisions()) {
			return null;
		}
		
		NodeRevisionDescriptor revisionDescriptor = getRevisionDescriptor(pathToFile);
		if (revisionDescriptor == null) {
			return null;
		}
		
		SlideToken rootToken = getContentToken();
		if (rootToken == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		try {
			return content.retrieve(rootToken, revisionDescriptors, revisionDescriptor);
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error getting InputStream for: " + pathToFile, e);
			rollbackTransaction();
		} finally {
			finishTransaction();
		}
		
		return null;
	}
	
	public InputStream getInputStream(String pathToFile) {
		pathToFile = getNormalizedPath(pathToFile);
		
		NodeRevisionContent nodeContent = getNodeContent(pathToFile);
		
		if (nodeContent == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		InputStream stream = null;
		try {
			stream = nodeContent.streamContent();
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error getting InputStream for: " + pathToFile, e);
			rollbackTransaction();
		}
		
		finishTransaction();
		return stream;
	}
	
	public boolean setContent(String pathToFile, InputStream contentStream) {
		pathToFile = getNormalizedPath(pathToFile);
		
		NodeRevisionContent nodeContent = getNodeContent(pathToFile);
		
		if (nodeContent == null) {
			return Boolean.FALSE;
		}
		
		SlideToken rootToken = getContentToken();
		if (rootToken == null) {
			return Boolean.FALSE;
		}
		
		NodeRevisionDescriptors descriptors = getNodeRevisionDescriptors(pathToFile);
		NodeRevisionDescriptor descriptor = getRevisionDescriptor(descriptors);
		
		if (!startTransaction()) {
			return Boolean.FALSE;
		}
		
		try {
			descriptor.setContentLength(contentStream.available());
			descriptor.setLastModified(new Date(System.currentTimeMillis()));
			
			nodeContent.setContent(contentStream);
			content.store(rootToken, pathToFile, descriptor, nodeContent);
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error setting content InputStream for: " + pathToFile, e);
			rollbackTransaction();
			return Boolean.FALSE;
		}
		
		finishTransaction();
		
		putValueIntoCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, pathToFile, descriptors);
		putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, pathToFile, descriptor);
		putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, pathToFile, Boolean.TRUE);
		return Boolean.TRUE;
	}
	
	public Enumeration<NodePermission> getPermissions(String path) {
		path = getNormalizedPath(path);
		if (StringUtil.isEmpty(path)) {
			return null;
		}
		
		List<NodePermission> permissions = new ArrayList<NodePermission>();
		permissions = getPermissions(permissions, path, path, getContentToken());
		return Collections.enumeration(permissions);
	}
	
	@SuppressWarnings("unchecked")
	private List<NodePermission> getPermissions(List<NodePermission> allPermissions, String path, String originalPath, SlideToken content) {
		if (path == null) {
			return allPermissions;
		}
		
		if (!startTransaction()) {
			return allPermissions;
		}
		Enumeration<NodePermission> permissions = null;
		try {
			permissions = security.enumeratePermissions(content, path);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Error getting permissions for: " + path, t);
			rollbackTransaction();
			return allPermissions;
		}
		if (!finishTransaction()) {
			return allPermissions;
		}
		
		if (permissions != null && permissions.hasMoreElements()) {
			List<NodePermission> currentPermissions = Collections.list(permissions);
			if (path.equals(originalPath)) {
				allPermissions.addAll(currentPermissions);
			} else {
				for (NodePermission permission: currentPermissions) {
					if (permission.isInheritable() && !allPermissions.contains(permission)) {
						allPermissions.add(permission);
					}
				}
			}
		}
		
		return getPermissions(allPermissions, getParentPath(path), path, content);
	}
	
	private String getParentPath(String path) {
		if (StringUtil.isEmpty(path)) {
			return null;
		}
		if (path.endsWith(CoreConstants.SLASH)) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.equals(CoreConstants.WEBDAV_SERVLET_URI)) {
			return path;
		}
		
		int lastSlash = path.lastIndexOf(CoreConstants.SLASH);
		if (lastSlash < 0) {
			return null;
		}
		return path.substring(0, lastSlash);
	}
	
	@SuppressWarnings("unchecked")
	public boolean setPermissions(String path, Ace[] aces) {
		if (ArrayUtil.isEmpty(aces)) {
			return false;
		}
		
		if (!createStructure(path)) {
			return false;
		}
		
		path = getNormalizedPath(path);
		
		if (!startTransaction()) {
			return false;
		}
		
		Collection<NodePermission> permissions = new ArrayList<NodePermission>(aces.length);
		for (Ace ace: aces) {
			List<String> actions = new ArrayList<String>();
			Enumeration<Privilege> privileges = ace.enumeratePrivileges();
			if (privileges != null) {
				while (privileges.hasMoreElements()) {
					Privilege p = privileges.nextElement();
					actions.add(p.getName());
				}
			}
			
			for (String action: actions) {
				String subjectUri = ace.getPrincipal();
				String actionUri = action;
				if (!Privilege.ALL.getName().equals(action)) {
					actionUri = action.startsWith(IWSlideConstants.PATH_ACTIONS) ?
						action : IWSlideConstants.PATH_ACTIONS.concat(CoreConstants.SLASH).concat(action);
				}
				NodePermission permission = new NodePermission(path, subjectUri, actionUri);
				permission.setInheritable(ace.isInheritable());
				String inheritedFrom = ace.getInheritedFrom();
				permission.setInheritedFrom(inheritedFrom);
				permission.setNegative(ace.isNegative());
				permission.setProtected(ace.isProtected());
				
				permissions.add(permission);
			}
		}
		try {
			security.setPermissions(getContentToken(), path, Collections.enumeration(permissions));
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Error setting ACLs " + aces + " for " + path, t);
			rollbackTransaction();
			return false;
		}
		
		finishTransaction();
		return true;
	}
	
	private String getNormalizedPath(String path) {
		if (StringUtil.isEmpty(path)) {
			return path;
		}
		
		if (path.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
			path = StringHandler.replace(path, CoreConstants.WEBDAV_SERVLET_URI, CoreConstants.EMPTY);
		}
		
		try {
			path = URLDecoder.decode(path, CoreConstants.ENCODING_UTF8);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error decoding: " + path, e);
		}
		
		return path;
	}
	
	private boolean startTransaction() {
		initializeSimpleSlideServiceBean();
		
		if (namespace == null) {
			return false;
		}
		
		try {
			if (namespace.getStatus() == 0) {
				//	Transaction was begun already
				return true;
			}
			
			namespace.begin();
		} catch(Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot start user transaction", e);
			return false;
		}
		
		return true;
	}
	
	private boolean rollbackTransaction() {
		if (namespace == null) {
			return false;
		}
		
		try {
			namespace.rollback();
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot rollback user transaction", e);
			return false;
		}
		return true;
	}
	
	private boolean finishTransaction() {
		if (namespace == null) {
			return false;
		}
		
		try {
			namespace.commit();
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot finish user transaction", e);
			return false;
		}
		return true;
	}

	public boolean createStructure(String path) {
		return createStructure(getContentToken(), path, Boolean.TRUE);
	}
	
	private boolean createStructure(SlideToken contentToken, String path, boolean checkParents) {
		path = getNormalizedPath(path);
		if (path == null) {
			return false;
		}
		
		if (checkParents) {
			String[] paths = path.split(CoreConstants.SLASH);
			
			String parentPath = CoreConstants.SLASH;
			for (String pathPart: paths) {
				if (!StringUtil.isEmpty(pathPart)) {
					parentPath = parentPath.concat(pathPart).concat(CoreConstants.SLASH);
					if (!createStructure(contentToken, parentPath, Boolean.FALSE)) {
						return false;
					}
				}
			}
		}
		
		if (checkExistance(path)) {
			return true;
		}
		
		if (!startTransaction()) {
			return false;
		}
		
		boolean error = false;
		ObjectNode node = new SubjectNode(path);
		NodeRevisionDescriptor descriptor = null;
		try {
			structure.create(contentToken, node, path);
			finishTransaction();
		} catch (ObjectAlreadyExistsException e) {
			descriptor = getRevisionDescriptor(path);
			if (descriptor != null) {
				putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path, Boolean.TRUE);
				return true;
			}
		} catch (Throwable t) {
			error = true;
			LOGGER.log(Level.WARNING, "Error creating structure: " + path, t);
			return false;
		} finally {
			if (error) {
				rollbackTransaction();
			}
		}
		
		try {
			if (!startTransaction()) {
				return false;
			}

			descriptor = new NodeRevisionDescriptor();
			NodeRevisionContent nodeContent = new NodeRevisionContent();
			nodeContent.setContent(path.getBytes());
			content.create(contentToken, path, descriptor, nodeContent);

			finishTransaction();
			
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path, Boolean.TRUE);
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path, descriptor);
			return true;
		} catch (Throwable t) {
			error = true;
			LOGGER.log(Level.WARNING, "Error creating descriptor: " + path, t);
		} finally {
			if (error) {
				rollbackTransaction();
			}
		}
		
		return false;
	}

	public boolean delete(String path) {
		path = getNormalizedPath(path);
		if (StringUtil.isEmpty(path)) {
			return false;
		}
		
		NodeRevisionDescriptor descriptor = getRevisionDescriptor(path);
		if (descriptor == null) {
			return false;
		}
		
		if (!startTransaction()) {
			return false;
		}
		try {
			content.remove(getContentToken(), path, descriptor);
			
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to delete: " + path, e);
			rollbackTransaction();
			return false;
		}
		finishTransaction();
		return true;
	}

	public void onSlideChange(IWContentEvent contentEvent) {
		AbstractEventMethod method = contentEvent.getMethod();

		String path = getNormalizedPath(contentEvent.getContentEvent().getUri());
		if (ContentEvent.REMOVE.equals(method)) {
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path);
		} else if (ContentEvent.STORE.equals(method)) {
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path, Boolean.TRUE);
		}
	}
	
	private <K extends Serializable, V> void putValueIntoCache(String cacheName, long ttl, K key, V value) {
		Map<K, V> cache = getCache(cacheName, ttl);
		if (cache != null) {
			cache.put(key, value);
		}
	}
	
	private <K extends Serializable, V> V getValueFromCache(String cacheName, long ttl,  K key) {
		Map<K, V> cache = getCache(cacheName, ttl);
		if (cache != null) {
			return cache.get(key);
		}
		return null;
	}
	
	private <K extends Serializable, V> V removeValueFromCache(String cacheName, long ttl, K key) {
		Map<K, V> cache = getCache(cacheName, ttl);
		if (cache != null) {
			return cache.remove(key);
		}
		return null;
	}
}