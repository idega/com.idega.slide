/*
 * $Id: FileSystemCopyService.java,v 1.2 2004/11/30 15:35:50 aron Exp $
 * Created on 30.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.util.Collection;


import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBOService;

/**
 * 
 *  Last modified: $Date: 2004/11/30 15:35:50 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public interface FileSystemCopyService extends IBOService {
    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#run
     */
    public void run() throws Exception, java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#copyPages
     */
    public void copyPages(String folder, Collection pages) throws Exception,
            java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#copy
     */
    public void copy(String folder, Collection files) throws Exception,
            java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#checkAndCreateFolder
     */
    public void checkAndCreateFolder(String folder)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#createFolderIfNotExists
     */
    public void createFolderIfNotExists(String folderPath)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#getResource
     */
    public WebdavResource getResource(String path) throws Exception,
            java.rmi.RemoteException;

    /**
     * @see com.idega.slide.business.FileSystemCopyServiceBean#getService
     */
    public IWSlideService getService() throws java.rmi.RemoteException;

}
