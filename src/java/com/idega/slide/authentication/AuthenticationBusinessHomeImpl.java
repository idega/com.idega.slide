/*
 * $Id: AuthenticationBusinessHomeImpl.java,v 1.1 2004/12/13 13:12:32 gummi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.authentication;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/12/13 13:12:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class AuthenticationBusinessHomeImpl extends IBOHomeImpl implements AuthenticationBusinessHome {

	protected Class getBeanInterfaceClass() {
		return AuthenticationBusiness.class;
	}

	public AuthenticationBusiness create() throws javax.ejb.CreateException {
		return (AuthenticationBusiness) super.createIBO();
	}
}
