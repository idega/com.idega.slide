/*
 * $Id: SlideFileSystemHome.java,v 1.1 2004/11/29 16:16:45 aron Exp $
 * Created on 22.11.2004
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
 *  Last modified: $Date: 2004/11/29 16:16:45 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface SlideFileSystemHome extends IBOHome {
    public SlideFileSystem create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
