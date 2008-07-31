package com.idega.slide.jcr;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.content.Content;
import org.apache.slide.security.Security;
import org.apache.slide.structure.Structure;

import com.idega.util.CoreConstants;

public class SlideRepository implements Repository {

	private boolean initialized;
	private NamespaceAccessToken namespace;
	private Structure structure;
	private Content content;
	private Security security;

	public SlideRepository(){
		initialize();
	}
	
	private synchronized void initialize() {
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
	
	
	
	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public NamespaceAccessToken getNamespace() {
		return namespace;
	}

	public void setNamespace(NamespaceAccessToken namespace) {
		this.namespace = namespace;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public String getDescriptor(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getDescriptorKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session login() throws LoginException, RepositoryException {
		Session session =  new SlideSession(this,(String)null);
		try {
			this.namespace.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

	public Session login(Credentials arg0) throws LoginException,
			RepositoryException {
		Session session =  new SlideSession(this,arg0);
		try {
			this.namespace.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

	public Session login(String workspaceName) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		Session session =  new SlideSession(this,workspaceName);
		try {
			this.namespace.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

	public Session login(Credentials credentials, String workspaceName) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		Session session =  new SlideSession(this,credentials,workspaceName);
		try {
			this.namespace.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

}
