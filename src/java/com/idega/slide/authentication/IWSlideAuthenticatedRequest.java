/*
 * $Id: IWSlideAuthenticatedRequest.java,v 1.2 2006/04/09 11:44:15 laddi Exp $
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
 *  Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public class IWSlideAuthenticatedRequest extends HttpServletRequestWrapper {

	private Principal userPrincipal;
	private Set userRoles;
	
	public IWSlideAuthenticatedRequest(HttpServletRequest request, String loginName, Set roles) {
		super(request);
		this.userPrincipal = new IWUserPrincipal(loginName);
		this.userRoles = roles;
	}
	
	public Principal getUserPrincipal(){
		return this.userPrincipal;
	}
	
	public String getRemoteUser(){
		return this.userPrincipal.getName();
	}
	
	public boolean isUserInRole(String role){
		return this.userRoles.contains(role);
	}
	
}
