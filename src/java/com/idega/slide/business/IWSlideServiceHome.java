/*
 * $Id: IWSlideServiceHome.java,v 1.3 2004/11/12 16:44:46 aron Exp $
 * Created on 12.11.2004
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
 *  Last modified: $Date: 2004/11/12 16:44:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public interface IWSlideServiceHome extends IBOHome {
    public IWSlideService create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
