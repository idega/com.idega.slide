/*
 * $Id: SlideFileSystemBean.java,v 1.4 2004/12/16 17:59:21 eiki Exp $
 * Created on 22.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.business.FileIconSupplier;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.slide.data.SlideFile;
import com.idega.slide.data.SlideFileHome;

/**
 *  An implementation of ICFileSystem to handle files in the SLide repository.
 *  Abstracts users from using the Slide API making it easier to change
 *  repository implementation. 
 * 
 *  Last modified: $Date: 2004/12/16 17:59:21 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.4 $
 */
public class SlideFileSystemBean extends IBOServiceBean implements ICFileSystem , SlideFileSystem{

	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileIconURI(com.idega.core.file.data.ICFile)
	 */
	public String getFileIconURI(ICFile file) throws RemoteException {
		return getIconURIByMimeType(file.getMimeType());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getIconURIByMimeType(java.lang.String)
	 */
	public String getIconURIByMimeType(String mimeType) throws RemoteException {
		FileIconSupplier iconSupplier = FileIconSupplier.getInstance();
		return iconSupplier.getFileIconURIByMimeType(mimeType);
	}
    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#initialize()
     */
    public void initialize() throws RemoteException {
        
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#getFileURI(com.idega.core.file.data.ICFile)
     */
    public String getFileURI(ICFile file) throws RemoteException {
        if(file instanceof SlideFile) {
            return getSlideService().getWebdavServerURI()+((SlideFile)file).getExternalURL();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#getFileURI(int)
     */
    public String getFileURI(int fileId) throws RemoteException {
        try {
            ICFile file = ((SlideFileHome)IDOLookup.getHome(SlideFile.class)).findByPrimaryKey(new Integer(fileId));
            return getFileURI(file);
        } catch (IDOLookupException e) {
        } catch (RemoteException e) {
        } catch (FinderException e) {
        }
        return null;
    }
    
    private IWSlideService getSlideService() throws IBOLookupException{
        return (IWSlideService)getServiceInstance(IWSlideService.class);
    }

}
