/*
 * $Id: AuthenticationBusinessBean.java,v 1.2 2004/12/14 17:24:11 gummi Exp $
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
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.slide.business.IWSlideService;


/**
 * 
 *  Last modified: $Date: 2004/12/14 17:24:11 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public class AuthenticationBusinessBean extends IBOServiceBean  implements AuthenticationBusiness{
	
	private static String PATH_USERS = "/users";
	private static String PATH_GROUPS = "/groups";
	private static String PATH_ROLES = "/roles";
	private static String SLASH = "/";
	private IWSlideService slideService = null;
	private static final String GROUP_MEMBER_SET = "group-member-set";
	private static final PropertyName GROUP_MEMBER_SET_PROPERTY_NAME = new PropertyName("DAV:",GROUP_MEMBER_SET);
	
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
		if(userLoginName!=null && userLoginName.length()>0 && !userLoginName.equals("root") ){
			IWSlideService service = getSlideServiceInstance();
			UsernamePasswordCredentials rCredentials = service.getRootUserCredentials();
			
			WebdavResource user;
//			try {
				//'WebdavResource.NOACTION, 0' is a fix to get around http 404 exception, caused by propfind method execution, if resource does not extist.
				user = new WebdavResource(service.getWebdavServerURL(rCredentials,getUserPath(userLoginName)), WebdavResource.NOACTION, 0);
				if(!user.exists()){
					user.mkcolMethod();
					user.close();
				}
//			}
//			catch (HttpException e1) {
//				if(e1.getReasonCode() == 404){
//					user = new WebdavResource(service.getWebdavServerURL(rCredentials,getUserPath("")));
//					user.putMethod(getUserPath(userLoginName),"");
//					user.mkcolMethod(userLoginName);
//					user.close();
//				} else {
//					throw e1;
//				}
//			}
			
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
				//'WebdavResource.NOACTION, 0' is a fix to get around http 404 exception, caused by propfind method execution, if resource does not extist.				
				WebdavResource newRole = new WebdavResource(service.getWebdavServerURL(rCredentials,getRolePath(sRole)), WebdavResource.NOACTION, 0);
				if(!newRole.exists()){
					newRole.mkcolMethod();
					newRole.close();
				}
//				newRole.proppatchMethod(GROUP_MEMBER_SET_PROPERTY_NAME,newGroupMemberSet,true);
				updateRoleMembershipForUser(newRole,userURI,roleNamesForUser, loginNamesOfAllLoggedOnUsers);
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
				String rootUser = getUserURI("root");
				for (Iterator iter = userSet.iterator(); iter.hasNext();) {
					String token = (String) iter.next();
					if(!rootUser.equals(token) && !userpathsOfAllLoggedOnUsers.contains(token)){
						userSet.remove(token);
						someChanges=true;
					}
				}
			}
			
			boolean userIsInRole = userSet.contains(userURI);
			boolean userShouldBeInRole = "users".equals(role.getDisplayName()) || roleNamesForUser.contains(role.getDisplayName());
			
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
		String newGroupMemberSet = "";
		for (Iterator iter = userOrGroupSet.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			newGroupMemberSet += "<D:href xmlns:D=\"DAV:\">"+path+"</D:href>";
		}
//		System.out.println("\t[newGroupMemberSet]: "+newGroupMemberSet);
		return newGroupMemberSet;
	}

	/**
	 * @param element
	 * @return
	 * @throws RemoteException
	 */
	private Set parseGroupMemberSetPropertyString(String propertyString) throws RemoteException {
		String[] tokens = propertyString.split("<D:href xmlns:D=\"DAV:\">");
		for (int i = 0; i < tokens.length; i++) {
			int closeTagIndex = tokens[i].indexOf("</D:href>");
			if(closeTagIndex >-1){
//				System.out.println("\t"+tokens[i]);
				tokens[i] = tokens[i].substring(0,closeTagIndex);
//				System.out.println("\t"+tokens[i]);
			}
		}
		Set userSet = new LinkedHashSet();
		for (int i = 0; i < tokens.length; i++) {
			if(tokens[i].indexOf("/")!=-1){
				userSet.add(tokens[i]);
			}
		}
		return userSet;
	}

	
	protected IWSlideService getSlideServiceInstance() throws IBOLookupException{
		if(slideService == null){
			slideService = (IWSlideService)IBOLookup.getServiceInstance(getIWApplicationContext(),IWSlideService.class);
		}
		return slideService;
	}
	
	
}
