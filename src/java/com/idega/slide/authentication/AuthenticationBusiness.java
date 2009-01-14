/*
 * $Id: AuthenticationBusiness.java,v 1.5 2009/01/14 14:21:55 civilis Exp $
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
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.WebdavResources;

import com.idega.business.IBOService;
import com.idega.slide.util.AccessControlList;

/**
 * Last modified: $Date: 2009/01/14 14:21:55 $ by $Author: civilis $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
 */
public interface AuthenticationBusiness extends IBOService {
	
	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getAllRoles
	 */
	public WebdavResources getAllRoles() throws HttpException, RemoteException,
	        IOException;
	
	/**
	 * @see com.idega.slide.authentication.AuthenticationBusinessBean#getAllRoles
	 */
	public WebdavResources getAllRoles(UsernamePasswordCredentials credentials)
	        throws HttpException, RemoteException, IOException;
	
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
	public void updateRoleMembershipForUser(String userLoginName,
	        Set roleNamesForUser, Set loginNamesOfAllLoggedOnUsers)
	        throws HttpException, RemoteException, IOException;
	
	public UsernamePasswordCredentials getRootUserCredentials()
	        throws RemoteException;
	
	public boolean isRootUser(HttpServletRequest request)
	        throws RemoteException;
	
	public AccessControlList applyDefaultPermissionsToRepository(
	        AccessControlList acl);
	
	public abstract AccessControlList applyPermissionsToRepository(
	        AccessControlList acl, Collection<String> roles);
}