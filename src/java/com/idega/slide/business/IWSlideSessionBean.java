/*
 * $Id: IWSlideSessionBean.java,v 1.15 2004/12/31 03:33:07 gimmi Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.http.HttpSessionBindingEvent;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.common.SlideToken;
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOSessionBean;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2004/12/31 03:33:07 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.15 $
 */
public class IWSlideSessionBean extends IBOSessionBean implements IWSlideSession { //, HttpSessionBindingListener {

	
//	public static final String PATH_DEFAULT_SCOPE_ROOT = "/files";
//	public static final String PATH_ROOT = "/";
//	public static final String PATH_CURRENT = null;
	
	
	private WebdavRootResource webdavRootResource = null;
	private boolean isLoggedOn = false;
	private UsernamePasswordCredentials usersCredentials = null;
	private HttpURL rootURL = null;
	
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
		if(service == null){
			try {
				service = (IWSlideService)this.getServiceInstance(IWSlideService.class);
			}
			catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return service;
	}
	
	public String getUserFullName() {
		try {
			if (getCurrentUser() != null) {
				return getCurrentUser().getName();
			}
		} catch (NotLoggedOnException e) {
			return "not logged on (TMP, iwslideSessionBean";
		}
		return null;
	}
	
	public String getUserFolderName() {
		if (getUserCredentials() != null) {
			return getUserCredentials().getUserName();
		}
		return null;
	}
	
	public String getWebdavServerURI(){
		if(servletPath == null){
			try {
				servletPath = getIWSlideService().getWebdavServerURI();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return servletPath;
	}
	
	public UsernamePasswordCredentials getUserCredentials(){
		LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(getUserContext());
		if(lInfo!=null){
			String password = (String)lInfo.getAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME);
			if(password == null){
				password = StringHandler.getRandomString(10);
				lInfo.setAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME,password);
			}
			return new UsernamePasswordCredentials(lInfo.getLogin(),password);
		}
		return null;
	}
	
	
	/**
	 * This returns a wrapper for the root webdavresoure.  Only one instance of this object is created for each session.
	 */
	public WebdavRootResource getWebdavRootResource() throws HttpException, IOException{
		boolean tmpIsLoggedOn = getUserContext().isLoggedOn();
		//if("resource is null" && ("has logged on/off" || ("is logged on" && "has some usersCredentials" && "the credential does not match his current login, that is he has logged in as some other user")))
		if(webdavRootResource != null && (isLoggedOn != tmpIsLoggedOn || (tmpIsLoggedOn && usersCredentials != null && !(usersCredentials).getUserName().equals(getUserContext().getRemoteUser())))){
			webdavRootResource.close();
			webdavRootResource = null;
			usersCredentials = null;
			isLoggedOn = !isLoggedOn;
		}
		
		if(webdavRootResource == null){
			if(usersCredentials == null){
				if(isLoggedOn){
					usersCredentials = getUserCredentials();
				}
			}
			WebdavResource resource;
			if(usersCredentials!=null){
				resource = new WebdavResource(getIWSlideService().getWebdavServerURL(usersCredentials));
			} else {
				resource = new WebdavResource(getIWSlideService().getWebdavServerURL());
			}
			resource.setFollowRedirects(true);
			
			webdavRootResource = new WebdavRootResource(resource);
		}
		
		return webdavRootResource;
	}


	public WebdavExtendedResource getWebdavResource(String path) throws HttpException, IOException, RemoteException {
		WebdavExtendedResource resource;
		if(usersCredentials!=null){
			resource = new WebdavExtendedResource(getIWSlideService().getWebdavServerURL(getUserCredentials(),path));
		} else {
			resource = new WebdavExtendedResource(getIWSlideService().getWebdavServerURL(path));
		}
		
		return resource;
	}
	
	public String getApplicationServerRelativePath(String path) throws RemoteException{
		return getWebdavServerURI()+((path.startsWith("/"))?"":"/")+path;
	}
	
	public boolean getExistence(String path) throws HttpException, IOException{
		if(path==null){
			return false;
		}
		return getWebdavRootResource().headMethod(((path.startsWith(getWebdavServerURI()))?path:getApplicationServerRelativePath(path)));
	}

	
	public void close(){
		if(webdavRootResource != null){
			try {
				webdavRootResource.close();
				webdavRootResource = null;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public String getUserHomeFolder() {
		if (getUserFolderName() != null) {
			return getIWSlideService().getUserHomeFolderPath(getUserFolderName());
		}
		return null;
	}
    /**
     * Returns a SlideToken using the authentication information of the IW login system
     *
     * @return a new SlideToken instance
     **/
    public SlideToken getSlideToken() {
    		if(_slideToken == null){
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
    		return _slideToken;

    }

	/* (non-Javadoc)
	 * @see com.idega.slide.business.IWSlideSession#setSlideToken(org.apache.slide.common.SlideToken)
	 */
	public void setSlideToken(SlideToken slideToken) {
		_slideToken = slideToken;
	}
	
//	public HistoryPathHandler getHistoryPathHandler(){
//		return HistoryPathHandler.getHistoryPathHandler();
//	}
//	
//	public WorkspacePathHandler getWorkspacePathHandler(){
//		return WorkspacePathHandler.getWorkspacePathHandler();
//	}
	
}
