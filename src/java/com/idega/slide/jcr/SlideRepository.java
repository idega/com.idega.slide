package com.idega.slide.jcr;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.content.Content;
import org.apache.slide.security.Security;
import org.apache.slide.structure.Structure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.slide.webdavservlet.DomainConfig;

/**
 * <p>
 * Main implementation of the JCR Repository object to Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
@Scope("singleton")
@Service(SlideRepository.SPRING_BEAN_IDENTIFIER)
public class SlideRepository implements Repository {

	public static final String SPRING_BEAN_IDENTIFIER="slideRepository";
	
	/*private boolean initialized;
	private NamespaceAccessToken namespace;
	private Structure structure;
	private Content content;
	private Security security;
	private URL configurationURL*/
	
	private QueryManager defaultQueryManager;
	private ObservationManager defaultObservationManager;
	
	@Autowired 
	protected DomainConfig domainConfig;
	
	public SlideRepository(){
		//initialize();
	}
	
	protected void initialize(){
		getDomainConfig().initialize();
	}
	
	/*public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}*/

	public NamespaceAccessToken getNamespace() {
		//return namespace;
		return getDomainConfig().getNamespace();
	}

	/*public void setNamespace(NamespaceAccessToken namespace) {
		this.namespace = namespace;
	}*/

	public Structure getStructure() {
		//return structure;
		return getDomainConfig().getStructure();
	}

	/*public void setStructure(Structure structure) {
		this.structure = structure;
	}*/

	public Content getContent() {
		//return content;
		return getDomainConfig().getContent();
	}

	/*public void setContent(Content content) {
		this.content = content;
	}*/

	public Security getSecurity() {
		//return security;
		return getDomainConfig().getSecurity();
	}

	/*public void setSecurity(Security security) {
		this.security = security;
	}*/

	public String getDescriptor(String arg0) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String[] getDescriptorKeys() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Session login() throws LoginException, RepositoryException {
		initialize();
		Session session =  new SlideSession(this,(String)null);
		return session;
	}

	public Session login(Credentials arg0) throws LoginException,
			RepositoryException {
		initialize();
		Session session =  new SlideSession(this,arg0);
		return session;
	}

	public Session login(String workspaceName) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		initialize();
		Session session =  new SlideSession(this,workspaceName);
		return session;
	}

	public Session login(Credentials credentials, String workspaceName) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		initialize();
		Session session =  new SlideSession(this,credentials,workspaceName);
		return session;
	}

	public DomainConfig getDomainConfig() {
		return domainConfig;
	}

	public void setDomainConfig(DomainConfig domainConfig) {
		this.domainConfig = domainConfig;
	}

	public QueryManager getDefaultQueryManager() {
		if(defaultQueryManager==null){
			this.defaultQueryManager=new SlideQueryManager(this);
		}
		return defaultQueryManager;
	}

	public void setDefaultQueryManager(QueryManager defaultQueryManager) {
		this.defaultQueryManager = defaultQueryManager;
	}

	public ObservationManager getDefaultObservationManager() {
		if(defaultObservationManager==null){
			defaultObservationManager = new SlideObservationManager(this);
		}
		return defaultObservationManager;
	}

	public void setDefaultObservationManager(
			ObservationManager defaultObservationManager) {
		this.defaultObservationManager = defaultObservationManager;
	}

}
