/*
 * $Id: SlideFileSystem.java,v 1.2 2004/12/15 16:02:36 palli Exp $
 * Created on Dec 15, 2004
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
 *  Last modified: $Date: 2004/12/15 16:02:36 $ by $Author: palli $
 * 
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.2 $
 */
public interface SlideFileSystem extends IBOService, ICFileSystem {

	/**
	 * @see com.idega.slide.business.SlideFileSystemBean#getFileIconURI
	 */
	public String getFileIconURI(ICFile file) throws RemoteException;

	/**
	 * @see com.idega.slide.business.SlideFileSystemBean#getIconURIByMimeType
	 */
	public String getIconURIByMimeType(String mimeType) throws RemoteException;

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
