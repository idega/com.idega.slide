/*
 * $Id: IWSlideServiceBean.java,v 1.1 2004/11/01 10:42:18 gummi Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOServiceBean;


/**
 * 
 *  Last modified: $Date: 2004/11/01 10:42:18 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IWSlideServiceBean extends IBOServiceBean  implements IWSlideService {

	protected static final String WEBDAV_SERVLET_URI = "/servlet/webdav/";
//	private static Credentials guestCredentials = new UsernamePasswordCredentials("guest","guest");
	
	/**
	 * 
	 */
	public IWSlideServiceBean() {
		super();
	}
	
	public String getWebdavServletURL(){
		return getIWMainApplication().getApplicationContextURI()+WEBDAV_SERVLET_URI;
	}
	
//	public Credentials getGuestCredentials(){
//		return guestCredentials;
//	}
}
