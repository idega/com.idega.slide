/*
 * $Id: IWSlideServiceBean.java,v 1.4 2004/11/15 14:07:06 aron Exp $
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
import org.apache.webdav.lib.WebdavFile;

import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.slide.schema.SlideSchemaCreator;


/**
 * 
 *  Last modified: $Date: 2004/11/15 14:07:06 $ by $Author: aron $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.4 $
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
		return getIWMainApplication().getApplicationContextURI()+WEBDAV_SERVLET_URI;
	}
	
	
	/**
	 * Gets the root url for the webdav server without any authentication
	 * @return
	 */
	public HttpURL getWebdavServerURL(){
	    
	    try {
	       String server = getIWApplicationContext().getDomain().getServerName();
	       if(server!=null){
		       if(server.endsWith("/"))
		           server = server.substring(0,server.lastIndexOf("/"));
		       server += getWebdavServletURL();
		       HttpURL hrl = new HttpURL(server);
	            //hrl.setUserinfo("root","root");
	            //hrl.setUserInfo("user","pass");
	            return hrl;
	       }
	       return null;
           
        } catch (URIException e) {
           throw new IBORuntimeException(e);
        }
	}
	
	/**
	 * Gets the root resource for the webdav server without any authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(){
	    try {
            return new WebdavFile(getWebdavServerURL());
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
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
	 * Moves all files from the IC file system to Slide
	 */
	public void copyFileSystemToSlide(){
	    try {
            ((FileSystemCopyService)getServiceInstance(FileSystemCopyService.class)).run();
        } catch (IBOLookupException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
