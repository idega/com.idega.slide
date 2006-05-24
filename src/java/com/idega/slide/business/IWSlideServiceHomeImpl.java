/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.12 2006/05/24 16:52:33 thomas Exp $
 * Created on May 24, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2006/05/24 16:52:33 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.12 $
 */
public class IWSlideServiceHomeImpl extends IBOHomeImpl implements IWSlideServiceHome {

	protected Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws javax.ejb.CreateException {
		return (IWSlideService) super.createIBO();
	}
}
