/*
 * $Id: IWSlideServiceBean.java,v 1.11 2004/12/14 17:24:10 gummi Exp $
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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.slide.schema.SlideSchemaCreator;


/**
 * 
 *  Last modified: $Date: 2004/12/14 17:24:10 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.11 $
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
	
	public String getWebdavServerURI(){
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
	
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential){
		return getWebdavServerURL(credential,null);
	}
	
	/**
	 * Gets the root url for the webdav server with authentication
	 * @return
	 */
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential,String path){
	    
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

		       String rootPath = getWebdavServerURI();
		       String realPath = rootPath;
		       if(path!=null){
		       		realPath = rootPath+path;
		       }
		       
		       //server += getWebdavServletURL();
		       HttpURL hrl = new HttpURL(server,port,realPath);
		       
		       
			   if(credential!=null){
			       hrl.setUserinfo(credential.getUserName(),credential.getPassword());
			    }
	            return hrl;
	       }
	       return null;
        } catch (URIException e) {
           throw new IBORuntimeException(e);
        }
	}
	
	
	/**
	 * Gets resource for the webdav server with authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials, String path){
	    try {
            return new WebdavFile(getWebdavServerURL(credentials,path));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
	/**
	 * Gets the root resource for the webdav server with authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials){
	   return getWebdavFile(credentials,null);
	}
	
	/**
	 * Gets the root resource for the webdav server without any authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(){
	    return getWebdavFile(null,null);
	}
	
	/**
	 * 
	 * @return
	 */
	public UsernamePasswordCredentials getRootUserCredentials(){
//		if(lInfo!=null){
//			String password = (String)lInfo.getAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME);
//			if(password == null){
//				password = StringHandler.getRandomString(10);
//				lInfo.setAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME,password);
//			}
			return new UsernamePasswordCredentials("root","root");
//		}
		
//		return null;
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
	
	
	/**
	 * Returns the WebdavResource at path "/" and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path) throws HttpException, IOException{
		return new WebdavResource(getWebdavServerURL(getRootUserCredentials(),path));
	}
	
	/**
	 * Returns the WebdavResource at the given path and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException{
		return getWebdavResourceAuthenticatedAsRoot(null);
	}
	
	public String getApplicationServerRelativePath(String path) throws RemoteException{
		return getWebdavServerURI()+((path.startsWith("/"))?"":"/")+path;
	}
	
	public boolean getExistence(String path) throws HttpException, IOException{
		if(path==null){
			return false;
		}
		return getWebdavResourceAuthenticatedAsRoot().headMethod(((path.startsWith(getWebdavServerURI()))?path:getApplicationServerRelativePath(path)));
	}

}
