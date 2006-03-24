/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.11 2006/03/24 16:44:09 eiki Exp $
 * Created on Mar 24, 2006
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
 *  Last modified: $Date: 2006/03/24 16:44:09 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.11 $
 */
public class IWSlideServiceHomeImpl extends IBOHomeImpl implements IWSlideServiceHome {

	protected Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws javax.ejb.CreateException {
		return (IWSlideService) super.createIBO();
	}
}
