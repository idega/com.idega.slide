/*
 * $Id: SlideFileSystemBean.java,v 1.2 2004/12/14 17:24:10 gummi Exp $
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
 *  Last modified: $Date: 2004/12/14 17:24:10 $ by $Author: gummi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class SlideFileSystemBean extends IBOServiceBean implements ICFileSystem , SlideFileSystem{

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
