/*
 * $Id: SlideFileSystemHome.java,v 1.2 2004/12/15 16:02:36 palli Exp $
 * Created on Dec 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/12/15 16:02:36 $ by $Author: palli $
 * 
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.2 $
 */
public interface SlideFileSystemHome extends IBOHome {

	public SlideFileSystem create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
