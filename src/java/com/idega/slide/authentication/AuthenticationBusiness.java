/*
 * $Id: AuthenticationBusiness.java,v 1.2 2005/02/23 15:49:51 gummi Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Set;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavResources;
import com.idega.business.IBOService;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2005/02/23 15:49:51 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public interface AuthenticationBusiness extends IBOService {

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getAllRoles
	 */
	public WebdavResources getAllRoles() throws HttpException, RemoteException, IOException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getAllRoles
	 */
	public WebdavResources getAllRoles(UsernamePasswordCredentials credentials) throws HttpException, RemoteException,
			IOException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getUserURI
	 */
	public String getUserURI(String userName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getUserPath
	 */
	public String getUserPath(String userName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getGroupURI
	 */
	public String getGroupURI(String groupName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getGroupPath
	 */
	public String getGroupPath(String groupName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getRoleURI
	 */
	public String getRoleURI(String roleName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getRolePath
	 */
	public String getRolePath(String roleName) throws RemoteException;

	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#updateRoleMembershipForUser
	 */
	public void updateRoleMembershipForUser(String userLoginName, Set roleNamesForUser, Set loginNamesOfAllLoggedOnUsers)
			throws HttpException, RemoteException, IOException;
	
	public UsernamePasswordCredentials getRootUserCredentials() throws RemoteException;
	
	public boolean isRootUser(IWContext iwc) throws RemoteException;
}
