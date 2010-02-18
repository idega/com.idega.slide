package com.idega.slide.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.slide.common.SlideCommonUtil;
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
import org.apache.slide.macro.Macro;
import org.apache.slide.macro.MacroImpl;
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
import org.apache.webdav.lib.WebdavResources;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.bean.SlideAction;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.webdavservlet.DomainConfig;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.xml.XmlUtil;

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
	
	private static final String DEFINITION_XML_FILE_ENDING = ".def.xml";
	
	private long THREE_MINUTES = 60 * 3;
	
	private Map<SlideAction, List<NamespaceAccessToken>> activeTransactions = new HashMap<SlideAction, List<NamespaceAccessToken>>();
	
	@Autowired
	private DomainConfig domainConfig;
	
	private Structure structure;
	private Content content;
	private Security security;
	
	private AuthenticationBusiness authenticationBusiness;
	
	private boolean initialized;
	
	private void initializeSimpleSlideServiceBean() {
		if (initialized) {
			initialized = !(structure == null || content == null || security == null);
		}
		if (initialized) {
			return;
		}
		
		initialized = true;
		try {
			if (!Domain.isInitialized()) {
				domainConfig.initialize();
			}
			
			NamespaceAccessToken namespace = getNamespace();
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

		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
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
			rollbackTransaction(namespace);
		} finally {
			if (closeStream) {
				IOUtil.closeInputStream(stream);
			}
			commitTransaction(namespace);
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.ROLLBACK);
		if (namespace == null) {
			return null;
		}
		
		pathToNode = getNormalizedPath(pathToNode);
		
		try {
			descriptors = content.retrieve(rootToken, pathToNode);
			
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, pathToNode, descriptors);
			return descriptors;
		} catch (ObjectNotFoundException e) {
			deletetDefinitionFile(e.getObjectUri());
		} catch (Throwable e) {
		} finally {
			rollbackTransaction(namespace);
		}
		
		return null;
	}
	
	private NodeRevisionDescriptor getNodeRevisionDescriptor(String path)
		throws AccessDeniedException, LinkedObjectNotFoundException, RevisionDescriptorNotFoundException, ObjectLockedException,
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
		throws AccessDeniedException, LinkedObjectNotFoundException, RevisionDescriptorNotFoundException, ObjectLockedException,
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.ROLLBACK);
		if (namespace == null) {
			return null;
		}
		
		try {
			descriptor = content.retrieve(rootToken, revisionDescriptors);
			
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path, descriptor);
			return descriptor;
		} catch (ObjectNotFoundException e) {
			deletetDefinitionFile(e.getObjectUri());
			return null;
		} finally {
			rollbackTransaction(namespace);
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return false;
		}

		boolean rollback = true;
		ObjectNode node = null;
		try {
			node = structure.retrieve(rootToken, pathToFile);
			
			commitTransaction(namespace);
			rollback = false;
			
			Boolean exists = node == null ? false : true;
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, pathToFile, exists);
			return exists;
		} catch (ObjectNotFoundException e) {
			deletetDefinitionFile(e.getObjectUri());
		} catch (LinkedObjectNotFoundException e) {
		} catch (AccessDeniedException e) {
			LOGGER.warning("Current user can not access " + pathToFile + " - access denied!");
		} catch (ServiceAccessException e) {
			LOGGER.log(Level.WARNING, "Error accessing " + pathToFile, e);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Some error occurred while trying to retrieve " + pathToFile, t);
		} finally {
			if (rollback) {
				rollbackTransaction(namespace);
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return null;
		}
		
		boolean rollback = false;
		try {
			return content.retrieve(rootToken, revisionDescriptors, revisionDescriptor);
		} catch (ObjectNotFoundException e) {
			LOGGER.log(Level.WARNING, "Error getting InputStream for: " + pathToFile, e);
			deletetDefinitionFile(e.getObjectUri());
			rollback = true;
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error getting InputStream for: " + pathToFile, e);
			rollback = true;
		} finally {
			if (rollback) {
				rollbackTransaction(namespace);
			} else {
				commitTransaction(namespace);
			}
		}
		
		return null;
	}
	
	public InputStream getInputStream(String pathToFile) {
		pathToFile = getNormalizedPath(pathToFile);
		
		NodeRevisionContent nodeContent = getNodeContent(pathToFile);
		
		if (nodeContent == null) {
			return null;
		}
		
		InputStream stream = null;
		try {
			stream = nodeContent.streamContent();
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error getting InputStream for: " + pathToFile, e);
		}
		
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return Boolean.FALSE;
		}
		
		boolean rollback = false;
		try {
			descriptor.setContentLength(contentStream.available());
			descriptor.setLastModified(new Date(System.currentTimeMillis()));
			
			nodeContent.setContent(contentStream);
			content.store(rootToken, pathToFile, descriptor, nodeContent);
		} catch (Throwable e) {
			rollback = Boolean.TRUE;
			LOGGER.log(Level.WARNING, "Error setting content InputStream for: " + pathToFile, e);
			
			if (e instanceof ObjectNotFoundException) {
				deletetDefinitionFile(((ObjectNotFoundException) e).getObjectUri());
			} else if (e instanceof RevisionDescriptorNotFoundException) {
				deletetDefinitionFile(((RevisionDescriptorNotFoundException) e).getObjectUri());
			}
			
			return Boolean.FALSE;
		} finally {
			if (rollback) {
				rollbackTransaction(namespace);
			} else {
				commitTransaction(namespace);
			}
		}
		
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return allPermissions;
		}
		Enumeration<NodePermission> permissions = null;
		try {
			permissions = security.enumeratePermissions(content, path);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Error getting permissions for: " + path, t);
			rollbackTransaction(namespace);
			
			if (t instanceof ObjectNotFoundException) {
				deletetDefinitionFile(((ObjectNotFoundException) t).getObjectUri());
			}
			
			return allPermissions;
		}
		if (!commitTransaction(namespace)) {
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
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
			rollbackTransaction(namespace);
			
			if (t instanceof ObjectNotFoundException) {
				deletetDefinitionFile(((ObjectNotFoundException) t).getObjectUri());
			}
			
			return false;
		}
		
		commitTransaction(namespace);
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
	
	private NamespaceAccessToken startTransaction(SlideAction action) {
		initializeSimpleSlideServiceBean();
		
		NamespaceAccessToken namespace = getNamespace();
		if (namespace == null) {
			return null;
		}
		
		try {
			if (namespace.getStatus() == 0) {
				//	Transaction was begun already!
				LOGGER.info("************* TRANSACTION already has started!");
				return namespace;
			}
			
			namespace.begin();
		} catch(Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot start user transaction", e);
			return null;
		} finally {			
			addTransaction(action, namespace);
		}
		
		return namespace;
	}
	
	private List<NamespaceAccessToken> getTransactions(SlideAction action) {
		return activeTransactions.get(action);
	}
	
	private void addTransaction(SlideAction action, NamespaceAccessToken namespace) {
		synchronized (activeTransactions) {
			List<NamespaceAccessToken> transactions = getTransactions(action);
			if (transactions == null) {
				transactions = new ArrayList<NamespaceAccessToken>();
				activeTransactions.put(action, transactions);
			}
			synchronized (transactions) {
				transactions.add(namespace);
				
				LOGGER.info("*********** actions for " + action + ": " + transactions);	//	TODO
			}
		}
	}
	
	private boolean canRollback() {
		return true;
		/*List<NamespaceAccessToken> rollbacks = getTransactions(SlideAction.ROLLBACK);
		if (rollbacks != null) {
			synchronized (rollbacks) {
				if (rollbacks.size() > 1) {
					LOGGER.info("Can not ROLLBACK: " + rollbacks.size());	//	TODO
					return false;
				}
			}
		}
		
		synchronized (activeTransactions) {
			List<NamespaceAccessToken> committs = getTransactions(SlideAction.COMMIT);
			if (committs == null) {
				LOGGER.info("ROLLBACK, there are no transactions for commit!");	//	TODO
				return Boolean.TRUE;
			}

			synchronized (committs) {
				if (committs.size() == 0) {
					LOGGER.info("ROLLBACK, there are no transactions for commit!");	//	TODO
					return Boolean.TRUE;
				}
				
				LOGGER.info("Can not ROLLBACK, there are transactions for commit: " + committs);	//	TODO
				//	We do not want to rollback if there are actions in progress that are going to commit changes in Slide
				return Boolean.FALSE;
			}
		}*/
	}
	
	private boolean rollbackTransaction(NamespaceAccessToken namespace) {
		if (namespace == null) {
			return false;
		}
		
		try {
			if (canRollback()) {
				namespace.rollback();
			}
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot rollback user transaction", e);
			return false;
		} finally {
			finishTransaction(namespace, SlideAction.ROLLBACK);
		}
		
		return true;
	}
	
	private boolean canCommit(NamespaceAccessToken namespace) {
		return Boolean.TRUE;
		
		/*List<NamespaceAccessToken> commits = getTransactions(SlideAction.COMMIT);
		if (commits == null) {
			LOGGER.info("COMMITTING, there are no more transactions for commit: " + commits);	//	TODO
			return Boolean.TRUE;
		}
		
		synchronized (commits) {
			if (commits.size() == 1 && commits.contains(namespace)) {
				LOGGER.info("COMMITTING, there is only one transaction to commit: " + commits + " OR current transaction " + namespace + " is not in a list!");	//	TODO
				return Boolean.TRUE;
			} else {
				LOGGER.info("NOT committing transaction " + namespace + ", there are more transactions to commit: " + commits);	//	TODO
				return Boolean.FALSE;
			}
		}*/
	}
	
	private boolean commitTransaction(NamespaceAccessToken namespace) {
		if (namespace == null) {
			return false;
		}
		
		try {
			if (canCommit(namespace)) {
				namespace.commit();
			}
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Cannot finish user transaction", e);
			return false;
		} finally {
			finishTransaction(namespace, SlideAction.COMMIT);
		}
		
		return true;
	}
	
	private void finishTransaction(NamespaceAccessToken namespace, SlideAction action) {
		if (!removeTransaction(namespace, action)) {
			LOGGER.info("Namespace " +namespace+ " was not found in: " + getTransactions(action) + " will try with oposite action");	//	TODO
			
			// Transaction was started for commit but now trying to rollback!
			SlideAction intendedAction = SlideAction.COMMIT == action ? SlideAction.ROLLBACK : SlideAction.COMMIT;
			removeTransaction(namespace, intendedAction);
		}
	}
	
	private boolean removeTransaction(NamespaceAccessToken namespace, SlideAction action) {
		synchronized (activeTransactions) {
			List<NamespaceAccessToken> transactions = getTransactions(action);
			if (transactions == null) {
				return Boolean.FALSE;
			}
			
			synchronized (transactions) {
				if (transactions.contains(namespace)) {
					LOGGER.info("REMOVING transaction " + namespace + " from " + transactions + ", action: " + action);
					transactions.remove(namespace);
					
					return Boolean.TRUE;
				} else {
					LOGGER.info("Transactions " + transactions + " does not contain this: " + namespace + ", try oposite action?");
				}
			}
		}
		
		return Boolean.FALSE;
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
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return false;
		}
		
		boolean error = false;
		ObjectNode node = new SubjectNode(path);
		NodeRevisionDescriptor descriptor = null;
		try {
			structure.create(contentToken, node, path);
			commitTransaction(namespace);
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
				rollbackTransaction(namespace);
			}
		}
		
		try {
			namespace = startTransaction(SlideAction.COMMIT);
			if (namespace == null) {
				return false;
			}

			descriptor = new NodeRevisionDescriptor();
			NodeRevisionContent nodeContent = new NodeRevisionContent();
			nodeContent.setContent(path.getBytes());
			content.create(contentToken, path, descriptor, nodeContent);

			commitTransaction(namespace);
			
			putValueIntoCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path, Boolean.TRUE);
			putValueIntoCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path, descriptor);
			return true;
		} catch (Throwable t) {
			error = true;
			LOGGER.log(Level.WARNING, "Error creating descriptor: " + path, t);
		} finally {
			if (error) {
				rollbackTransaction(namespace);
			}
		}
		
		return false;
	}

	public boolean delete(String path) {
		path = getNormalizedPath(path);
		if (StringUtil.isEmpty(path)) {
			return false;
		}
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.COMMIT);
		if (namespace == null) {
			return false;
		}
		
		boolean deleteXML = true;
		try {
			SlideToken token = getContentToken();
			org.apache.slide.common.Namespace slideNamespace = SlideCommonUtil.getInstance().getDefaultNamespace();

			Macro macro = new MacroImpl(slideNamespace, slideNamespace.getConfig(), security, content, structure, namespace.getLockHelper());
			macro.delete(token, path);
			
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTORS_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_DESCRIPTOR_NAME, THREE_MINUTES, path);
			removeValueFromCache(CACHE_RESOURCE_EXISTANCE_NAME, -1, path);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Unable to delete: " + path, t);
			deleteXML = t instanceof ObjectNotFoundException || t instanceof RevisionDescriptorNotFoundException;
			if (!deleteXML) {
				rollbackTransaction(namespace);
				return false;
			}
		} finally {
			if (deleteXML) {
				deletetDefinitionFile(path);
			}
		}
		
		commitTransaction(namespace);
		return true;
	}
	
	public void deletetDefinitionFile(String path) {
		String workingDir = System.getProperty("user.dir");
		if (workingDir == null) {
			LOGGER.warning("Unknown directory for Slide store!");
			return;
		}
		
		workingDir = workingDir.concat("/store/metadata");
		String realPath = workingDir.concat(path).concat(DEFINITION_XML_FILE_ENDING);
		String parentFile = null;
		try {
			File xml = new File(realPath);
			if (xml != null && xml.exists()) {
				parentFile = xml.getParent();
				xml.delete();
			}
			
			if (parentFile == null) {
				int lastSlash = realPath.lastIndexOf(CoreConstants.SLASH);
				if (lastSlash != -1) {
					parentFile = realPath.substring(0, lastSlash);
				}
			}
			if (StringUtil.isEmpty(parentFile)) {
				LOGGER.warning("Parent file can not be found for: " + realPath);
				return;
			}
			
			String parentFileXMLURI = parentFile.concat(DEFINITION_XML_FILE_ENDING);
			File parentXMLFile = new File(parentFileXMLURI);
			if (parentXMLFile == null || !parentXMLFile.exists()) {
				LOGGER.warning("File " + parentFileXMLURI + " does not exist!");
				return;
			}
			
			Document parentXML = XmlUtil.getJDOMXMLDocument(new FileInputStream(parentXMLFile));
			if (parentXML == null) {
				LOGGER.warning("XML document was not loaded from: " + parentFileXMLURI);
				return;
			}
			
			Namespace n = Namespace.getNamespace(CoreConstants.EMPTY, CoreConstants.EMPTY);
			List<Element> children = XmlUtil.getElementsByXPath(parentXML.getRootElement(), "child", n);
			if (ListUtil.isEmpty(children)) {
				return;
			}
			
			List<Element> toRemove = new ArrayList<Element>();
			for (Element child: children) {
				Attribute uuri = child.getAttribute("uuri");
				if (path.equals(uuri.getValue())) {
					toRemove.add(child);
				}
			}
			
			if (toRemove.size() > 0) {
				for (Iterator<Element> toRemoveIter = toRemove.iterator(); toRemoveIter.hasNext();) {
					toRemoveIter.next().detach();
				}
				InputStream stream = StringHandler.getStreamFromString(XmlUtil.getPrettyJDOMDocument(parentXML));
				FileUtil.streamToFile(stream, parentXMLFile);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error deleting XML file (from metadata) for: " + path, e);
		}
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
	
	@SuppressWarnings("unchecked")
	public WebdavResources getResources(String path) {
		WebdavResources resources = new WebdavResources();
		path = getNormalizedPath(path);
		if (path == null) {
			return resources;
		}
		
		NamespaceAccessToken namespace = startTransaction(SlideAction.ROLLBACK);
		if (namespace == null) {
			return resources;
		}
		
		ObjectNode node = null;
		try {
			node = structure.retrieve(getContentToken(), path);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying to retrieve: " + path, e);
			if (e instanceof ObjectNotFoundException) {
				deletetDefinitionFile(((ObjectNotFoundException) e).getObjectUri());
			}
		} finally {
			rollbackTransaction(namespace);
		}
		
		if (node == null) {
			return resources; 
		}
		
		Vector<String> children = node.getChildren();
		if (ListUtil.isEmpty(children)) {
			return resources;
		}
		
		IWSlideService slideService = getServiceInstance(IWSlideService.class);
		for (String child: children) {
			try {
				String name = child;
				if (child.indexOf(CoreConstants.SLASH) != -1) {
					name = child.substring(child.lastIndexOf(CoreConstants.SLASH));
				}
				
				if (checkExistance(child)) {
					resources.addResource(name, slideService.getWebdavResourceAuthenticatedAsRoot(child));
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while adding resource: " + child + " to the " + path, e);
			}
		}
		
		return resources;
	}
	
	private NamespaceAccessToken getNamespace() {
		try {
			return Domain.accessNamespace(new SecurityToken(CoreConstants.EMPTY), Domain.getDefaultNamespace());
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Error getting namespace (instanceof "+NamespaceAccessToken.class+")", t);
		}
		return null;
	}
}