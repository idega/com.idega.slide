/*
 * $Id: IWSlideService.java,v 1.2 2004/11/12 16:30:36 aron Exp $
 * Created on 5.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;


import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBOService;
import com.idega.idegaweb.IWUserContext;

/**
 * 
 *  Last modified: $Date: 2004/11/12 16:30:36 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public interface IWSlideService extends IBOService {
    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServletURL
     */
    public String getWebdavServletURL() throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
     */
    public HttpURL getWebdavServerURL(IWUserContext iwuc)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResource
     */
    public WebdavResource getWebdavResource(IWUserContext iwuc)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
     */
    public WebdavFile getWebdavFile(IWUserContext iwuc)
            throws java.rmi.RemoteException;

}
