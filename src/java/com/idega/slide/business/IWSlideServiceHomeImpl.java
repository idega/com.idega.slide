/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.10 2006/02/23 18:40:30 eiki Exp $
 * Created on Feb 23, 2006
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
 *  Last modified: $Date: 2006/02/23 18:40:30 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.10 $
 */
public class IWSlideServiceHomeImpl extends IBOHomeImpl implements IWSlideServiceHome {

	protected Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws javax.ejb.CreateException {
		return (IWSlideService) super.createIBO();
	}
}
