/*
 * $Id: IWSlideServiceBean.java,v 1.69 2009/05/20 14:18:49 valdas Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.security.Security;
import org.apache.slide.webdav.WebdavServlet;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import org.apache.webdav.lib.WebdavState;
import org.apache.webdav.lib.properties.AclProperty;
import org.apache.webdav.lib.util.WebdavStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.ZipInstaller;
import com.idega.servlet.filter.RequestProvider;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.schema.SlideSchemaCreator;
import com.idega.slide.util.AccessControlEntry;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavLocalResource;
import com.idega.slide.util.WebdavOutputStream;
import com.idega.slide.util.WebdavRootResource;
import com.idega.slide.webdavservlet.DomainConfig;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This is the main bean for accessing system wide information about the slide
 * store.
 * </p>
 * 
 * Last modified: $Date: 2009/05/20 14:18:49 $ by $Author: valdas $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>,<a
 *         href="mailto:tryggvi@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.69 $
 */
public class IWSlideServiceBean extends IBOServiceBean implements IWSlideService, IWSlideChangeListener {

	private static final long serialVersionUID = -4520443825572949293L;

	// listeners and caching
	private List<IWSlideChangeListener> iwSlideChangeListeners = null;
	private IWSlideChangeListener[] iwSlideChangeListenersArray = null;
	private Map<String, List<String>> childPathsCacheMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> childPathsExcludingFolderAndHiddenFilesCacheMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> childFolderPathsCacheMap = new HashMap<String, List<String>>();

	protected static final String FILE_SERVER_URI = CoreConstants.WEBDAV_SERVLET_URI + CoreConstants.PATH_FILES_ROOT;
	protected static final String USER_SERVLET_URI = CoreConstants.WEBDAV_SERVLET_URI + "/users";

	protected static final String PATH_BLOCK_HOME = CoreConstants.PATH_FILES_ROOT + "/cms";
	protected static final String PATH_USERS_HOME_FOLDERS = CoreConstants.PATH_FILES_ROOT + "/users";
	protected static final String PATH_GROUPS_HOME_FOLDERS = CoreConstants.PATH_FILES_ROOT + "/groups";

	protected static final String FOLDER_NAME_PUBLIC = "/public";
	protected static final String FOLDER_NAME_SHARED = "/shared";
	protected static final String FOLDER_NAME_DROPBOX = "/dropbox";

	protected Map<String, String> lastUniqueFileNameScopeMap = new HashMap<String, String>();
	protected String lastGlobalUniqueFileName = null;

	private Security security = null;

	private static final Logger LOGGER = Logger.getLogger(IWSlideServiceBean.class.getName());

	@Autowired
	private IWSimpleSlideService simpleSlideService;
	
	public IWSlideServiceBean() {
		super();
	}

	/**
	 * <p>
	 * Gets the URI for the root of the slide repository. The repository is by
	 * default mapped on '/content' under the web application.<br/> This method
	 * returns the context path for the application so if it is e.g. mapped
	 * under '/cms' this method returns '/cms/content'. If the webapplication is
	 * mapped on '/' the method returns '/content'
	 * </p>
	 * 
	 * @param path
	 * @return
	 */
	public String getWebdavServerURI() {
		String appContext = getIWMainApplication().getApplicationContextURI();
		if (appContext.endsWith(CoreConstants.SLASH)) {
			appContext = appContext.substring(0, appContext.lastIndexOf(CoreConstants.SLASH));
		}
		return appContext + CoreConstants.WEBDAV_SERVLET_URI;
	}

	/**
	 * <p>
	 * Gets the URL from with a path in the filesystem (e.g.) if the given path
	 * is '/files/public/myfile.pdf' then this method returns
	 * 'http://[hostname]:[port]/[contextpath]/content/files/public/myfile.pdf'
	 * </p>
	 * 
	 * @param path
	 * @return
	 */
	public HttpURL getWebdavServerURL(String path) {
		return getWebdavServerURL(null, path);
	}

	public HttpURL getWebdavServerURL() {
		return getWebdavServerURL(null, null);
	}

	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential) {
		return getWebdavServerURL(credential, null);
	}

	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential, String path) {
		return getWebdavServerURL(credential, path, getWebdavServerURI(), Boolean.TRUE);
	}

	/**
	 * Gets the root url for the webdav server with authentication
	 * 
	 * @return
	 */
	private HttpURL getWebdavServerURL(UsernamePasswordCredentials credential, String path, String servletPath, boolean addSessionId) {
		try {
			String server = getIWApplicationContext().getDomain().getURL();
			if (server == null) {
				return null;
			}
			
			int port = 80;
			boolean https = false;
			if (server.endsWith(CoreConstants.SLASH)) {
				server = server.substring(0, server.lastIndexOf(CoreConstants.SLASH));
			}
			if (server.startsWith("http://")) {
				server = server.substring(7, server.length());
			}
			if (server.startsWith("https://")) {
				if (getIWMainApplication().getSettings().getBoolean("slide.allow.local.https")) {
					// https protocol when to slide is only enabled when this property is set
					https = true;
				}
				server = server.substring(8, server.length());
			}
			if (server.indexOf(CoreConstants.COLON) != -1) {
				String sPort = server.substring(server.indexOf(CoreConstants.COLON) + 1, server.length());
				port = Integer.parseInt(sPort);
				server = server.substring(0, server.indexOf(CoreConstants.COLON));
			}

			String rootPath = servletPath;
			String realPath = rootPath;
			if (path != null) {
				realPath = rootPath + path;
			}

			HttpURL hrl = https ? new HttpsURL(server, port, realPath) : new HttpURL(server, port, realPath);
			if (credential != null) {
				hrl.setUserinfo(credential.getUserName(), credential.getPassword());
			}
			
			return hrl;
		} catch (URIException e) {
			throw new IBORuntimeException(e);
		}
	}

	/**
	 * Gets resource for the webdav server with authentication
	 * 
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials,
			String path) {
		try {
			return new WebdavFile(getWebdavServerURL(credentials, path));
		} catch (HttpException e) {
			throw new IBORuntimeException(e);
		}
	}

	/**
	 * Gets the root resource for the webdav server with authentication
	 * 
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials) {
		return getWebdavFile(credentials, null);
	}

	/**
	 * Gets the root resource for the webdav server without any authentication
	 * 
	 * @return
	 */
	public WebdavFile getWebdavFile() {
		return getWebdavFile(null, null);
	}

	/**
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws IBOLookupException
	 */
	public UsernamePasswordCredentials getRootUserCredentials()
			throws IBOLookupException, RemoteException {
		return getAuthenticationBusiness().getRootUserCredentials();
	}

	/**
	 * Auto creates the Slide sql schema structure
	 */
	public void createSlideSchemas() {
		try {
			new SlideSchemaCreator().createSchemas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Returns the WebdavResource for the "/" or root of the WebDav server.
	 * </p>
	 * 
	 * @param credentials
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws RemoteException
	 */
	public WebdavResource getWebdavExternalRootResource(UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException {
		return getWebdavExtendedResource(null, credentials, Boolean.FALSE);
	}

	public WebdavResource getWebdavResource(String path, UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException {
		return getWebdavExtendedResource(path, credentials);
	}

	public WebdavExtendedResource getWebdavExtendedResource(String path, UsernamePasswordCredentials credentials) throws HttpException, IOException,
		RemoteException {
		return getWebdavExtendedResource(path, credentials, Boolean.TRUE);
	}
	
	private boolean isLocalResourceEnabled() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("local_slide_resource", Boolean.FALSE);
	}
	
	public WebdavExtendedResource getWebdavExtendedResource(String path, UsernamePasswordCredentials credentials, boolean localResource) throws HttpException,
			IOException, RemoteException, RemoteException {

		HttpURL url = getWebdavServerURL(credentials, getPath(path), getWebdavServerURI(), localResource);
		if (url == null) {
			throw new IOException("[IWSlideService] WebdavServerURL could not be retrieved for " + path + ", using credentials: " + credentials);
		}
		
		WebdavExtendedResource resource = null;
		
		if (localResource && isLocalResourceEnabled()) {
			if (!Domain.isInitialized()) {
				DomainConfig domainConfig = ELUtil.getInstance().getBean(DomainConfig.SPRING_BEAN_IDENTIFIER);
				domainConfig.initialize();
			}
			
			HttpSession currentSession = getCurrentSession();
			if (currentSession != null) {
				url.setQuery(CoreConstants.PARAMETER_SESSION_ID.toLowerCase(), currentSession.getId());
			}
			
			try {
				resource = new WebdavLocalResource(getHttpClient(url, credentials));
				resource.setHttpURL(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (resource == null) {
			resource = new WebdavExtendedResource(url);
		}
		
		return resource;
	}

	@SuppressWarnings("deprecation")
	private HttpClient getHttpClient(HttpURL url, UsernamePasswordCredentials credentials) throws Exception {
		HttpSession currentSession = getCurrentSession();
		
		HttpState state = new WebdavState();
		AuthScope authScope = new AuthScope(url.getHost(), url.getPort());
		state.setCredentials(authScope, credentials);
		if (currentSession != null) {
			IWTimestamp iwExpires = new IWTimestamp(System.currentTimeMillis());
			iwExpires.setMinute(iwExpires.getMinute() + 30);
			Date expires = new Date(iwExpires.getTimestamp().getTime());
			
			boolean secure = url instanceof HttpsURL;
			
			Cookie cookie = new Cookie(url.getHost(), CoreConstants.PARAMETER_SESSION_ID, currentSession.getId(), CoreConstants.SLASH, expires, secure);
			state.addCookie(cookie);
		}
		
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		client.setState(state);

        HostConfiguration hostConfig = client.getHostConfiguration();
        hostConfig.setHost(url);
        
        Credentials hostCredentials = null;

        if (credentials == null) {
            String userName = url.getUser();
            if (userName != null && userName.length() > 0) {
                hostCredentials = new UsernamePasswordCredentials(userName, url.getPassword());
            }
        }

        if (hostCredentials != null) {
            HttpState clientState = client.getState();
            clientState.setCredentials(null, url.getHost(), hostCredentials);
            clientState.setAuthenticationPreemptive(true);
        }
        
        return client;
	}
	
	private HttpSession getCurrentSession() {
		try {
			RequestProvider requestProvider = ELUtil.getInstance().getBean(RequestProvider.class);
			return requestProvider.getRequest().getSession(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the WebdavResource at the given path and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path) throws HttpException, IOException {
		return getWebdavResource(path, getRootUserCredentials());
	}

	/**
	 * Returns the WebdavResource at path "/" and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException {
		return getWebdavResourceAuthenticatedAsRoot(null);
	}
	
	private WebdavResource getWebdavExternalResourceAuthenticatedAsRoot(String path) throws HttpException, IOException {
		return getWebdavExtendedResource(path, getRootUserCredentials(), Boolean.FALSE);
	}
		
	public WebdavResource getWebdavExternalResourceAuthenticatedAsRoot() throws HttpException, IOException {
		return getWebdavExternalResourceAuthenticatedAsRoot(null);
	}

	/**
	 * <p>
	 * Gets the URI from with a path in the filesystem (e.g.) if the given path
	 * is '/files/public/myfile.pdf' then this method returns
	 * '/[contextpath]/content/files/public/myfile.pdf'
	 * </p>
	 * 
	 * @param path
	 * @return
	 */
	public String getURI(String path) throws RemoteException {
		if (path.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
			// to avoid /content/content/
			path = path.substring(CoreConstants.WEBDAV_SERVLET_URI.length());
		}
		return getWebdavServerURI() + ((path.startsWith(CoreConstants.SLASH)) ? CoreConstants.EMPTY : CoreConstants.SLASH) + path;
	}

	public String getPath(String uri) throws RemoteException {
		String uriPrefix = getWebdavServerURI();
		if (uri == null) {
			return null;
		} else {
			return ((uri.startsWith(uriPrefix)) ? uri.substring(uriPrefix
					.length()) : uri);
		}
	}

	public boolean getExistence(String path) throws HttpException, IOException {
		if (path == null) {
			return false;
		}
		
		try {
			return getSimpleSlideService().checkExistance(path);
		} catch (Exception e) {}
		
		try {
			String pathToCheck = ((path.startsWith(getWebdavServerURI())) ? path : getURI(path));
			Enumeration prop = getWebdavExternalResourceAuthenticatedAsRoot().propfindMethod(pathToCheck, WebdavResource.DISPLAYNAME);
			return !(prop == null || !prop.hasMoreElements());
		} catch (HttpException e) {
			if (e.getReasonCode() == WebdavStatus.SC_NOT_FOUND) {
				return false;
			} else {
				throw e;
			}
		}
	}

	public boolean generateUserFolders(String loginName) throws HttpException, IOException {
		boolean returner = false;

		if (loginName != null && !getExistence(getUserHomeFolderPath(loginName))) {
			WebdavResource rootFolder = getWebdavResourceAuthenticatedAsRoot();

			String userFolderPath = getURI(getUserHomeFolderPath(loginName));
			rootFolder.mkcolMethod(userFolderPath);
			rootFolder.mkcolMethod(userFolderPath + FOLDER_NAME_DROPBOX);
			rootFolder.mkcolMethod(userFolderPath + FOLDER_NAME_PUBLIC);

			// try {
			// logOutAcesForUserFolders(loginName);
			// }
			// catch (HttpException e1) {
			// e1.printStackTrace();
			// }
			// catch (IOException e1) {
			// e1.printStackTrace();
			// }
			//			
			// try {
			// AuthenticationBusiness aBusiness = getAuthenticationBusiness();
			//				
			//				
			// AclProperty userFolderProperty =
			// rootFolder.aclfindMethod(userFolderPath);
			// Ace[] userFolderProperties = (userFolderProperty==null)?new
			// Ace[0]:userFolderProperty.getAces();
			//				
			// int homeLength = userFolderProperties.length;
			// Ace[] homeFolderAce = new Ace[homeLength+1];
			// System.arraycopy(userFolderProperties,0,homeFolderAce,0,homeLength);
			//				
			// Ace userAllPrivilege = new Ace(aBusiness.getUserURI(loginName));
			// userAllPrivilege.addPrivilege(Privilege.ALL);
			// homeFolderAce[homeLength] = userAllPrivilege;
			// rootFolder.aclMethod(userFolderPath,homeFolderAce);
			//				
			//				
			//				
			// AclProperty userDropboxProperty =
			// rootFolder.aclfindMethod(userFolderPath+FOLDER_NAME_DROPBOX);
			// Ace[] userDropboxProperties = (userDropboxProperty==null)?new
			// Ace[0]:userDropboxProperty.getAces();
			//				
			// int dropboxLength = userDropboxProperties.length;
			// Ace[] dropboxAce = new Ace[dropboxLength+1];
			// System.arraycopy(userDropboxProperties,0,dropboxAce,0,dropboxLength);
			//				
			// dropboxAce[dropboxLength] = new
			// Ace(aBusiness.getRoleURI(IWSlideConstants.ROLENAME_USERS));
			// dropboxAce[dropboxLength].addPrivilege(Privilege.WRITE);
			// rootFolder.aclMethod(userFolderPath+FOLDER_NAME_DROPBOX,dropboxAce);
			//				
			//				
			// AclProperty userPublicFolderProperty =
			// rootFolder.aclfindMethod(userFolderPath+FOLDER_NAME_PUBLIC);
			// Ace[] userPublicFolderProperties =
			// (userPublicFolderProperty==null)?new
			// Ace[0]:userPublicFolderProperty.getAces();
			//				
			// int publicLength = userPublicFolderProperties.length;
			// Ace[] publicAce = new Ace[publicLength+1];
			// System.arraycopy(userPublicFolderProperties,0,publicAce,0,publicLength);
			//				
			// publicAce[publicLength] = new
			// Ace(IWSlideConstants.SUBJECT_URI_ALL);
			// publicAce[publicLength].addPrivilege(Privilege.READ);
			// publicAce[publicLength].setInherited(true);
			// rootFolder.aclMethod(userFolderPath+FOLDER_NAME_PUBLIC,publicAce);
			// }
			// catch (IBOLookupException e) {
			// e.printStackTrace();
			// }
			// catch (HttpException e) {
			// e.printStackTrace();
			// }
			// catch (RemoteException e) {
			// e.printStackTrace();
			// }
			// catch (IOException e) {
			// e.printStackTrace();
			// }
			//			
			//			
			// try {
			// logOutAcesForUserFolders(loginName);
			// }
			// catch (HttpException e1) {
			// e1.printStackTrace();
			// }
			// catch (IOException e1) {
			// e1.printStackTrace();
			// }
			//			
			// // if(transactionStarted){
			// // returner = rootFolder.commitTransaction();
			// // }
			rootFolder.close();
		}
		// else {
		// logOutAcesForUserFolders(loginName);
		// }

		try {
			updateUserFolderPrivileges(loginName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returner;
	}

	public void updateUserFolderPrivileges(String loginName) throws IOException, IOException {

		String userFolderPath = getURI(getUserHomeFolderPath(loginName));

		AuthenticationBusiness aBusiness = getAuthenticationBusiness();
		String userPrincipal = aBusiness.getUserURI(loginName);

		// user folder
		AccessControlList userFolderList = getAccessControlList(userFolderPath);
		// should be 'all' for the user himself
		List<AccessControlEntry> userFolderUserACEs = userFolderList.getAccessControlEntriesForUsers();
		AccessControlEntry usersPositiveAce = null;
		AccessControlEntry usersNegativeAce = null;
		boolean madeChangesToUserFolderList = false;
		// Find the ace
		for (Iterator<AccessControlEntry> iter = userFolderUserACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = iter.next();
			if (ace.getPrincipal().equals(userPrincipal) && !ace.isInherited()) {
				if (ace.isNegative()) {
					usersNegativeAce = ace;
				} else {
					usersPositiveAce = ace;
				}
			}
		}
		if (usersPositiveAce == null) {
			usersPositiveAce = new AccessControlEntry(userPrincipal, false, false, false, null, AccessControlEntry.PRINCIPAL_TYPE_USER);
			userFolderList.add(usersPositiveAce);
		}

		if (!usersPositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_ALL)) {
			if (usersNegativeAce != null && usersNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_ALL)) {
				// do nothing becuse this is not ment to reset permissions but
				// to set them in the first
				// first place and update for legacy reasons. If Administrator
				// has closed someones user folder
				// for some reason, this is not supposed to reset that.
			} else {
				usersPositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_ALL);
				madeChangesToUserFolderList = true;

				// temporary at least:
				usersPositiveAce.setInherited(false);
				usersPositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if (madeChangesToUserFolderList) {
			storeAccessControlList(userFolderList);
		}

		// dropbox
		updateUsersDropboxPrivileges(userFolderPath);

		// public folder
		updateUsersPublicFolderPrivileges(userFolderPath);

	}

	/**
	 * @param userFolderPath
	 * @throws HttpException
	 * @throws IOException
	 */
	private void updateUsersDropboxPrivileges(String userFolderPath)throws HttpException, IOException {
		// dropbox
		AccessControlList dropboxList = getAccessControlList(userFolderPath + FOLDER_NAME_DROPBOX);
		// should be 'write' for authenticated

		List<AccessControlEntry> publicFolderStandardACEs = dropboxList.getAccessControlEntriesForUsers();
		String principalAuthenticated = IWSlideConstants.SUBJECT_URI_AUTHENTICATED;
		AccessControlEntry prAuthenticatedPositiveAce = null;
		AccessControlEntry prAuthenticatedNegativeAce = null;
		boolean madeChangesToPublicFolderList = false;
		// Find the ace
		for (Iterator<AccessControlEntry> iter = publicFolderStandardACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = iter.next();
			if (ace.getPrincipal().equals(principalAuthenticated) && !ace.isInherited()) {
				if (ace.isNegative()) {
					prAuthenticatedNegativeAce = ace;
				} else {
					prAuthenticatedPositiveAce = ace;
				}
			}
		}
		if (prAuthenticatedPositiveAce == null) {
			prAuthenticatedPositiveAce = new AccessControlEntry(principalAuthenticated, false, false, false, null, AccessControlEntry.PRINCIPAL_TYPE_STANDARD);
			dropboxList.add(prAuthenticatedPositiveAce);
		}

		if (!prAuthenticatedPositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_WRITE)) {
			if (prAuthenticatedNegativeAce != null && prAuthenticatedNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_WRITE)) {
				// do nothing becuse this is not ment to reset permissions but
				// to set them in the first
				// first place and update for legacy reasons.
			} else {
				prAuthenticatedPositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_WRITE);
				madeChangesToPublicFolderList = true;

				// temporary at least:
				prAuthenticatedPositiveAce.setInherited(false);
				prAuthenticatedPositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if (madeChangesToPublicFolderList) {
			storeAccessControlList(dropboxList);
		}
	}

	/**
	 * @param userFolderPath
	 * @throws HttpException
	 * @throws IOException
	 */
	private void updateUsersPublicFolderPrivileges(String userFolderPath) throws HttpException, IOException {
		// public folder
		AccessControlList publicFolderList = getAccessControlList(userFolderPath + FOLDER_NAME_PUBLIC);
		// should be 'read' for everyone (and preferably nothing set for 'write')

		List<AccessControlEntry> publicFolderStandardACEs = publicFolderList.getAccessControlEntriesForUsers();
		String principalEveryone = IWSlideConstants.SUBJECT_URI_ALL;
		AccessControlEntry prEveryonePositiveAce = null;
		AccessControlEntry prEveryoneNegativeAce = null;
		boolean madeChangesToPublicFolderList = false;
		// Find the ace
		for (Iterator<AccessControlEntry> iter = publicFolderStandardACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = iter.next();
			if (ace.getPrincipal().equals(principalEveryone) && !ace.isInherited()) {
				if (ace.isNegative()) {
					prEveryoneNegativeAce = ace;
				} else {
					prEveryonePositiveAce = ace;
				}
			}
		}
		if (prEveryonePositiveAce == null) {
			prEveryonePositiveAce = new AccessControlEntry(principalEveryone, false, false, false, null, AccessControlEntry.PRINCIPAL_TYPE_STANDARD);
			publicFolderList.add(prEveryonePositiveAce);
		}

		if (!prEveryonePositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_READ)) {
			if (prEveryoneNegativeAce != null && prEveryoneNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_READ)) {
				// do nothing becuse this is not ment to reset permissions but
				// to set them in the first
				// first place and update for legacy reasons.
			} else {
				prEveryonePositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_READ);
				madeChangesToPublicFolderList = true;

				// temporary at least:
				prEveryonePositiveAce.setInherited(false);
				prEveryonePositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if (madeChangesToPublicFolderList) {
			storeAccessControlList(publicFolderList);
		}
	}

	public AccessControlList getAccessControlList(String path) throws HttpException, IOException {
		WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(path);
		return getAccessControlList(path, new WebdavRootResource(resource));
	}

	/**
	 * @param path
	 * @param rResource
	 * @return
	 * @throws RemoteException
	 * @throws HttpException
	 * @throws IOException
	 */
	public AccessControlList getAccessControlList(String path, WebdavRootResource rResource) throws HttpException, IOException {
		String thePath = null;
		if (path != null) {
			thePath = getPath(path);
		}
		
		AccessControlList acl = new AccessControlList(getWebdavServerURI(), thePath);

		AclProperty aclProperty = null;
		if (thePath != null) {
			aclProperty = rResource.aclfindMethod(getURI(thePath));
		} else {
			aclProperty = rResource.aclfindMethod();
		}
		if (aclProperty != null) {
			Ace[] aclProperties = aclProperty.getAces();
			if (aclProperties != null) {
				acl.setAces(aclProperties);
			}
		}
		return acl;
	}

	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException {
		WebdavResource rResource = getWebdavExtendedResource(null, getRootUserCredentials(), Boolean.TRUE);
		return storeAccessControlList(acl, new WebdavRootResource(rResource));
	}

	/**
	 * @param acl
	 * @param rResource
	 * @return
	 * @throws RemoteException
	 * @throws HttpException
	 * @throws IOException
	 */
	public boolean storeAccessControlList(AccessControlList acl, WebdavRootResource rResource) throws HttpException, IOException {
		String resourceURI = getURI(acl.getResourcePath());
		Ace[] aces = acl.getAces();
		return rResource.aclMethod(resourceURI, aces);
	}

	/**
	 * @return
	 * @throws IBOLookupException
	 */
	public AuthenticationBusiness getAuthenticationBusiness()
			throws IBOLookupException {
		return (AuthenticationBusiness) getServiceInstance(AuthenticationBusiness.class);
	}

	/**
	 * @param loginName
	 * @return
	 */
	public String getUserHomeFolderPath(String loginName) {
		return PATH_USERS_HOME_FOLDERS + CoreConstants.SLASH + loginName;
	}

	/**
	 * @param scope
	 *            This parameter can be null and then the file name will be
	 *            unique over the whole web. If one needs unique name within a
	 *            module or a folder one can set some (unique) string as a scope
	 *            parameter
	 */
	public synchronized String createUniqueFileName(String scope) {
		IWTimestamp timestamp = new IWTimestamp();
		String minuteString = "yyyyMMdd-HHmm";
		String name = timestamp.getDateString(minuteString);
		String lastName = null;
		if (scope != null && !"".equals(scope)) {
			lastName = this.lastUniqueFileNameScopeMap.get(scope);
		} else {
			lastName = this.lastGlobalUniqueFileName;
		}

		if (!(lastName == null || !lastName.startsWith(name))) {
			if (lastName.length() == minuteString.length()) {
				name += "-1";
			} else {
				String counter = lastName.substring(minuteString.length() + 1);
				name += "-" + (Integer.parseInt(counter) + 1);
			}
		}

		if (scope != null) {
			this.lastUniqueFileNameScopeMap.put(scope, name);
		}
		this.lastGlobalUniqueFileName = name;
		return name;
	}

	public Security getSecurityHelper() {
		if (this.security == null) {
			NamespaceAccessToken token = (NamespaceAccessToken) getIWApplicationContext().getApplicationAttribute(WebdavServlet.ATTRIBUTE_NAME);
			this.security = token.getSecurityHelper();
		}
		return this.security;
	}

	/**
	 * Creates all the folders in path
	 * 
	 * @param path
	 *            Path with all the folders to create. Should hold all the
	 *            folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPath(String path, UsernamePasswordCredentials credentials) throws HttpException, RemoteException, IOException {
		boolean create = !getExistence(path);
		
		if (create) {
			if (isLocalResourceEnabled() && getSimpleSlideService().createStructure(path)) {
				return true;
			}
			
			WebdavResource rootResource = getWebdavExternalRootResource(credentials);
			StringBuffer createPath = new StringBuffer(getWebdavServerURI());
			StringTokenizer st = new StringTokenizer(path, CoreConstants.SLASH);
			while (st.hasMoreTokens()) {
				createPath.append(CoreConstants.SLASH).append(st.nextToken());
				rootResource.mkcolMethod(createPath.toString());
			}
		}
		
		return true;
	}

	/**
	 * Creates all the folders in path with credentatials of the
	 * root/administrator user.
	 * 
	 * @param path
	 *            Path with all the folders to create. Should hold all the
	 *            folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPathAsRoot(String path) throws HttpException, RemoteException, IOException {
		return createAllFoldersInPath(path, getRootUserCredentials());
	}

	/**
	 * Creates the parent folder if needed and uploads the content of the string
	 * as a utf8 encoded file of the contenttype/mimetype you specify
	 * 
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString,
			String contentType) {
		return uploadFileAndCreateFoldersFromStringAsRoot(parentPath, fileName,
				fileContentString, contentType, false);
	}

	/**
	 * Creates the parent folder if needed and uploads the content of the string
	 * as a utf8 encoded file of the contenttype/mimetype you specify
	 * 
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString, String contentType,
			boolean deletePredecessor) {
		
		InputStream stream = null;
		try {
			stream = StringHandler.getStreamFromString(fileContentString);
			return uploadFileAndCreateFoldersFromStringAsRoot(parentPath, fileName, stream, contentType, deletePredecessor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeInputStream(stream);
		}
		return false;
	}

	private String createFoldersAndPreparedUploadPath(String uploadPath, boolean checkSlashes) {
		if (checkSlashes) {
			if (!uploadPath.startsWith(CoreConstants.SLASH)) {
				uploadPath = CoreConstants.SLASH.concat(uploadPath);
			}
			if (!uploadPath.endsWith(CoreConstants.SLASH)) {
				uploadPath.concat(CoreConstants.SLASH);
			}
		}

		try {
			if (uploadPath.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
				// to avoid /content/content/
				if (createAllFoldersInPathAsRoot(uploadPath.substring(CoreConstants.WEBDAV_SERVLET_URI.length()))) {
					return uploadPath;
				}
			} else {
				if (createAllFoldersInPathAsRoot(uploadPath)) {
					return uploadPath;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean uploadFile(String uploadPath, String fileName,
			String contentType, InputStream fileInputStream) {
		return uploadFile(uploadPath, fileName, contentType, fileInputStream,
				true);
	}

	private boolean uploadFile(String uploadPath, String fileName, String contentType, InputStream fileInputStream, boolean closeStream) {
		uploadPath = createFoldersAndPreparedUploadPath(uploadPath, true);
		if (uploadPath == null) {
			return false;
		}

		IWSimpleSlideService simpleSlideService = getSimpleSlideService();
		if (simpleSlideService == null) {
			return false;
		}
		
		try {
			return simpleSlideService.upload(fileInputStream, uploadPath, fileName, contentType, null, closeStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * Creates the parent folder if needed and uploads the content of the file
	 * to Slide and sets the contenttype/mimetype you specify
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, InputStream fileInputStream,
			String contentType, boolean deletePredecessor) {
		if (uploadFile(parentPath, fileName, contentType, fileInputStream, false)) { // Trying with Slide API firstly
			return true;
		}

		// Slide API failed - using old way to do it
		try {
			parentPath = createFoldersAndPreparedUploadPath(parentPath, false);
			if (parentPath == null) {
				return false;
			}

			String filePath = parentPath + fileName;
			WebdavResource rootResource = getWebdavExternalResourceAuthenticatedAsRoot();

			String fixedURL = getURI(filePath);

			// delete previous versions
			if (deletePredecessor) {
				if (!rootResource.deleteMethod(fixedURL)) {
					rootResource.deleteMethod(filePath);
				}
			}

			// Conflict fix: uri for creating but path for updating
			// Note! This is a patch to what seems to be a bug in WebDav
			// Apparently in version below works in some cases and the other in
			// other cases.
			// Seems to be connected to creating files in folders created in
			// same tomcat session or similar
			// not quite clear...

			// update or create
			if (!rootResource.putMethod(fixedURL, fileInputStream)) {
				rootResource.putMethod(filePath, fileInputStream);
			}
			if (contentType != null) {
				// use the object PropertyName, do not use
				// proppatchMethod(String, String, String, boolean)
				// where only the property name is set but not the namespace
				// "DAV:"
				// The namespace is needed later in the method
				// StandardRDBMSAdapter#createRevisionDescriptor(Connection
				// connection, Uri uri, NodeRevisionDescriptor
				// revisionDescriptor)
				// first parameter is the namespace, second parameter is the
				// name of the property (e.g. "getcontenttype")
				PropertyName propertyName = new PropertyName("DAV:", WebdavResource.GETCONTENTTYPE);
				if (!rootResource.proppatchMethod(fixedURL, propertyName, contentType, true)) {
					rootResource.proppatchMethod(filePath, propertyName, contentType, true);
				}
			}
			rootResource.close();
			// log(rootResource.getStatusMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Uploads the supplied string as a file with the content type "text/xml"
	 * 
	 * @param parentPath
	 * @param fileName
	 * @param fileContentString
	 * @param contentType
	 * @return
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString) {
		return uploadFileAndCreateFoldersFromStringAsRoot(parentPath, fileName,
				fileContentString, "text/xml", false);
	}

	/**
	 * Uploads the supplied string as a file with the content type "text/xml"
	 * 
	 * @param parentPath
	 * @param fileName
	 * @param fileContentString
	 * @param contentType
	 * @param deletePredecessor
	 * @return
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString,
			boolean deletePredecessor) {
		return uploadFileAndCreateFoldersFromStringAsRoot(parentPath, fileName,
				fileContentString, "text/xml", deletePredecessor);
	}

	/**
	 * @return Returns the array of IWSlideChangeListeners.
	 */
	public IWSlideChangeListener[] getIWSlideChangeListeners() {
		return this.iwSlideChangeListenersArray;
	}

	/**
	 * @param iwSlideChangeListeners
	 *            The iwSlideChangeListeners to set. Overwrites the current list
	 */
	public void setIWSlideChangeListeners(List<IWSlideChangeListener> iwSlideChangeListeners) {
		this.iwSlideChangeListeners = iwSlideChangeListeners;
		this.iwSlideChangeListenersArray = iwSlideChangeListeners.toArray(new IWSlideChangeListener[0]);
	}

	/**
	 * Add a listener that get's notified whenever content changes in Slide,
	 * filter the event yourself by event.getURI() for example
	 * 
	 * @param iwSlideChangeListener
	 */
	public void addIWSlideChangeListeners(IWSlideChangeListener iwSlideChangeListener) {
		if (this.iwSlideChangeListeners == null) {
			this.iwSlideChangeListeners = new ArrayList<IWSlideChangeListener>();
		}

		if (!this.iwSlideChangeListeners.contains(iwSlideChangeListener)) {
			this.iwSlideChangeListeners.add(iwSlideChangeListener);
			// update the array, for speed optimization
			this.iwSlideChangeListenersArray = this.iwSlideChangeListeners.toArray(new IWSlideChangeListener[0]);
		}
	}

	/**
	 * 
	 * @param folderURI
	 * @return the count of "real" child resources, excluding folders and hidden
	 *         files
	 */
	public int getChildCountExcludingFoldersAndHiddenFiles(String folderURI) {
		List<String> children = getChildPathsExcludingFoldersAndHiddenFiles(folderURI);

		if (children != null) {
			return children.size();
		}
		return 0;
	}

	/**
	 * 
	 * @param folderURI
	 * @return the count of folder resources under the sepcified path, excluding
	 *         files and hidden files
	 */
	public int getChildFolderCount(String folderURI) {
		List<String> children = getChildFolderPaths(folderURI);

		if (children != null) {
			return children.size();
		}
		return 0;
	}

	/**
	 * 
	 * @param folderURI
	 * @return the count of ALL child resources, including folders and hidden
	 *         files
	 */
	public int getChildCount(String folderURI) {
		List<String> children = getChildPaths(folderURI);

		if (children != null) {
			return children.size();
		}
		return 0;
	}

	public boolean isHiddenFile(String fileName) {
		if (fileName != null) {
			return fileName.startsWith(CoreConstants.DOT) || fileName.startsWith("Thumbs.db");
		}
		return false;
	}

	/**
	 * 
	 * @param folderURI
	 * @return the count of "real" child resources, excluding folders and hidden
	 *         files
	 */
	public List<String> getChildPathsExcludingFoldersAndHiddenFiles(String folderURI) {

		Map<String, List<String>> cache = getChildPathsCacheMap();
		List<String> paths = cache.get(folderURI);

		if (paths == null) {
			try {
				// TODO optimize by using a dasl search!
				WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(folderURI);

				if (resource.isCollection()) {
					WebdavResources children = resource.getChildResources();
					WebdavResource[] resources = children.listResources();

					if (resources.length > 0) {
						paths = new ArrayList<String>();
						for (int i = 0; i < resources.length; i++) {
							WebdavResource wResource = resources[i];
							String path = wResource.getPath();
							String fileName = path.substring(path.lastIndexOf(CoreConstants.SLASH) + 1);
							if (!resources[i].isCollection() && !isHiddenFile(fileName)) {
								paths.add(wResource.getPath());
							}
						}
						cache.put(folderURI, paths);
					}
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return paths;
	}

	/**
	 * 
	 * @param folderURI
	 * @return the paths of folder resources under the specified path, excluding
	 *         files and hidden files
	 */
	public List<String> getChildFolderPaths(String folderURI) {

		Map<String, List<String>>cache = getChildFolderPathsCacheMap();
		List<String> paths = cache.get(folderURI);

		if (paths == null) {
			try {
				// TODO optimize by using a dasl search!
				WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(folderURI);

				if (resource.isCollection()) {
					WebdavResources children = resource.getChildResources();
					WebdavResource[] resources = children.listResources();

					if (resources.length > 0) {
						paths = new ArrayList<String>();
						for (int i = 0; i < resources.length; i++) {
							WebdavResource wResource = resources[i];
							if (resources[i].isCollection()) {
								paths.add(wResource.getPath());
							}
						}
						cache.put(folderURI, paths);
					}
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return paths;
	}

	/**
	 * 
	 * @param folderURI
	 * @return the path of ALL child resources, including folders and hidden
	 *         files. Null if no children
	 */
	public List<String> getChildPaths(String folderURI) {
		Map<String, List<String>> cache = getChildPathsCacheMap();
		List<String> paths = cache.get(folderURI);

		if (paths == null) {
			try {
				// TODO optimize by using a dasl search!
				WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(folderURI);

				if (resource.isCollection()) {
					WebdavResources children = resource.getChildResources();
					WebdavResource[] resources = children.listResources();

					if (resources.length > 0) {
						paths = new ArrayList<String>();
						for (int i = 0; i < resources.length; i++) {
							WebdavResource wResource = resources[i];
							paths.add(wResource.getPath());
						}
						cache.put(folderURI, paths);
					}
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return paths;
	}

	/**
	 * Takes the URI and splits it by each "/" and invalidates child counts and
	 * childpath caches for each folder
	 * 
	 * @param URI
	 */
	public void invalidateCacheForAllFoldersInURIPath(String URI) {
		// rip the URI apart and then rebuild it from ground up, invalidating
		// each folders cache
		// must end with a "/"
		if (!URI.endsWith(CoreConstants.SLASH)) {
			URI += CoreConstants.SLASH;
		}
		StringBuffer createPath = new StringBuffer();
		StringTokenizer st = new StringTokenizer(URI, CoreConstants.SLASH);
		while (st.hasMoreTokens()) {

			if (!createPath.toString().startsWith(CoreConstants.SLASH)) {
				createPath.append(CoreConstants.SLASH);
			}
			createPath.append(st.nextToken()).append(CoreConstants.SLASH);
			// clear from maps
			String path = createPath.toString();
			getChildFolderPathsCacheMap().remove(path);
			getChildPathsCacheMap().remove(path);
			getChildPathsExcludingFolderAndHiddenFilesCacheMap().remove(path);
		}

	}

	/**
	 * @return Returns the childFolderPathsCacheMap.
	 */
	public Map<String, List<String>> getChildFolderPathsCacheMap() {
		return this.childFolderPathsCacheMap;
	}

	/**
	 * @param childFolderPathsCacheMap
	 *            The childFolderPathsCacheMap to set.
	 */
	public void setChildFolderPathsCacheMap(Map<String, List<String>> childFolderPathsCacheMap) {
		this.childFolderPathsCacheMap = childFolderPathsCacheMap;
	}

	/**
	 * @param <K>
	 * @param <V>
	 * @return Returns the childPathsCacheMap.
	 */
	public Map<String, List<String>> getChildPathsCacheMap() {
		return this.childPathsCacheMap;
	}

	/**
	 * @param childPathsCacheMap
	 *            The childPathsCacheMap to set.
	 */
	public void setChildPathsCacheMap(Map<String, List<String>> childPathsCacheMap) {
		this.childPathsCacheMap = childPathsCacheMap;
	}

	/**
	 * @return Returns the childPathsExcludingFolderAndHiddenFilesCacheMap.
	 */
	public Map<String, List<String>> getChildPathsExcludingFolderAndHiddenFilesCacheMap() {
		return this.childPathsExcludingFolderAndHiddenFilesCacheMap;
	}

	/**
	 * @param childPathsExcludingFolderAndHiddenFilesCacheMap
	 *            The childPathsExcludingFolderAndHiddenFilesCacheMap to set.
	 */
	public void setChildPathsExcludingFolderAndHiddenFilesCacheMap(Map<String, List<String>> childPathsExcludingFolderAndHiddenFilesCacheMap) {
		this.childPathsExcludingFolderAndHiddenFilesCacheMap = childPathsExcludingFolderAndHiddenFilesCacheMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.slide.business.IWSlideChangeListener#onSlideChange(org.apache.slide.event.ContentEvent)
	 */
	public void onSlideChange(IWContentEvent contentEvent) {
		// get the url changing and invalidate
		String URI = contentEvent.getContentEvent().getUri();
		invalidateCacheForAllFoldersInURIPath(URI);
	}

	/**
	 * Gets the parent path of the resource
	 * 
	 * @param resource
	 * @return
	 */
	public String getParentPath(WebdavResource resource) {
		String path = resource.getPath();
		return getParentPath(path);
	}

	public String getParentPath(String path) {
		String parentPath = null;
		if (path != null) {
			int index = path.lastIndexOf(CoreConstants.SLASH);
			if (index == 0) {
				parentPath = CoreConstants.EMPTY;
			} else {
				parentPath = path.substring(0, index);
			}
		} else {
			return null;
		}
		return parentPath;
	}

	/**
	 * Uploads zip file's contents to slide. Note: only *.zip file allowed!
	 * 
	 * @param zipInputStream:
	 *            a stream to read the file and its content from
	 * @param uploadPath:
	 *            a path in slide where to store files (for example:
	 *            "/files/public/")
	 * @return result: success (true) or failure (false) while uploading file
	 */
	public boolean uploadZipFileContents(ZipInputStream zipInputStream,
			String uploadPath) {
		boolean result = (uploadPath == null || CoreConstants.EMPTY
				.equals(uploadPath)) ? false : true; // Checking if
														// parameters are valid
		if (!result) {
			LOGGER.warning("Invalid upload path!");
			return result;
		}
		result = zipInputStream == null ? false : true;
		if (!result) {
			LOGGER.warning("ZipInputStream is closed!");
			return result;
		}

		ZipEntry entry = null;
		ZipInstaller zip = new ZipInstaller();
		ByteArrayOutputStream memory = null;
		InputStream is = null;
		String pathToFile = null;
		String fileName = null;
		try {
			while ((entry = zipInputStream.getNextEntry()) != null && result) {
				if (!entry.isDirectory()) {
					pathToFile = CoreConstants.EMPTY;
					fileName = StringHandler.removeCharacters(entry.getName(),
							CoreConstants.SPACE, CoreConstants.UNDER);
					fileName = StringHandler.removeCharacters(fileName,
							CoreConstants.BRACKET_LEFT, CoreConstants.EMPTY);
					fileName = StringHandler.removeCharacters(fileName,
							CoreConstants.BRACKET_RIGHT, CoreConstants.EMPTY);
					int lastSlash = fileName.lastIndexOf(CoreConstants.SLASH);
					if (lastSlash != -1) {
						pathToFile = fileName.substring(0, lastSlash + 1);
						fileName = fileName.substring(lastSlash + 1, fileName
								.length());
					}
					if (!fileName.startsWith(CoreConstants.DOT)) { // If not a system file
						memory = new ByteArrayOutputStream();
						zip.writeFromStreamToStream(zipInputStream, memory);
						is = new ByteArrayInputStream(memory.toByteArray());
						result = uploadFile(uploadPath + pathToFile, fileName,
								null, is);
						memory.close();
						is.close();
					}
				}
				zip.closeEntry(zipInputStream);
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Error uploading zip file to: " + uploadPath, e);
			return false;
		} finally {
			zip.closeEntry(zipInputStream);
		}
		return result;
	}

	/**
	 * Gets an inputstream for reading the file on the given path as ROOT
	 * 
	 * @throws IOException
	 * @throws
	 */
	public InputStream getInputStream(String path) throws IOException {
		IWSimpleSlideService simpleSlideService = getSimpleSlideService();
		InputStream stream = null;
		if (simpleSlideService != null) {
			stream = simpleSlideService.getInputStream(path);
		}

		if (stream == null) {
			WebdavResource resource = getWebdavExternalResourceAuthenticatedAsRoot(path);
			stream = resource.getMethodData();
		}

		return stream;
	}
	
	private IWSimpleSlideService getSimpleSlideService() {
		if (simpleSlideService == null) {
			ELUtil.getInstance().autowire(this);
		}
		return simpleSlideService;
	}

	public OutputStream getOutputStream(File file) throws IOException {
		return getOutputStream(file.getAbsolutePath());
	}

	/**
	 * Gets an outputstream for writing to the file on the given path
	 * 
	 * @throws IOException
	 * @throws
	 */
	public OutputStream getOutputStream(String path) throws IOException {
		WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(path);
		return new WebdavOutputStream(resource);
	}

	/**
	 * Gets a file representation for the given path as root
	 * 
	 * @throws RemoteException
	 */
	public File getFile(String path) throws URIException, RemoteException {
		WebdavFile file = null;
		try {
			file = new WebdavFile(getWebdavServerURL(getRootUserCredentials(),
					path));
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return file;
	}

	public boolean deleteAsRootUser(String path) throws RemoteException {
		if (getSimpleSlideService().delete(path)) {
			return true;
		}
		
		try {
			WebdavResource resource = getWebdavResourceAuthenticatedAsRoot(path);
			resource.deleteMethod();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error deleting: " + path, e);
			return false;
		}
		
		return true;
	}

	public boolean delete(String path, UsernamePasswordCredentials credentials) throws RemoteException {
		try {
			WebdavResource resource = getWebdavResource(path, credentials);
			resource.deleteMethod();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error deleting: " + path, e);
			return false;
		}
		
		return true;
	}

}