/*
 * $Id: IWSlideSessionBean.java,v 1.5 2004/12/13 13:12:32 gummi Exp $
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
import java.util.StringTokenizer;
import javax.servlet.http.HttpSessionBindingEvent;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOSessionBean;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2004/12/13 13:12:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
 */
public class IWSlideSessionBean extends IBOSessionBean implements IWSlideSession { //, HttpSessionBindingListener {

	
//	public static final String PATH_DEFAULT_SCOPE_ROOT = "/files";
//	public static final String PATH_ROOT = "/";
//	public static final String PATH_CURRENT = null;
	
	
	private WebdavResource webdavResource = null;
	private boolean isLoggedOn = false;
	private Credentials usersCredentials = null;
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
	
	public String getWebdavServletURL(){
		if(servletPath == null){
			try {
				servletPath = getIWSlideService().getWebdavServletURL();
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
	
	public WebdavResource getWebdavResource() throws HttpException, IOException{
		boolean tmpIsLoggedOn = getUserContext().isLoggedOn();
		if(webdavResource != null && isLoggedOn != tmpIsLoggedOn ){ //TMP || (tmpIsLoggedOn && usersCredentials != null && !((UsernamePasswordCredentials)usersCredentials).getUserName().equals(getUserContext().getCurrentUser().getUniqueId()))){
			webdavResource.close();
			webdavResource = null;
			usersCredentials = null;
			isLoggedOn = !isLoggedOn;
		}
		
		if(webdavResource == null){
			if(usersCredentials == null){
				if(isLoggedOn){
					//TMP User usr = getUserContext().getCurrentUser();
					//TMP usersCredentials = new UsernamePasswordCredentials(usr.getUniqueId(),StringHandler.getRandomString(10));
					usersCredentials = getUserCredentials();
				}
			}
			webdavResource = new WebdavResource(getWebdavServletURL(),usersCredentials);
		}
		
		return webdavResource;
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.business.IWSlideSession#getWebdavResource(java.lang.String)
	 */
	//TEST
	public WebdavResource getWebdavResource(String path) throws HttpException, IOException, RemoteException {
		StringTokenizer tokens = new StringTokenizer(path,"/");
		WebdavResource currentResource = getWebdavResource();
		while ( tokens.hasMoreTokens()) {
			String element = tokens.nextToken();
			if(element == null || "".equals(element)){
				continue;
			}
			WebdavResources childResources = currentResource.getChildResources();
			currentResource = childResources.getResource(element);
			if(currentResource == null){
				throw new IOException("For path '"+path+"', '"+element+"' was not found");
			}
		}
		
		return currentResource;
	}

	
	public void close(){
		if(webdavResource != null){
			try {
				webdavResource.close();
				webdavResource = null;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public HttpURL getWebdavServerURL(){
	    
	    try {
	       String server = getUserContext().getApplicationContext().getDomain().getServerName();
	       if(server.endsWith("/"))
	           server = server.substring(0,server.lastIndexOf("/"));
	       server += getWebdavServletURL();
            HttpURL hrl = new HttpURL(server);
            hrl.setUserinfo("root","root");
            //hrl.setUserInfo("user","pass");
            return hrl;
        } catch (URIException e) {
           throw new IBORuntimeException(e);
        }
	}
	/*
	public WebdavResource getWebdavResource(){
	    try {
            return new WebdavResource(getWebdavServerURL());
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}*/
	
	public WebdavFile getWebdavFile(){
	    try {
            return new WebdavFile(getWebdavServerURL());
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
}
