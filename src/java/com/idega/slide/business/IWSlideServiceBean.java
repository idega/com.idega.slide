/*
 * $Id: IWSlideServiceBean.java,v 1.8 2004/11/18 10:36:39 tryggvil Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.WebdavFile;

import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.slide.schema.SlideSchemaCreator;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2004/11/18 10:36:39 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.8 $
 */
public class IWSlideServiceBean extends IBOServiceBean  implements IWSlideService {

	protected static final String WEBDAV_SERVLET_URI = "/servlet/webdav";
//	private static Credentials guestCredentials = new UsernamePasswordCredentials("guest","guest");
	
	/**
	 * 
	 */
	public IWSlideServiceBean() {
		super();
	}
	
	public String getWebdavServletURL(){
		String appContext = getIWMainApplication().getApplicationContextURI();
		if (appContext.endsWith("/")){
			appContext = appContext.substring(0, appContext.lastIndexOf("/"));			
		}
		return appContext+WEBDAV_SERVLET_URI;
	}
	
	/**
	 * Gets the URL from with a path in the filesystem (e.g.) /files/content
	 * @param path
	 * @return
	 */
	public HttpURL getWebdavServerURL(String path){
		return getWebdavServerURL(null,path);
	}
	
	public HttpURL getWebdavServerURL(){
		return getWebdavServerURL(null,null);
	}
	
	public HttpURL getWebdavServerURL(User user){
		return getWebdavServerURL(user,null);
	}
	
	/**
	 * Gets the root url for the webdav server with authentication
	 * @return
	 */
	public HttpURL getWebdavServerURL(User user,String path){
	    
	    try {
	       String server = getIWApplicationContext().getDomain().getServerName();
	       if(server!=null){
	       		int port = 80;
		       if(server.endsWith("/"))
		           server = server.substring(0,server.lastIndexOf("/"));
		       if(server.startsWith("http://"))
		       		server = server.substring(7,server.length());
		       if(server.indexOf(":")!=-1){
		       		String sPort = server.substring(server.indexOf(":")+1,server.length());
		       		port = Integer.parseInt(sPort);
		       		server = server.substring(0,server.indexOf(":"));
		       }

		       String rootPath = getWebdavServletURL();
		       String realPath = rootPath;
		       if(path!=null){
		       		realPath = rootPath+path;
		       }
		       
		       //server += getWebdavServletURL();
		       HttpURL hrl = new HttpURL(server,port,realPath);
		       
		       
			   if(user!=null){
			       //TODO: Implement real user authorization, now hardcoded as root
			       hrl.setUserinfo("root","root");
			       //hrl.setUserInfo("user","pass");
			    }
	            return hrl;
	       }
	       return null;
        } catch (URIException e) {
           throw new IBORuntimeException(e);
        }
	}

	/**
	 * Gets the root resource for the webdav server with authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(User user){
	    try {
            return new WebdavFile(getWebdavServerURL(user));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
	
	/**
	 * Gets the root resource for the webdav server without any authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(){
	    return getWebdavFile(null);
	}
	
	
	/**
	 * Auto creates the Slide sql schema structure
	 */
	public void createSlideSchemas(){
	    try {
			new SlideSchemaCreator().createSchemas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
