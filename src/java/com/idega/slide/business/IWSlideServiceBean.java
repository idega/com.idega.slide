/*
 * $Id: IWSlideServiceBean.java,v 1.2 2004/11/12 16:30:36 aron Exp $
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
import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWUserContext;


/**
 * 
 *  Last modified: $Date: 2004/11/12 16:30:36 $ by $Author: aron $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
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
	
	public HttpURL getWebdavServerURL(IWUserContext iwuc){
	    
	    try {
	       String server = iwuc.getApplicationContext().getDomain().getServerName();
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
	
	public WebdavResource getWebdavResource(IWUserContext iwuc){
	    try {
            return new WebdavResource(getWebdavServerURL(iwuc));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
	public WebdavFile getWebdavFile(IWUserContext iwuc){
	    try {
            return new WebdavFile(getWebdavServerURL(iwuc));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
//	public Credentials getGuestCredentials(){
//		return guestCredentials;
//	}
}
