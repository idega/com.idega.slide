/*
 * $Id: SlideFileSystem.java,v 1.1 2004/11/29 16:16:45 aron Exp $
 * Created on 22.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.rmi.RemoteException;


import com.idega.business.IBOService;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;

/**
 * 
 *  Last modified: $Date: 2004/11/29 16:16:45 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface SlideFileSystem extends IBOService, ICFileSystem {
    /**
     * @see com.idega.slide.business.SlideFileSystemBean#initialize
     */
    public void initialize() throws RemoteException;

    /**
     * @see com.idega.slide.business.SlideFileSystemBean#getFileURI
     */
    public String getFileURI(ICFile file) throws RemoteException;

    /**
     * @see com.idega.slide.business.SlideFileSystemBean#getFileURI
     */
    public String getFileURI(int fileId) throws RemoteException;
    

}
