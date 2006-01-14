/*
 * $Id: AuthenticationBusinessBean.java,v 1.10 2006/01/14 22:40:46 laddi Exp $
 * Created on 9.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.util.PropertyParser;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2006/01/14 22:40:46 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.10 $
 */
public class AuthenticationBusinessBean extends IBOServiceBean  implements AuthenticationBusiness{
	
	private static final String PATH_USERS = IWSlideConstants.PATH_USERS;
	private static final String PATH_GROUPS = IWSlideConstants.PATH_GROUPS;
	private static final String PATH_ROLES = IWSlideConstants.PATH_ROLES;
	private static final String SLASH = "/";
	//private static final String SLIDE_ROLE_NAME_ROOT = "root";
	private static final String SLIDE_DEFAULT_ROOT_USER = "root";
	private static final String SLIDE_ROLE_NAME_USER = "user";
	//private static final String SLIDE_ROLE_NAME_GUEST = "guset";
	private IWSlideService slideService = null;
	private static final String GROUP_MEMBER_SET = "group-member-set";
	private static final PropertyName GROUP_MEMBER_SET_PROPERTY_NAME = new PropertyName("DAV:",GROUP_MEMBER_SET);
	private static final String NO_PASSWORD = "no_password";
	private static final String ROOT_USER_NAME = "root";
	private final UsernamePasswordCredentials rootCredential = new UsernamePasswordCredentials(ROOT_USER_NAME,NO_PASSWORD);
	//private LoginBusinessBean _loginBusiness = new LoginBusinessBean();
	
	
	public WebdavResources getAllRoles() throws HttpException, RemoteException, IOException{
		return getAllRoles(null);
	}
	
	public WebdavResources getAllRoles(UsernamePasswordCredentials credentials) throws HttpException, RemoteException, IOException{
		IWSlideService service = getSlideServiceInstance();
		WebdavResource rolesFolder = new WebdavResource(service.getWebdavServerURL(credentials, PATH_ROLES));
		return rolesFolder.getChildResources();
	}
	
	public String getUserURI(String userName) throws RemoteException{
		IWSlideService service = getSlideServiceInstance();
		return service.getWebdavServerURI()+getUserPath(userName);
	}
	
	public String getUserPath(String userName) throws RemoteException{
		return PATH_USERS+SLASH+userName;
	}
	
	public String getGroupURI(String groupName) throws RemoteException{
		IWSlideService service = getSlideServiceInstance();
		return service.getWebdavServerURI()+getGroupPath(groupName);
	}
	
	public String getGroupPath(String groupName) throws RemoteException{
		return PATH_GROUPS+SLASH+groupName;
	}
	
	public String getRoleURI(String roleName) throws RemoteException{
		IWSlideService service = getSlideServiceInstance();
		return service.getWebdavServerURI()+getRolePath(roleName);
	}
	
	public String getRolePath(String roleName) throws RemoteException{
		return PATH_ROLES+SLASH+roleName;
	}
	
	/**
	 * 
	 * @param loginName
	 * @param roleNamesForUser
	 * @param loginNameOfAllLoggedOnUsers Set of all users that are logged on, other users are removed from roles.  If the set is null no users are removed from roles.
	 * @throws IOException
	 * @throws RemoteException
	 * @throws HttpException
	 */
	public void updateRoleMembershipForUser(String userLoginName, Set roleNamesForUser, Set loginNamesOfAllLoggedOnUsers) throws HttpException, RemoteException, IOException{
		if(userLoginName!=null && userLoginName.length()>0 && !userLoginName.equals(SLIDE_DEFAULT_ROOT_USER) ){
			IWSlideService service = getSlideServiceInstance();
			UsernamePasswordCredentials rCredentials = service.getRootUserCredentials();
			

			if(!service.getExistence(getUserPath(userLoginName))){
				WebdavResource user = new WebdavResource(service.getWebdavServerURL(rCredentials,getUserPath(userLoginName)), WebdavResource.NOACTION, 0);
				user.mkcolMethod();
				user.close();
			}
			
			Set newRoles = new HashSet(roleNamesForUser);
			Enumeration e = getAllRoles(rCredentials).getResources();
			String userURI = getUserURI(userLoginName);
			while(e.hasMoreElements()){
				WebdavResource role = (WebdavResource)e.nextElement();
				newRoles.remove(role.getDisplayName());
				updateRoleMembershipForUser(role,userURI,roleNamesForUser, loginNamesOfAllLoggedOnUsers);
			}
			
			//Add Roles that don't exist
			for (Iterator iter = newRoles.iterator(); iter.hasNext();) {
				String sRole = (String) iter.next();

				if(!service.getExistence(getRolePath(sRole))){
					WebdavResource newRole = new WebdavResource(service.getWebdavServerURL(rCredentials,getRolePath(sRole)), WebdavResource.NOACTION, 0);
					newRole.mkcolMethod();
					updateRoleMembershipForUser(newRole,userURI,roleNamesForUser, loginNamesOfAllLoggedOnUsers);
					newRole.close();
				}
			}			
		}
	}
	
	private void updateRoleMembershipForUser(WebdavResource role, String userURI, Set roleNamesForUser, Set userpathsOfAllLoggedOnUsers) throws HttpException, RemoteException, IOException{
//		System.out.println("[AuthenticationBusiness]: resouce "+role.getDisplayName()+" begins");
		boolean someChanges = false;
		try {
			Enumeration e = role.propfindMethod(GROUP_MEMBER_SET);
			String propertyString = "";
			while (e.hasMoreElements()) {
				propertyString += (String) e.nextElement();
			}
//			System.out.println("\t[group-member-set1]: "+propertyString);
			
			Set userSet = parseGroupMemberSetPropertyString(propertyString);
			
			if(userpathsOfAllLoggedOnUsers != null){
				String rootUser = getUserURI(SLIDE_DEFAULT_ROOT_USER);
				for (Iterator iter = userSet.iterator(); iter.hasNext();) {
					String token = (String) iter.next();
					if(!rootUser.equals(token) && !userpathsOfAllLoggedOnUsers.contains(token)){
						userSet.remove(token);
						someChanges=true;
					}
				}
			}
			
			boolean userIsInRole = userSet.contains(userURI);
			boolean userShouldBeInRole = SLIDE_ROLE_NAME_USER.equals(role.getDisplayName()) || roleNamesForUser.contains(role.getDisplayName());
			
			if(!userIsInRole && userShouldBeInRole){
				userSet.add(userURI);
				someChanges=true;
			} else if(userIsInRole && !userShouldBeInRole) {
				userSet.remove(userURI);
				someChanges=true;
			}
			
			if(someChanges){
				String newGroupMemberSet = encodeGroupMemberSetPropertyString(userSet);
				
				role.proppatchMethod(GROUP_MEMBER_SET_PROPERTY_NAME,newGroupMemberSet,true);
				
//				Enumeration e2 = role.propfindMethod(GROUP_MEMBER_SET);
//				if (e2.hasMoreElements()) {
//					String element2 = (String) e2.nextElement();
//					System.out.println("\t[group-member-set2]: "+element2);
//				}
			}
		}
		catch (HttpException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
//		System.out.println("[AuthenticationBusiness]: resouce "+role.getDisplayName()+" ends");
	}
	
	/**
	 * @param userSet Set of userpaths or grouppaths
	 * @see getUserURI(String)
	 * @see getGroupURI(String)
	 * @return
	 */
	private String encodeGroupMemberSetPropertyString(Set userOrGroupSet) {
		return PropertyParser.encodePropertyString(null,userOrGroupSet);
	}

	/**
	 * @param element
	 * @return
	 * @throws RemoteException
	 */
	private Set parseGroupMemberSetPropertyString(String propertyString) throws RemoteException {
		return PropertyParser.parsePropertyString(null,propertyString);
	}

	
	protected IWSlideService getSlideServiceInstance() throws IBOLookupException{
		if(slideService == null){
			slideService = (IWSlideService)IBOLookup.getServiceInstance(getIWApplicationContext(),IWSlideService.class);
		}
		return slideService;
	}
	
	public UsernamePasswordCredentials getRootUserCredentials(){
		if(NO_PASSWORD.equals(rootCredential.getPassword())){
			rootCredential.setPassword(StringHandler.getRandomString(20));
		}
		return rootCredential;
	}
	
	public boolean isRootUser(HttpServletRequest request){
		//HttpServletRequest request = iwc.getRequest();
		LoginBusinessBean loginBusiness = getLoginBusiness();
		String[] usernameAndPassword = loginBusiness.getLoginNameAndPasswordFromBasicAuthenticationRequest(request);
		UsernamePasswordCredentials tmpCredential = getRootUserCredentials();
		return tmpCredential.getUserName().equals(usernameAndPassword[0]) && tmpCredential.getPassword().equals(usernameAndPassword[1]);
	}
	
	protected LoginBusinessBean getLoginBusiness(){
		//return _loginBusiness;
		return LoginBusinessBean.getLoginBusinessBean(getIWApplicationContext());
	}
	
	
}
