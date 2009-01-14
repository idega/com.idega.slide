package com.idega.slide.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.SlideTokenImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 * Main implementation of the JCR Session object to Slide
 * </p>
 *  Last modified: $Date: 2009/01/14 13:56:12 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class SlideSession implements Session {

	private SlideRepository slideRepository;
	SlideToken token;
	String workspaceName;
	private Credentials credentials;

	public SlideSession(SlideRepository slideRepository, Credentials credentials) {
		this(slideRepository,credentials,null);
	}

	public SlideSession(SlideRepository slideRepository, String workspaceName) {
		this(slideRepository,null,workspaceName);
	}

	public SlideSession(SlideRepository slideRepository, Credentials credentials,
			String workspaceName) {
		this.slideRepository=slideRepository;
		this.credentials=credentials;
		this.workspaceName=workspaceName;
		beginTransaction();
	}

	private void beginTransaction() {
		try {
			this.slideRepository.getNamespace().begin();
		} catch (NotSupportedException e) {
			if(SlideNode.LOGLEVEL==SlideNode.LOGLEVEL_DEBUG){
				e.printStackTrace();
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SlideRepository getSlideRepository() {
		return slideRepository;
	}

	public void setSlideRepository(SlideRepository slideRepository) {
		this.slideRepository = slideRepository;
	}

	public void addLockToken(String arg0) {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void checkPermission(String arg0, String arg1)
			throws AccessControlException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void exportDocumentView(String arg0, ContentHandler arg1,
			boolean arg2, boolean arg3) throws PathNotFoundException,
			SAXException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void exportDocumentView(String arg0, OutputStream arg1,
			boolean arg2, boolean arg3) throws IOException,
			PathNotFoundException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void exportSystemView(String arg0, ContentHandler arg1,
			boolean arg2, boolean arg3) throws PathNotFoundException,
			SAXException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void exportSystemView(String arg0, OutputStream arg1, boolean arg2,
			boolean arg3) throws IOException, PathNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public Object getAttribute(String arg0) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String[] getAttributeNames() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public ContentHandler getImportContentHandler(String arg0, int arg1)
			throws PathNotFoundException, ConstraintViolationException,
			VersionException, LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Item getItem(String arg0) throws PathNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String[] getLockTokens() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getNamespacePrefix(String arg0) throws NamespaceException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String[] getNamespacePrefixes() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getNamespaceURI(String arg0) throws NamespaceException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Node getNodeByUUID(String arg0) throws ItemNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Repository getRepository() {
		return this.slideRepository;
	}

	public Node getRootNode() throws RepositoryException {
		return new SlideNode(this,"/",false);
	}

	public String getUserID() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public ValueFactory getValueFactory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Workspace getWorkspace() {
		return new SlideWorkspace(this);
	}

	public boolean hasPendingChanges() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Session impersonate(Credentials arg0) throws LoginException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void importXML(String arg0, InputStream arg1, int arg2)
			throws IOException, PathNotFoundException, ItemExistsException,
			ConstraintViolationException, VersionException,
			InvalidSerializedDataException, LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public boolean isLive() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean itemExists(String arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void logout() {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void move(String arg0, String arg1) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void refresh(boolean arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void removeLockToken(String arg0) {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException,
			RepositoryException {
		
		try {
			getSlideRepository().getNamespace().commit();
			getSlideRepository().getNamespace().begin();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setNamespacePrefix(String arg0, String arg1)
			throws NamespaceException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public SlideToken getToken() {
		if(token==null){
			String userPrincipals = null;
			
			/*try {
				AuthenticationBusiness ab = getAuthenticationBusiness();
				userPrincipals = ab.getRootUserCredentials().getUserName();
			} catch(Exception e) {
				e.printStackTrace();
			}*/
			
			if(credentials!=null){
				if(credentials instanceof SimpleCredentials){
					SimpleCredentials simpleCredentials = (SimpleCredentials)credentials;
					char[] passwd = simpleCredentials.getPassword();
					String userId = simpleCredentials.getUserID();
					userPrincipals=userId;
				}
			}
			else{
				//userPrincipals="root";
				userPrincipals="unauthenticated";
			}
			token = new SlideTokenImpl(new CredentialsToken(userPrincipals));
			token.setForceStoreEnlistment(true);
		}
		return token;
	}

	public void setToken(SlideToken token) {
		this.token = token;
	}

}
