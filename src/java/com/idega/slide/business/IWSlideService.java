/*
 * $Id: IWSlideService.java,v 1.6 2004/11/17 08:49:00 aron Exp $
 * Created on 17.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;


import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavFile;

import com.idega.business.IBOService;
import com.idega.user.data.User;

/**
 * 
 *  Last modified: $Date: 2004/11/17 08:49:00 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.6 $
 */
public interface IWSlideService extends IBOService {
    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServletURL
     */
    public String getWebdavServletURL() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
     */
    public HttpURL getWebdavServerURL() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
     */
    public HttpURL getWebdavServerURL(User user)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
     */
    public WebdavFile getWebdavFile(User user) throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
     */
    public WebdavFile getWebdavFile() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#createSlideSchemas
     */
    public void createSlideSchemas() throws java.rmi.RemoteException;

}
