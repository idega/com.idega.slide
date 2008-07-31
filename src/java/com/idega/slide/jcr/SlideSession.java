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
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.SlideTokenImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

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
	}

	public SlideRepository getSlideRepository() {
		return slideRepository;
	}

	public void setSlideRepository(SlideRepository slideRepository) {
		this.slideRepository = slideRepository;
	}

	public void addLockToken(String arg0) {
		// TODO Auto-generated method stub

	}

	public void checkPermission(String arg0, String arg1)
			throws AccessControlException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void exportDocumentView(String arg0, ContentHandler arg1,
			boolean arg2, boolean arg3) throws PathNotFoundException,
			SAXException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void exportDocumentView(String arg0, OutputStream arg1,
			boolean arg2, boolean arg3) throws IOException,
			PathNotFoundException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void exportSystemView(String arg0, ContentHandler arg1,
			boolean arg2, boolean arg3) throws PathNotFoundException,
			SAXException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void exportSystemView(String arg0, OutputStream arg1, boolean arg2,
			boolean arg3) throws IOException, PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentHandler getImportContentHandler(String arg0, int arg1)
			throws PathNotFoundException, ConstraintViolationException,
			VersionException, LockException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Item getItem(String arg0) throws PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getLockTokens() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespacePrefix(String arg0) throws NamespaceException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getNamespacePrefixes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI(String arg0) throws NamespaceException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getNodeByUUID(String arg0) throws ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Repository getRepository() {
		// TODO Auto-generated method stub
		return this.slideRepository;
	}

	public Node getRootNode() throws RepositoryException {
		// TODO Auto-generated method stub
		return new SlideNode(this,"/",false);
	}

	public String getUserID() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueFactory getValueFactory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Workspace getWorkspace() {
		// TODO Auto-generated method stub
		return new SlideWorkspace(this);
	}

	public boolean hasPendingChanges() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public Session impersonate(Credentials arg0) throws LoginException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void importXML(String arg0, InputStream arg1, int arg2)
			throws IOException, PathNotFoundException, ItemExistsException,
			ConstraintViolationException, VersionException,
			InvalidSerializedDataException, LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public boolean isLive() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean itemExists(String arg0) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public void logout() {
		// TODO Auto-generated method stub

	}

	public void move(String arg0, String arg1) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void refresh(boolean arg0) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	public void removeLockToken(String arg0) {
		// TODO Auto-generated method stub

	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException,
			RepositoryException {
		
		try {
			getSlideRepository().getNamespace().commit();
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
		}
	}

	public void setNamespacePrefix(String arg0, String arg1)
			throws NamespaceException, RepositoryException {
		// TODO Auto-generated method stub

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
