/*
 * $Id: IWSlideSessionBean.java,v 1.2 2004/11/05 17:30:36 gummi Exp $
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOSessionBean;


/**
 * 
 *  Last modified: $Date: 2004/11/05 17:30:36 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public class IWSlideSessionBean extends IBOSessionBean implements IWSlideSession, HttpSessionBindingListener {

	
//	public static final String PATH_DEFAULT_SCOPE_ROOT = "/files";
//	public static final String PATH_ROOT = "/";
//	public static final String PATH_CURRENT = null;
	
	
	private boolean isLoggedOn = false;
	private Credentials usersCredentials = null;
	private HttpURL rootURL = null;
	
	private IWSlideService service = null;
	
	private String servletPath = null;
	
	private List resourcePool = null;
	
	
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
	
	public WebdavResourceSession getWebdavResource() throws HttpException, IOException{
		boolean tmpIsLoggedOn = getUserContext().isLoggedOn();
		
		if(!getWebdavResourcePool().isEmpty() && isLoggedOn != tmpIsLoggedOn ){ //TMP || (tmpIsLoggedOn && usersCredentials != null && !((UsernamePasswordCredentials)usersCredentials).getUserName().equals(getUserContext().getCurrentUser().getUniqueId()))){
			close();
			usersCredentials = null;
			isLoggedOn = !isLoggedOn;
		}
		
		WebdavResourceSession session = null;
		List pool = getWebdavResourcePool();
		for (Iterator iter = pool.iterator(); iter.hasNext();) {
			WebdavResourceSession s = (WebdavResourceSession) iter.next();
			if(!s.isInUse()){
//				session=s;
//				continue;
				return s;
			}
		}
		
		if(session == null){
			session = new WebdavResourceSession(getNewConnection());
			addToPool(session);
		}
		
		return session;
	}
	
	private void addToPool(WebdavResourceSession session){
		getWebdavResourcePool().add(session);
	}
	
	private List getWebdavResourcePool(){
		if(resourcePool == null){
			resourcePool = new ArrayList(5);
		}
		return resourcePool;
	}
	
	private WebdavResource getNewConnection() throws HttpException, IOException{
		return new WebdavResource(getWebdavServletURL());//,usersCredentials);
	}

//	/* (non-Javadoc)
//	 * @see com.idega.slide.business.IWSlideSession#getWebdavResource(java.lang.String)
//	 */
//	//TEST
//	public WebdavResource getWebdavResource(String path) throws HttpException, IOException, RemoteException {
//		StringTokenizer tokens = new StringTokenizer(path,"/");
//		WebdavResource currentResource = getWebdavResource();
//		while ( tokens.hasMoreTokens()) {
//			String element = tokens.nextToken();
//			if(element == null || "".equals(element)){
//				continue;
//			}
//			WebdavResources childResources = currentResource.getChildResources();
//			currentResource = childResources.getResource(element);
//			if(currentResource == null){
//				throw new IOException("For path '"+path+"', '"+element+"' was not found");
//			}
//		}
//		
//		return currentResource;
//	}

	
	public void close(){
		List pool = getWebdavResourcePool();
		for (Iterator iter = pool.iterator(); iter.hasNext();) {
			try {
				WebdavResourceSession session = (WebdavResourceSession) iter.next();
				session.getWebdavResource().close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		resourcePool = new ArrayList(5);
	}
	
}
