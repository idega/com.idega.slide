/*
 * $Id: IWSlideSessionHome.java,v 1.5 2005/01/07 19:16:06 gummi Exp $
 * Created on 1.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/01/07 19:16:06 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
 */
public interface IWSlideSessionHome extends IBOHome {

	public IWSlideSession create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
