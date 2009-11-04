package com.idega.slide.business;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.SlideTokenImpl;
import org.apache.slide.content.Content;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.security.Security;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.Structure;
import org.apache.slide.structure.SubjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.webdavservlet.DomainConfig;
import com.idega.user.data.User;
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
public class IWSimpleSlideServiceImp implements IWSimpleSlideService {

	private static final long serialVersionUID = 8065146986117553218L;
	private static final Logger LOGGER = Logger.getLogger(IWSimpleSlideServiceImp.class.getName());
	
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
			try {
				authenticationBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), AuthenticationBusiness.class);
			} catch (IBOLookupException e) {
				LOGGER.log(Level.WARNING, "Error getting EJB bean:" + AuthenticationBusiness.class, e);
			}
		}
		return authenticationBusiness;
	}
	
	private SlideToken getSlideToken() {
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
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	private boolean doUploading(InputStream stream, SlideToken token, String uploadPath, String contentType, User user, boolean closeStream) {
		//	TODO: there is problem in uploadPath: API doesn't "see" different in case: /files/themes/ and /files/Themes/
		try {
			NodeRevisionNumber lastRevision = null;
			try {
				structure.retrieve(token, uploadPath);
				lastRevision = content.retrieve(token, uploadPath).getLatestRevision();
			}
			catch (ObjectNotFoundException e) {
				SubjectNode subject = new SubjectNode();
				//	Create object
				structure.create(token, subject, uploadPath);
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
					new Hashtable());
			revisionDescriptor.setResourceType(CoreConstants.EMPTY);
			revisionDescriptor.setSource(CoreConstants.EMPTY);
			revisionDescriptor.setContentLanguage(Locale.ENGLISH.getLanguage());
			revisionDescriptor.setLastModified(now.getDate());
			revisionDescriptor.setETag(computeEtag(uploadPath, revisionDescriptor));
			revisionDescriptor.setCreationDate(now.getDate());
			
			//	Owner
			String creator = ((SubjectNode)security.getPrincipal(token)).getPath().lastSegment();
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
			return true;
		} catch(Throwable e) {
			LOGGER.log(Level.SEVERE, "Error while uploading!", e);
		}
		finally {
			if (closeStream) {
				IOUtil.closeInputStream(stream);
			}
			finishTransaction();
		}
		
		return false;
	}
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user, boolean closeStream) throws Exception {
		if (stream == null || uploadPath == null || fileName == null) {
			return false;
		}
		
		initializeSimpleSlideServiceBean();
		if (!initialized) {
			return false;
		}
		
		SlideToken token = getSlideToken();
		if (!startTransaction()) {
			return false;
		}
		
		boolean uploadingResult = doUploading(stream, token, uploadPath + fileName, contentType, user, closeStream);
		if (!uploadingResult) {
			rollbackTransaction();
		}
		
		return uploadingResult;
	}
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user) throws Exception {
		return upload(stream, uploadPath, fileName, contentType, user, true);
	}

	private NodeRevisionDescriptors getNodeRevisionDescriptors(String pathToNode) {
		if (StringUtil.isEmpty(pathToNode)) {
			return null;
		}
		
		initializeSimpleSlideServiceBean();
		if (!initialized) {
			return null;
		}
		
		SlideToken rootToken = getSlideToken();
		if (rootToken == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		if (pathToNode.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
			pathToNode = StringHandler.replace(pathToNode, CoreConstants.WEBDAV_SERVLET_URI, CoreConstants.EMPTY);
		}
		
		try {
			return content.retrieve(rootToken, pathToNode);
		} catch (Throwable e) {
			LOGGER.warning("Unable to retrieve requested object: " + pathToNode);
		} finally {
			rollbackTransaction();
		}
		
		return null;
	}
	
	public boolean checkExistance(String pathToFile) {
		NodeRevisionDescriptors revisionDescriptors = getNodeRevisionDescriptors(pathToFile);
		return revisionDescriptors == null ? Boolean.FALSE : Boolean.TRUE;
	}
	
	private NodeRevisionContent getNodeContent(String pathToFile) {
		NodeRevisionDescriptors revisionDescriptors = getNodeRevisionDescriptors(pathToFile);
		if (revisionDescriptors == null || !revisionDescriptors.hasRevisions()) {
			return null;
		}
		
		SlideToken rootToken = getSlideToken();
		if (rootToken == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		NodeRevisionDescriptor revisionDescriptor = null;
		try {
			revisionDescriptor = content.retrieve(rootToken, revisionDescriptors);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Error retrieving revision descriptor", e);
			rollbackTransaction();
		}
		
		try {
			return content.retrieve(rootToken, revisionDescriptors, revisionDescriptor);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Error getting InputStream for: " + pathToFile, e);
			rollbackTransaction();
		} finally {
			finishTransaction();
		}
		
		return null;
	}
	
	public InputStream getInputStream(String pathToFile) {
		NodeRevisionContent nodeContent = getNodeContent(pathToFile);
		
		if (nodeContent == null) {
			return null;
		}
		
		if (!startTransaction()) {
			return null;
		}
		
		try {
			return nodeContent.streamContent();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Error getting InputStream for: " + pathToFile, e);
			rollbackTransaction();
		} finally {
			finishTransaction();
		}
		
		return null;
	}
	
	public boolean setContent(String pathToFile, InputStream contentStream) {
		NodeRevisionContent nodeContent = getNodeContent(pathToFile);
		
		if (nodeContent == null) {
			return Boolean.FALSE;
		}
		
		if (!startTransaction()) {
			return Boolean.FALSE;
		}
		
		try {
			nodeContent.setContent(contentStream);
			return Boolean.TRUE;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Error setting content InputStream for: " + pathToFile, e);
			rollbackTransaction();
		} finally {
			finishTransaction();
		}
		
		return Boolean.FALSE;
	}
	
	private boolean startTransaction() {
		try {
			namespace.begin();
		} catch(Throwable e) {
			LOGGER.log(Level.SEVERE, "Cannot start user transaction", e);
			return false;
		}
		
		return true;
	}
	
	private boolean rollbackTransaction() {
		try {
			namespace.rollback();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Cannot rollback user transaction", e);
			return false;
		}
		return true;
	}
	
	private boolean finishTransaction() {
		try {
			namespace.commit();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Cannot finish user transaction", e);
			return false;
		}
		return true;
	}
}