/*
 * $Id: IWSlideSessionBean.java,v 1.10 2004/12/14 17:24:10 gummi Exp $
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
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOSessionBean;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.slide.util.WebdavRootResource;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2004/12/14 17:24:10 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.10 $
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
				service = (IWSlideService)IBOLookup.getServiceInstance(getIWApplicationContext(),IWSlideService.class);
			}
			catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return service;
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


	public WebdavResource getWebdavResource(String path) throws HttpException, IOException, RemoteException {
		WebdavResource resource;
		if(usersCredentials!=null){
			resource = new WebdavResource(getIWSlideService().getWebdavServerURL(getUserCredentials(),path));
		} else {
			resource = new WebdavResource(getIWSlideService().getWebdavServerURL(path));
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
	
	
}
