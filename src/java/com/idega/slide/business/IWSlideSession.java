/*
 * $Id: IWSlideSession.java,v 1.5 2004/12/14 11:44:58 gummi Exp $
 * Created on 12.11.2004
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
import org.apache.webdav.lib.WebdavResource;
import com.idega.business.IBOSession;
import com.idega.slide.utils.WebdavRootResource;

/**
 * 
 *  Last modified: $Date: 2004/12/14 11:44:58 $ by $Author: gummi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public interface IWSlideSession extends IBOSession {
    /**
     * @see com.idega.slide.business.IWSlideSessionBean#valueBound
     */
    public void valueBound(HttpSessionBindingEvent arg0)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#valueUnbound
     */
    public void valueUnbound(HttpSessionBindingEvent arg0)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#getIWSlideService
     */
    public IWSlideService getIWSlideService() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#getWebdavServletURL
     */
    public String getWebdavServletURL() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#getWebdavResource
     */
    public WebdavRootResource getWebdavRootResource() throws HttpException,
            IOException, java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#getWebdavResource
     */
    public WebdavResource getWebdavResource(String path) throws HttpException,
            IOException, RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideSessionBean#close
     */
    public void close() throws java.rmi.RemoteException;

}
