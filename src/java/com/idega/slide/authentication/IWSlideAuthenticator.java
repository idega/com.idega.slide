/*
 * $Id: IWSlideAuthenticator.java,v 1.1 2004/12/13 13:12:32 gummi Exp $
 * Created on 8.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpException;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2004/12/13 13:12:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IWSlideAuthenticator implements Filter {

	private static int tmpCounter = 0;
	private static int tmpHeaderCount = 0;
	private static final String SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME = "org.apache.slide.webdav.method.principal";
	
	private LoginBusinessBean loginBusiness = new LoginBusinessBean();
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response = (HttpServletResponse)arg1;
		
//		Enumeration headerNames = request.getHeaderNames();
//		System.out.println("------------HEADER "+(tmpHeaderCount)+" BEGINS-------------");
//		while (headerNames.hasMoreElements()) {
//			String headerName = (String) headerNames.nextElement();
//			System.out.println("\t["+headerName+"]: "+request.getHeader(headerName));
//		}
//		System.out.println("------------HEADER "+(tmpHeaderCount)+" ENDS-------------");
		
//		Enumeration parameterNames = request.getParameterNames();
//		System.out.println("------------PARAMETERS "+(tmpHeaderCount)+" BEGINS-------------");
//		while (parameterNames.hasMoreElements()) {
//			String parameterName = (String) parameterNames.nextElement();
//			System.out.println("\t["+parameterNames+"]: "+request.getParameter(parameterName));
//		}
//		System.out.println("------------PARAMETERS "+(tmpHeaderCount++)+" ENDS-------------");
//		
		
		IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
		
		try{
			if(iwc.isLoggedOn()){
				LoggedOnInfo lInfo = getLoginBusiness(iwc).getLoggedOnInfo(iwc);
				if(request.getUserPrincipal()==null && lInfo != null){
					request = new IWSlideAuthenticatedRequest(request,lInfo.getLogin(),lInfo.getUserRoles());
				}
				updateRolesForUser(iwc, lInfo);
			} else {
				String[] loginAndPassword = getLoginBusiness(iwc).getLoginNameAndPasswordFromBasicAuthenticationRequest(iwc);
				String loggedInUser = (String)iwc.getSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
				if(loginAndPassword != null){
					String username = loginAndPassword[0];
					String password = loginAndPassword[1];
					LoggedOnInfo lInfo = getLoginBusiness(iwc).getLoggedOnInfo(iwc,username);
					
					if(loggedInUser==null){
						if(isAuthenticated(iwc,lInfo,username,password)){
							if(username.equals("root")){
								request = new IWSlideAuthenticatedRequest(request,username,Collections.singleton("root"));
							} else {
								request = new IWSlideAuthenticatedRequest(request,username,lInfo.getUserRoles());
								updateRolesForUser(iwc,lInfo);
							}
						} else {
							setAsUnauthenticatedInSlide(iwc);
						}
					} else if(!username.equals(loggedInUser)){
						//request.getSession().invalidate();
						if(isAuthenticated(iwc,lInfo,username,password)){
							if(username.equals("root")){
								request = new IWSlideAuthenticatedRequest(request,username,Collections.singleton("root"));
							} else {
								request = new IWSlideAuthenticatedRequest(request,username,lInfo.getUserRoles());
								updateRolesForUser(iwc,lInfo);
							}
						} else {
							setAsUnauthenticatedInSlide(iwc);
						}
					}
		
				} else if(loggedInUser!=null){
						setAsUnauthenticatedInSlide(iwc);
				}
			}
		}
		catch (HttpException e) {
			e.printStackTrace();
			iwc.getResponse().sendError(e.getReasonCode(),e.getReason());
			return;
		}
			
//		if(tmpCounter<0){
//			tmpCounter++;
//			
//			AuthenticationBusiness business = (AuthenticationBusiness)IBOLookup.getServiceInstance(iwc,AuthenticationBusiness.class);
//			business.tmpPrintOutGroupSetMembers(business.getAllRoles());
//			
//		}
		
		arg2.doFilter(request, response);
	}
	
	/**
	 * @param iwc
	 */
	private void setAsUnauthenticatedInSlide(IWContext iwc) {
		iwc.removeSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}

	/**
	 * @param lInfo
	 * @throws IOException
	 * @throws RemoteException
	 * @throws HttpException
	 */
	private void updateRolesForUser(IWContext iwc, LoggedOnInfo lInfo) throws HttpException, RemoteException, IOException {
		if(lInfo != null){
			if(lInfo.getAttribute("iw_slide_roles_updated")==null){
				AuthenticationBusiness business = (AuthenticationBusiness)IBOLookup.getServiceInstance(iwc,AuthenticationBusiness.class);
				business.updateRoleMembershipForUser(lInfo.getLogin(),lInfo.getUserRoles(),null);
				lInfo.setAttribute("iw_slide_roles_updated",Boolean.TRUE);
			}
		}
	}

	private boolean isAuthenticated(IWContext iwc, LoggedOnInfo info, String login, String password){
		if(iwc.isLoggedOn()){
			return true;
		} else {
			//TMP root authentication
			//TODO: authenticate root user
			if("root".equals(login)&&"root".equals(password)){
				return true;
			}
			if(info != null){
				String slidePassword = (String)info.getAttribute("iw_slide_password");
				if(slidePassword!=null){
					return slidePassword.equals(password);
				}
			}
		}
		return false;
	}

	protected LoginBusinessBean getLoginBusiness(IWContext iwc){
		return loginBusiness;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}
