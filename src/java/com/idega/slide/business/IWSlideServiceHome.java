/*
 * $Id: IWSlideServiceHome.java,v 1.5 2004/11/17 08:49:00 aron Exp $
 * Created on 17.11.2004
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
 *  Last modified: $Date: 2004/11/17 08:49:00 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public interface IWSlideServiceHome extends IBOHome {
    public IWSlideService create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
