/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.9 2004/12/22 20:13:18 gummi Exp $
 * Created on 21.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/12/22 20:13:18 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.9 $
 */
public class IWSlideServiceHomeImpl extends IBOHomeImpl implements IWSlideServiceHome {

	protected Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws javax.ejb.CreateException {
		return (IWSlideService) super.createIBO();
	}
}
