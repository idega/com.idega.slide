/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.13 2006/08/30 16:54:40 valdas Exp $
 * Created on May 24, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class IWSlideServiceHomeImpl extends IBOHomeImpl implements IWSlideServiceHome {
	public Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws CreateException {
		return (IWSlideService) super.createIBO();
	}
}