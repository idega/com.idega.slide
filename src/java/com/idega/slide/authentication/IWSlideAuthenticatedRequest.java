/*
 * $Id: IWSlideAuthenticatedRequest.java,v 1.1 2004/12/13 13:12:32 gummi Exp $
 * Created on 11.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.authentication;

import java.security.Principal;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import com.idega.core.accesscontrol.jaas.IWUserPrincipal;


/**
 * 
 *  Last modified: $Date: 2004/12/13 13:12:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IWSlideAuthenticatedRequest extends HttpServletRequestWrapper {

	private Principal userPrincipal;
	private Set userRoles;
	
	public IWSlideAuthenticatedRequest(HttpServletRequest request, String loginName, Set roles) {
		super(request);
		userPrincipal = new IWUserPrincipal(loginName);
		userRoles = roles;
	}
	
	public Principal getUserPrincipal(){
		return userPrincipal;
	}
	
	public String getRemoteUser(){
		return userPrincipal.getName();
	}
	
	public boolean isUserInRole(String role){
		return userRoles.contains(role);
	}
	
}
