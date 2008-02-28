/*
 * $Id: IWSlideAuthenticator.java,v 1.25 2008/02/28 17:03:56 eiki Exp $
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

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.slide.webdav.util.WebdavUtils;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.SpringBeanLookup;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.BaseFilter;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.business.IWSlideSession;
import com.idega.slide.util.AccessControlList;
import com.idega.util.CoreConstants;


/**
 * <p>
 * This filter is mapped before any request to the Slide WebdavServlet to make sure
 * a logged in user from idegaWeb is logged also into the Slide authentication system.
 * </p>
 *  Last modified: $Date: 2008/02/28 17:03:56 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.25 $
 */
public class IWSlideAuthenticator extends BaseFilter{

	private static final String SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME = "org.apache.slide.webdav.method.principal";

	private static final String PROPERTY_ENABLED = "slide.authenticator.enable";
	private static final String PROPERTY_UPDATE_ROLES = "slide.updateroles.enable";
	
	private LoginBusinessBean loginBusiness = new LoginBusinessBean();
	
	private boolean defaultPermissionsApplied = false;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
		ServletException{
	
		HttpServletRequest hRequest = (HttpServletRequest)request;
		
		//String requestUri = hRequest.getRequestURI();
		//System.out.println(" - '"+requestUri+"'");
		
		boolean isEnabled=isEnabled(hRequest);
		if(isEnabled){
			doAuthentication(request,response,chain);
			
			if (!defaultPermissionsApplied) {
				defaultPermissionsApplied = true;
				defaultPermissionsApplied = applyDefaultPermissionsToRepository(hRequest.getSession());
			}
		}
		else{
			chain.doFilter(request,response);
		}
	
	}
	
	private boolean applyDefaultPermissionsToRepository(HttpSession session) {
		try {
			IWSlideService slideService = (IWSlideService) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), IWSlideService.class);
			slideService.createAllFoldersInPathAsRoot(CoreConstants.CONTENT_PATH);
			AccessControlList aclCMS = slideService.getAccessControlList(CoreConstants.CONTENT_PATH);
			AccessControlList aclPublic = slideService.getAccessControlList(CoreConstants.PUBLIC_PATH);
			aclCMS = slideService.getAuthenticationBusiness().applyDefaultPermissionsToRepository(aclCMS);
			aclPublic = slideService.getAuthenticationBusiness().applyDefaultPermissionsToRepository(aclPublic);
			slideService.storeAccessControlList(aclCMS);
			slideService.storeAccessControlList(aclPublic);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method isEnabled
	 * </p>
	 * @return
	 */
	private boolean isEnabled(HttpServletRequest request) {
		
		IWMainApplication iwma = getIWMainApplication(request);
		
		String prop = iwma.getSettings().getProperty(PROPERTY_ENABLED);
		if(prop==null){
			return true;
		}
		else{
			return Boolean.valueOf(prop).booleanValue();
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doAuthentication(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
			ServletException {	
		
		//IWContext iwc = new IWContext((HttpServletRequest)arg0, (HttpServletResponse)arg1, ((HttpServletRequest)arg0).getSession().getServletContext());
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response = (HttpServletResponse)arg1;
		HttpSession session = request.getSession();
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		//HttpServletRequest newRequest = request;
		
		try{
			if(loginBusiness.isLoggedOn(request)){
				LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session);
				request = setAsAuthenticatedInSlide(request,lInfo.getLogin(),lInfo);
			} else {
				String[] loginAndPassword = loginBusiness.getLoginNameAndPasswordFromBasicAuthenticationRequest(request);
				String loggedInUser = getUserAuthenticatedBySlide(session);
				if(loginAndPassword != null){
					String username = loginAndPassword[0];
					String password = loginAndPassword[1];
					LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session,username);
					if(loggedInUser==null){
						if(isAuthenticated(request,lInfo,username,password)){
							request = setAsAuthenticatedInSlide(request,username,lInfo);
						} else {
							setAsUnauthenticatedInSlide(session);
						}
					} else if(!username.equals(loggedInUser)){
						//request.getSession().invalidate();
						if(isAuthenticated(request,lInfo,username,password)){
							request = setAsAuthenticatedInSlide(request,username,lInfo);
						} else {
							setAsUnauthenticatedInSlide(session);
						}
					}
		
				} else if(loggedInUser!=null){
						setAsUnauthenticatedInSlide(session);
				}
			}
		}
		catch (HttpException e) {
			e.printStackTrace();
			response.sendError(e.getReasonCode(),e.getReason());
			return;
		}
		
		// the slide token is set so that business methods can get it from IWSlideSession.   
		// The WebdavUtils#getSlideToken(request) can be expensive since it copies pointers to all attributes from session to the token.
		// This is used e.g. to check for permissions(i.e. to calculate permissions using the ACLSecurityImpl) 
		IWSlideSession slideSession = (IWSlideSession)IBOLookup.getSessionInstance(session,IWSlideSession.class);
		slideSession.setSlideToken(WebdavUtils.getSlideToken(request));
		
		arg2.doFilter(request,response);
		
		//2005.05.27 - Gummi
		//Workaround to ensure that the response is fully flushed.  
		//Needed because of troubles with jakarta-slide.
		//iwc.getWriter().flush();
	}
	
	/**
	 * @param iwc
	 * @return
	 */
	private String getUserAuthenticatedBySlide(HttpSession session) {
		return (String)session.getAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}

	/**
	 * @param session
	 * @throws IBOLookupException
	 */
	private void setAsUnauthenticatedInSlide(HttpSession session) throws IBOLookupException {
		session.removeAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}
	
	private HttpServletRequest setAsAuthenticatedInSlide(HttpServletRequest request,String loginName, LoggedOnInfo lInfo) throws HttpException, RemoteException, IOException{
		String slidePrincipal = loginName;
		//HttpServletRequest returnRequest = request;
		HttpSession session = request.getSession();
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		if(loginBusiness.isLoggedOn(request)){	
			LoginSession loginSession = SpringBeanLookup.getInstance().getSpringBean(request.getSession(), LoginSession.class);
			if(loginSession.isSuperAdmin()){
				String rootUserName = getAuthenticationBusiness(request).getRootUserCredentials().getUserName();
				//iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),rootUserName,Collections.singleton(rootUserName)));
				request = new IWSlideAuthenticatedRequest(request,rootUserName,Collections.singleton(rootUserName));
				slidePrincipal=rootUserName;
			} else {
				if(request.getUserPrincipal()==null && lInfo != null){
				//if(iwc.getUserPrincipal()==null && lInfo != null){
					//iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,lInfo.getUserRoles()));
					request = new IWSlideAuthenticatedRequest(request,loginName,lInfo.getUserRoles());
				}
				updateRolesForUser(request, lInfo);
			}
		} else {
			String rootUserName = getAuthenticationBusiness(request).getRootUserCredentials().getUserName();
			if(loginName.equals(rootUserName)){
				//iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,Collections.singleton(rootUserName)));
				request = new IWSlideAuthenticatedRequest(request,rootUserName,Collections.singleton(rootUserName));
			} else {
				//iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,lInfo.getUserRoles()));
				request = new IWSlideAuthenticatedRequest(request,loginName,lInfo.getUserRoles());
				updateRolesForUser(request,lInfo);
			}
		}
		//iwc.setSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME,slidePrincipal);
		session.setAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME,slidePrincipal);
		return request;
	}

	/**
	 * @param lInfo
	 * @throws IOException
	 * @throws RemoteException
	 * @throws HttpException
	 */
	private void updateRolesForUser(HttpServletRequest request, LoggedOnInfo lInfo) throws HttpException, RemoteException, IOException {
		boolean doUpdateRoles = true;
		IWMainApplication iwma = getIWMainApplication(request);
		String prop = iwma.getSettings().getProperty(PROPERTY_UPDATE_ROLES);
		if(prop!=null){
			doUpdateRoles=Boolean.valueOf(prop).booleanValue();
		}
		if(doUpdateRoles){
			if(lInfo != null){
				if(lInfo.getAttribute("iw_slide_roles_updated")==null){
					AuthenticationBusiness business = getAuthenticationBusiness(request);
					business.updateRoleMembershipForUser(lInfo.getLogin(),lInfo.getUserRoles(),null);
					generateUserFolders(request);
					lInfo.setAttribute("iw_slide_roles_updated",Boolean.TRUE);
				}
			}
		}
	}
	
	private void generateUserFolders(HttpServletRequest request) throws HttpException, RemoteException, IOException{
		IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
		IWSlideService slideService = (IWSlideService)IBOLookup.getServiceInstance(iwac,IWSlideService.class);
		slideService.generateUserFolders(request.getRemoteUser());
	}

	private boolean isAuthenticated(HttpServletRequest request, LoggedOnInfo info, String login, String password) throws IBOLookupException, RemoteException{
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		if(loginBusiness.isLoggedOn(request)){
			return true;
		} else {
			if(getAuthenticationBusiness(request).isRootUser(request)){
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
		return this.loginBusiness;
	}
	
	protected AuthenticationBusiness getAuthenticationBusiness(HttpServletRequest request) throws IBOLookupException {
		IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
		return (AuthenticationBusiness) IBOLookup.getServiceInstance(iwac,AuthenticationBusiness.class);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}
