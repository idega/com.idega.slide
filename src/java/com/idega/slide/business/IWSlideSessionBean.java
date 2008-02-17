/*
 * $Id: IWSlideSessionBean.java,v 1.37 2008/02/17 13:23:47 eiki Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.security.Security;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.LinkNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.util.WebdavStatus;

import com.idega.business.IBOLookupException;
import com.idega.business.IBOSessionBean;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavOutputStream;
import com.idega.slide.util.WebdavRootResource;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2008/02/17 13:23:47 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.37 $
 */
public class IWSlideSessionBean extends IBOSessionBean implements IWSlideSession { //, HttpSessionBindingListener {

	
//	public static final String PATH_DEFAULT_SCOPE_ROOT = "/files";
//	public static final String PATH_ROOT = "/";
//	public static final String PATH_CURRENT = null;
	
	
	private WebdavRootResource webdavRootResource = null;
	private boolean isLoggedOn = false;
	private UsernamePasswordCredentials usersCredentials = null;
	private IWSlideService service = null;
	
	private String servletPath = null;
	
	private static final String SLIDE_PASSWORD_ATTRIBUTE_NAME = "iw_slide_password";
	
	private SlideToken _slideToken = null;
	
//    /** The WebDAV resource. */
//    private WebdavResource webdavResource = null;
//    private HttpURL rootURL = null;
//    private WebdavFile homedir = null;
//    private String username = null;
//    private String password = null;
//    private String uri = null;
//    private String rootPath = null;
    
    
	
	/**
	 * 
	 */
	public IWSlideSessionBean() {
		super();
	}	

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		close();
	}
	
	public IWSlideService getIWSlideService(){
		if(this.service == null){
			try {
				this.service = (IWSlideService)this.getServiceInstance(IWSlideService.class);
			}
			catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return this.service;
	}
	
	/**
	 * @return returns full name of the current user.  Returns <code>null</code> if user is not logged on.
	 */
	public String getUserFullName() {
		try {
			if (getCurrentUser() != null) {
				return getCurrentUser().getName();
			}
		} catch (NotLoggedOnException e) {
			return null;
		}
		return null;
	}
		
	public String getWebdavServerURI(){
		if(this.servletPath == null){
			try {
				this.servletPath = getIWSlideService().getWebdavServerURI();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return this.servletPath;
	}
	
	public UsernamePasswordCredentials getUserCredentials() throws RemoteException{
		LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(getUserContext());
		if(lInfo!=null){
			String password = (String)lInfo.getAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME);
			if(password == null){
				password = StringHandler.getRandomString(10);
				lInfo.setAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME,password);
			}
			if(getUserContext().isSuperAdmin()){
				return getIWSlideService().getRootUserCredentials();
			} else {
				return new UsernamePasswordCredentials(lInfo.getLogin(),password);
			}
			
		}
		return null;
	}
	
	
	/**
	 * This returns a wrapper for the root webdavresoure.  Only one instance of this object is created for each session.
	 */
	public WebdavRootResource getWebdavRootResource() throws HttpException, IOException{
		boolean tmpIsLoggedOn = getUserContext().isLoggedOn();
		//if("resource is null" && ("has logged on/off" || ("is logged on" && "has some usersCredentials" && "the credential does not match his current login, that is he has logged in as some other user")))
		if(this.webdavRootResource != null && (this.isLoggedOn != tmpIsLoggedOn || (tmpIsLoggedOn && this.usersCredentials != null && !(this.usersCredentials).getUserName().equals(getUserContext().getRemoteUser())))){
			String userName = (this.usersCredentials).getUserName();
			//extra check because "Administrator" is "root"
			if(!(userName.equals("root") && getUserContext().isSuperAdmin())){
				this.webdavRootResource.close();
				this.webdavRootResource = null;
				this.usersCredentials = null;
				this.isLoggedOn = !this.isLoggedOn;
			}
		}
		
		if(this.webdavRootResource == null || this.webdavRootResource.isClosed()){
			this.webdavRootResource=null;
			if(this.usersCredentials == null){
				if(tmpIsLoggedOn){
					this.usersCredentials = getUserCredentials();
					this.isLoggedOn=true;
				}
			}
			WebdavResource resource;
			if(this.usersCredentials!=null){
				resource = new WebdavResource(getIWSlideService().getWebdavServerURL(this.usersCredentials));
			} else {
				resource = new WebdavResource(getIWSlideService().getWebdavServerURL());
			}
			resource.setFollowRedirects(true);
			
			this.webdavRootResource = new WebdavRootResource(resource);
		}
		
		return this.webdavRootResource;
	}


	public WebdavExtendedResource getWebdavResource(String path) throws HttpException, IOException, RemoteException {
		WebdavExtendedResource resource;
//		if(getUserContext().isLoggedOn()){
			//resource = new WebdavExtendedResource(getIWSlideService().getWebdavServerURL(getUserCredentials(),getPath(path)));
			IWSlideService service = getIWSlideService();
			resource = service.getWebdavExtendedResource(path,getUserCredentials());
//		} else {
//			resource = new WebdavExtendedResource(getIWSlideService().getWebdavServerURL(path));
//		}
		
		return resource;
	}
	
	/**
	 * <p>
	 * Gets an URL to the WebdavServer with set credentials
	 * </p>
	 * @param path
	 * @return
	 */
	public HttpURL getURL(String path){
		IWSlideService service = getIWSlideService();
		try {
			return service.getWebdavServerURL(getUserCredentials(), path);
		}
		catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets a file representation for the given path
	 */
	public File getFile(String path)throws URIException{
		WebdavFile file = null;
		file = new WebdavFile(getURL(path));
		return file;
	}
	
	/**
	 * Gets an inputstream for reading the file on the given path
	 * @throws IOException 
	 * @throws  
	 */
	public InputStream getInputStream(String path)throws IOException{
		WebdavResource resource = getWebdavResource(path);
		return resource.getMethodData();
	}

	public OutputStream getOutputStream(File file)throws IOException{
		return getOutputStream(file.getAbsolutePath());
	}
	
	/**
	 * Gets an outputstream for writing to the file on the given path
	 * @throws IOException
	 * @throws  
	 */
	public OutputStream getOutputStream(String path)throws IOException{
		WebdavResource resource = getWebdavResource(path);
		return new WebdavOutputStream(resource);
	}

	
	public String getURI(String path) throws RemoteException{
		return getIWSlideService().getURI(path);
	}
	
	public String getPath(String uri) throws RemoteException{
		String uriPrefix = getWebdavServerURI();
		return ((uri.startsWith(uriPrefix))?uri.substring(uriPrefix.length()):uri);
	}
	
	/**
	 * Differs from the method in the service bean a little. The service bean uses the root credentials but this uses the current users credentials.
	 */
	public boolean getExistence(String path) throws HttpException, IOException{
		if(path==null){
			return false;
		}
		try {
			String pathToCheck = ((path.startsWith(getWebdavServerURI()))?path:getURI(path));
			Enumeration prop = getWebdavRootResource().propfindMethod(pathToCheck, WebdavResource.DISPLAYNAME);
			return !(prop == null || !prop.hasMoreElements());
		}
		catch (HttpException e) {
			if(e.getReasonCode()==WebdavStatus.SC_NOT_FOUND){
				return false;
			} else {
				throw e;
			}
		}
//		return getWebdavRootResource().headMethod(((path.startsWith(getWebdavServerURI()))?path:getURI(path)));
	}

	
	public void close(){
		if(this.webdavRootResource != null){
			try {
				this.webdavRootResource.close();
				this.webdavRootResource = null;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	public AccessControlList getAccessControlList(String path) throws HttpException, IOException{
		WebdavRootResource rResource = getWebdavRootResource();
		return getIWSlideService().getAccessControlList(path, rResource);
	}
	

	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException{
		WebdavRootResource rResource = getWebdavRootResource();
		return getIWSlideService().storeAccessControlList(acl, rResource);
	}

	public String getUserHomeFolder() throws RemoteException {
		String loginName = getUserContext().getRemoteUser();
		if (loginName != null) {
			return getIWSlideService().getUserHomeFolderPath(loginName);
		}
		return null;
	}
	
    /**
     * Returns a SlideToken using the authentication information of the IW login system
     *
     * @return a new SlideToken instance
     **/
    private SlideToken getSlideToken() {
    		if(this._slideToken == null){
    			throw new RuntimeException("["+this.getClass().getName()+"]: Requesting SlideToken but token has not been set.  Check if IWSlideAuthenticator filter is mapped right (/*) in web.xml");
//    			// This code is borrowed from org.apache.slide.webdav.util.WebdavUtils#getSlideToken(HttpServletRequest)
//    			// and altered since we just have session and not requst object.
//    			
//            CredentialsToken credentialsToken;
//            Principal principal = getUserContext().getUserPrincipal();
//            
//            // store the current principal in the session, to get around a bug in
//            // IE 5 where the authentication info is not submitted by IE when
//            // doing a HEAD request.
//            if (principal == null) {
//                credentialsToken = new CredentialsToken("");
//            } else {
//                // because the principal is not guaranteed to be serializable
//                // and could thus create problems in a distributed deployment
//                // we store the principal name instead of the principal itself
////                session.setAttribute(CREDENTIALS_ATTRIBUTE, principal.getName());
//                credentialsToken = new CredentialsToken(principal);
//            }
//            
//            SlideToken token = new SlideTokenImpl(credentialsToken);
//            token.setEnforceLockTokens(true);
//
//            // store session attributes in token parameters to pass them through
//            // to the stores
//            for(Enumeration e = getUserContext().getSeAttributeNames(); e.hasMoreElements();) {
//                String name = (String)e.nextElement();
//                token.addParameter(name, getUserContext().getSessionAttribute(name));
//            }
//            return _slideToken;
    		}
    		return this._slideToken;

    }

	/* (non-Javadoc)
	 * @see com.idega.slide.business.IWSlideSession#setSlideToken(org.apache.slide.common.SlideToken)
	 */
	public void setSlideToken(Object slideToken) {
		setSlideToken((SlideToken)slideToken);
	}
    
	/* (non-Javadoc)
	 * @see com.idega.slide.business.IWSlideSession#setSlideToken(org.apache.slide.common.SlideToken)
	 */
	private void setSlideToken(SlideToken slideToken) {
		this._slideToken = slideToken;
	}
	
	
	private Security getSecurity() throws RemoteException{
		return getIWSlideService().getSecurityHelper();
	}
	
	
	public boolean hasPermission(String resourcePath, Privilege privilege) throws RemoteException {
		try {
			boolean hasPermission = getSecurity().hasPermission(getSlideToken(),getObjectNode(resourcePath),getActionNode(privilege));
			return hasPermission;
		}
		catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		catch (ServiceAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param resourcePath
	 * @return
	 */
	private LinkNode getObjectNode(String resourcePath) throws RemoteException {
		return new LinkNode(getPath(resourcePath));
	}

	/**
	 * @param action
	 * @return
	 */
	private ActionNode getActionNode(Privilege privilege) {
		
		String path = IWSlideConstants.PATH_ACTIONS+"/"+privilege.getName();
		
		return ActionNode.getActionNode(path);
	}
	
	/**
	 * Creates all the folders in path 
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPath(String path) throws HttpException, RemoteException, IOException {
		/*boolean hadToCreate = false;
		WebdavRootResource rootResource = getWebdavRootResource();
		
		hadToCreate = !getExistence(path);
		if(hadToCreate){
			StringBuffer createPath = new StringBuffer(getWebdavServerURI());
			StringTokenizer st = new StringTokenizer(path,"/");
			while(st.hasMoreTokens()) {
				createPath.append("/").append(st.nextToken());
				rootResource.mkcolMethod(createPath.toString());
			}
		}
		return hadToCreate;*/
		IWSlideService slideService = getIWSlideService();
		return slideService.createAllFoldersInPath(path,getUserCredentials());
		
	}
	
	/**
	 * 
	 * @param folderURI the path to the folder
	 * @return true if the path refers to a folder, checks as the current user, false if he doesn't have the priviledges to the folder
	 */
	public boolean isFolder(String folderURI) {
		
		WebdavResource resource;
		try {
			resource = getWebdavResource(folderURI);
			return resource.isCollection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;	
	}
}
