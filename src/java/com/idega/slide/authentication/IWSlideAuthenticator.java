/*
 * $Id: IWSlideAuthenticator.java,v 1.8 2005/02/23 15:49:51 gummi Exp $
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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpException;
import org.apache.slide.webdav.util.WebdavUtils;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.business.IWSlideSession;


/**
 * 
 *  Last modified: $Date: 2005/02/23 15:49:51 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.8 $
 */
public class IWSlideAuthenticator implements Filter {

//	private static int tmpCounter = 0;
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
		
		IWContext iwc = new IWContext((HttpServletRequest)arg0, (HttpServletResponse)arg1, ((HttpServletRequest)arg0).getSession().getServletContext());
		
		try{
			if(iwc.isLoggedOn()){
				LoggedOnInfo lInfo = getLoginBusiness(iwc).getLoggedOnInfo(iwc);
				setAsAuthenticatedInSlide(iwc,lInfo.getLogin(),lInfo);
			} else {
				String[] loginAndPassword = getLoginBusiness(iwc).getLoginNameAndPasswordFromBasicAuthenticationRequest(iwc);
				String loggedInUser = getUserAuthenticatedBySlide(iwc);
				if(loginAndPassword != null){
					String username = loginAndPassword[0];
					String password = loginAndPassword[1];
					LoggedOnInfo lInfo = getLoginBusiness(iwc).getLoggedOnInfo(iwc,username);
					if(loggedInUser==null){
						if(isAuthenticated(iwc,lInfo,username,password)){
							setAsAuthenticatedInSlide(iwc,username,lInfo);
						} else {
							setAsUnauthenticatedInSlide(iwc);
						}
					} else if(!username.equals(loggedInUser)){
						//request.getSession().invalidate();
						if(isAuthenticated(iwc,lInfo,username,password)){
							setAsAuthenticatedInSlide(iwc,username,lInfo);
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
		
		// the slide token is set so that business methods can get it from IWSlideSession.   
		// The WebdavUtils#getSlideToken(request) can be expensive since it copies pointers to all attributes from session to the token.
		// This is used e.g. to check for permissions(i.e. to calculate permissions using the ACLSecurityImpl) 
		IWSlideSession slideSession = (IWSlideSession)IBOLookup.getSessionInstance(iwc,IWSlideSession.class);
		slideSession.setSlideToken(WebdavUtils.getSlideToken(iwc.getRequest()));
		
		arg2.doFilter(iwc.getRequest(), iwc.getResponse());
	}
	
	/**
	 * @param iwc
	 * @return
	 */
	private String getUserAuthenticatedBySlide(IWContext iwc) {
		return (String)iwc.getSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}

	/**
	 * @param iwc
	 * @throws IBOLookupException
	 */
	private void setAsUnauthenticatedInSlide(IWContext iwc) throws IBOLookupException {
		iwc.removeSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}
	
	private void setAsAuthenticatedInSlide(IWContext iwc,String loginName, LoggedOnInfo lInfo) throws HttpException, RemoteException, IOException{
		if(iwc.isLoggedOn()){
			if(iwc.getUserPrincipal()==null && lInfo != null){
				iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,lInfo.getUserRoles()));
			}
			updateRolesForUser(iwc, lInfo);
		} else {
			if(getAuthenticationBusiness(iwc).isRootUser(iwc)){
				iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,Collections.singleton("root")));
			} else {
				iwc.setRequest(new IWSlideAuthenticatedRequest(iwc.getRequest(),loginName,lInfo.getUserRoles()));
				updateRolesForUser(iwc,lInfo);
			}
		}
		iwc.setSessionAttribute(SLIDE_USER_PRINCIPAL_ATTRIBUTE_NAME,loginName);
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
				generateUserFolders(iwc);
				lInfo.setAttribute("iw_slide_roles_updated",Boolean.TRUE);
			}
		}
	}
	
	private void generateUserFolders(IWContext iwc) throws HttpException, RemoteException, IOException{
		IWSlideService slideService = (IWSlideService)IBOLookup.getServiceInstance(iwc,IWSlideService.class);
		slideService.generateUserFolders(iwc.getRemoteUser());
	}

	private boolean isAuthenticated(IWContext iwc, LoggedOnInfo info, String login, String password) throws IBOLookupException, RemoteException{
		if(iwc.isLoggedOn()){
			return true;
		} else {
			if(getAuthenticationBusiness(iwc).isRootUser(iwc)){
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
	
	protected AuthenticationBusiness getAuthenticationBusiness(IWContext iwc) throws IBOLookupException {
		return (AuthenticationBusiness) IBOLookup.getServiceInstance(iwc,AuthenticationBusiness.class);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}
