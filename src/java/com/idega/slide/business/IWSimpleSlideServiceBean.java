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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.SlideConstants;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;

/**
 * Simple API of Slide implementation
 * @author valdas
 *
 */

@Service(SlideConstants.SIMPLE_SLIDE_SERVICE)
@Scope("singleton")
public class IWSimpleSlideServiceBean {

	private static final long serialVersionUID = 8065146986117553218L;
	private static final Logger LOGGER = Logger.getLogger(IWSimpleSlideServiceBean.class.getName());
	
	private NamespaceAccessToken namespace;
	private Structure structure;
	private Content content;
	private Security security;
	
	private AuthenticationBusiness authenticationBusiness = null;
	
	private boolean initialized = false;
	
	private synchronized void initializeSimpleSlideServiceBean() {
		if (initialized) {
			return;
		}
		
		initialized = true;
		try {
			namespace = Domain.accessNamespace(new SecurityToken(CoreConstants.EMPTY), Domain.getDefaultNamespace());
			structure = namespace.getStructureHelper();
			content = namespace.getContentHelper();
			security = namespace.getSecurityHelper();
		} catch(Exception e) {
			initialized = false;
			e.printStackTrace();
		}
	}
	
	private AuthenticationBusiness getAuthenticationBusiness() {
		if (authenticationBusiness == null) {
			try {
				authenticationBusiness = (AuthenticationBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
																																AuthenticationBusiness.class);
			} catch (IBOLookupException e) {
				e.printStackTrace();
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
			e.printStackTrace();
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
			NodeRevisionDescriptor revisionDescriptor = new NodeRevisionDescriptor(lastRevision, NodeRevisionDescriptors.MAIN_BRANCH, new Vector(), new Hashtable());
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if (closeStream) {
				IOUtil.closeInputStream(stream);
			}
			finishTransaction();
		}
		
		return false;
	}
	
	protected boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user, boolean closeStream) throws Exception {
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
	
	protected boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user) throws Exception {
		return upload(stream, uploadPath, fileName, contentType, user, true);
	}

	protected InputStream getInputStream(String pathToFile) {
		if (StringUtil.isEmpty(pathToFile)) {
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
		
		NodeRevisionDescriptors revisionDescriptors = null;
		try {
			revisionDescriptors = content.retrieve(rootToken, pathToFile);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error retrieving revision descriptors", e);
		}
		if (revisionDescriptors == null || !revisionDescriptors.hasRevisions()) {
			LOGGER.log(Level.SEVERE, "There are no revision descriptors or no revisions for: " + pathToFile);
			rollbackTransaction();
			return null;
		}
		
		NodeRevisionDescriptor revisionDescriptor = null;
		try {
			revisionDescriptor = content.retrieve(rootToken, revisionDescriptors);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error retrieving revision descriptor", e);
		}
		if (revisionDescriptor == null) {
			rollbackTransaction();
		}
		
		try {
			return content.retrieve(rootToken, revisionDescriptors, revisionDescriptor).streamContent();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting InputStream for: " + pathToFile, e);
		} finally {
			finishTransaction();
		}
		
		return null;
	}
	
	private boolean startTransaction() {
		try {
			namespace.begin();
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Cannot start user transaction", e);
			return false;
		}
		
		return true;
	}
	
	private boolean rollbackTransaction() {
		try {
			namespace.rollback();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Cannot rollback user transaction", e);
			return false;
		}
		return true;
	}
	
	private boolean finishTransaction() {
		try {
			namespace.commit();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Cannot finish user transaction", e);
			return false;
		}
		return true;
	}
	
}
