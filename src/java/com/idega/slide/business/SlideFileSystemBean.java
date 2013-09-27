/*
 * $Id: SlideFileSystemBean.java,v 1.5 2005/06/02 12:05:10 gummi Exp $
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
 *  Last modified: $Date: 2005/06/02 12:05:10 $ by $Author: gummi $
 *
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public class SlideFileSystemBean extends IBOServiceBean implements ICFileSystem , SlideFileSystem{

	private static final long serialVersionUID = 806641888220638803L;

	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileIconURI(com.idega.core.file.data.ICFile)
	 */
	@Override
	public String getFileIconURI(ICFile file) throws RemoteException {
		return getIconURIByMimeType(file.getMimeType());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getIconURIByMimeType(java.lang.String)
	 */
	@Override
	public String getIconURIByMimeType(String mimeType) throws RemoteException {
		FileIconSupplier iconSupplier = FileIconSupplier.getInstance();
		return iconSupplier.getFileIconURIByMimeType(mimeType);
	}
    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#initialize()
     */
    @Override
	public void initialize() throws RemoteException {

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#getFileURI(com.idega.core.file.data.ICFile)
     */
    @Override
	public String getFileURI(ICFile file) throws RemoteException {
        if(file instanceof SlideFile) {
            return getSlideService().getWebdavServerURI()+((SlideFile)file).getExternalURL();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSystem#getFileURI(int)
     */
    @Override
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
        return getServiceInstance(IWSlideService.class);
    }
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileURI(int, java.lang.String)
	 */
	@Override
	public String getFileURI(int fileId, String datasource) throws RemoteException {
		// TODO Auto-generated method stub
		return getFileURI(fileId);
	}

}
