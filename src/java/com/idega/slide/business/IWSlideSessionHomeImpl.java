/*
 * $Id: IWSlideSessionHomeImpl.java,v 1.4 2004/12/29 11:32:16 gimmi Exp $
 * Created on 28.12.2004
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
 *  Last modified: $Date: 2004/12/29 11:32:16 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.4 $
 */
public class IWSlideSessionHomeImpl extends IBOHomeImpl implements IWSlideSessionHome {

	protected Class getBeanInterfaceClass() {
		return IWSlideSession.class;
	}

	public IWSlideSession create() throws javax.ejb.CreateException {
		return (IWSlideSession) super.createIBO();
	}
}
