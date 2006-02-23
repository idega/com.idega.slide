/*
 * $Id: IWSlideServiceBean.java,v 1.34 2006/02/23 18:40:30 eiki Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.security.Security;
import org.apache.slide.webdav.WebdavServlet;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.properties.AclProperty;
import org.apache.webdav.lib.util.WebdavStatus;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.schema.SlideSchemaCreator;
import com.idega.slide.util.AccessControlEntry;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.IWSlideConstants;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;
import com.idega.util.IWTimestamp;


/**
 * <p>
 * This is the main bean for accessing system wide information about the slide store.
 * </p>
 * 
 *  Last modified: $Date: 2006/02/23 18:40:30 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>,<a href="mailto:tryggvi@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.34 $
 */
public class IWSlideServiceBean extends IBOServiceBean  implements IWSlideService {

	protected static final String SLASH = "/";
	
	protected static final String WEBDAV_SERVLET_URI = "/content";
	protected static final String FILE_SERVER_URI = WEBDAV_SERVLET_URI+"/files";
	protected static final String USER_SERVLET_URI = WEBDAV_SERVLET_URI+"/users";
	
	protected static final String PATH_FILES_ROOT = "/files";
	protected static final String PATH_BLOCK_HOME = PATH_FILES_ROOT+"/cms";
	protected static final String PATH_USERS_HOME_FOLDERS = PATH_FILES_ROOT+"/users";
	protected static final String PATH_GROUPS_HOME_FOLDERS = PATH_FILES_ROOT+"/groups";
	
	protected static final String FOLDER_NAME_PUBLIC = "/public";
	protected static final String FOLDER_NAME_SHARED = "/shared";
	protected static final String FOLDER_NAME_DROPBOX = "/dropbox";
	
	protected Map lastUniqueFileNameScopeMap = new HashMap();
	protected String lastGlobalUniqueFileName = null;
	
	private Security security = null;
		
	public IWSlideServiceBean() {
		super();
	}
	
	/**
	 * <p>
	 * Gets the URI for the root of the slide repository.
	 * The repository is by default mapped on '/content' under the web application.<br/>
	 * This method returns the context path for the application so if it is e.g. mapped under '/cms' this method returns '/cms/content'.
	 * If the webapplication is mapped on '/' the method returns '/content'
	 * </p>
	 * @param path
	 * @return
	 */
	public String getWebdavServerURI(){
		String appContext = getIWMainApplication().getApplicationContextURI();
		if (appContext.endsWith("/")){
			appContext = appContext.substring(0, appContext.lastIndexOf("/"));			
		}
		return appContext+WEBDAV_SERVLET_URI;
	}
	
	/**
	 * <p>
	 * Gets the URL from with a path in the filesystem (e.g.) if the given path is '/files/public/myfile.pdf' then this
	 * method returns 'http://[hostname]:[port]/[contextpath]/content/files/public/myfile.pdf'
	 * </p>
	 * @param path
	 * @return
	 */
	public HttpURL getWebdavServerURL(String path){
		return getWebdavServerURL(null,path,getWebdavServerURI());
	}
	
	public HttpURL getWebdavServerURL(){
		return getWebdavServerURL(null,null,getWebdavServerURI());
	}
	
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential){
		return getWebdavServerURL(credential,null,getWebdavServerURI());
	}
	
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential, String path){
		return getWebdavServerURL(credential,path,getWebdavServerURI());
	}
	
	/**df öh
	 * Gets the root url for the webdav server with authentication
	 * @return
	 */
	private HttpURL getWebdavServerURL(UsernamePasswordCredentials credential,String path,String servletPath){
	    
	    try {
	       //String server = getIWApplicationContext().getDomain().getServerName();
	       String server = getIWApplicationContext().getDomain().getURL();
	    		if(server!=null){
	       		int port = 80;
	       		boolean https = false;
		       if(server.endsWith("/"))
		           server = server.substring(0,server.lastIndexOf("/"));
		       if(server.startsWith("http://"))
		       		server = server.substring(7,server.length());
		       if(server.startsWith("https://")) {
		    	   https = true;
		       		server = server.substring(8,server.length());
		       }
		       if(server.indexOf(":")!=-1){
		       		String sPort = server.substring(server.indexOf(":")+1,server.length());
		       		port = Integer.parseInt(sPort);
		       		server = server.substring(0,server.indexOf(":"));
		       }

		       String rootPath = servletPath;
		       String realPath = rootPath;
		       if(path!=null){
		       		realPath = rootPath+path;
		       }
		       
		       //server += getWebdavServletURL();
		       HttpURL hrl = null;
		       if (https) {
		    	   hrl = new HttpsURL(server,port,realPath);
		       } else {
		    	   hrl = new HttpURL(server,port,realPath);
		       }
		       
		       
			   if(credential!=null){
			       hrl.setUserinfo(credential.getUserName(),credential.getPassword());
			    }
	            return hrl;
	       }
	       return null;
        } catch (URIException e) {
           throw new IBORuntimeException(e);
        }
	}
	
	
	/**
	 * Gets resource for the webdav server with authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials, String path){
	    try {
            return new WebdavFile(getWebdavServerURL(credentials,path));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
	
	/**
	 * Gets the root resource for the webdav server with authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials){
	   return getWebdavFile(credentials,null);
	}
	
	/**
	 * Gets the root resource for the webdav server without any authentication
	 * @return
	 */
	public WebdavFile getWebdavFile(){
	    return getWebdavFile(null,null);
	}
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws IBOLookupException
	 */
	public UsernamePasswordCredentials getRootUserCredentials() throws IBOLookupException, RemoteException{
		return getAuthenticationBusiness().getRootUserCredentials();
	}
	
	
	/**
	 * Auto creates the Slide sql schema structure
	 */
	public void createSlideSchemas(){
	    try {
			new SlideSchemaCreator().createSchemas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * Returns the WebdavResource for the "/" or root of the WebDav server.
	 * </p>
	 * @param credentials
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws RemoteException
	 */
	public WebdavResource getWebdavRootResource(UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException {
		return getWebdavExtendedResource(null,credentials);
		
	}
	
	public WebdavResource getWebdavResource(String path,UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException {
		return getWebdavExtendedResource(path,credentials);
		
	}
	
	public WebdavExtendedResource getWebdavExtendedResource(String path,UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException {
		WebdavExtendedResource resource;
//		if(getUserContext().isLoggedOn()){
			resource = new WebdavExtendedResource(getWebdavServerURL(credentials,getPath(path)));
//		} else {
//			resource = new WebdavExtendedResource(getIWSlideService().getWebdavServerURL(path));
//		}
		return resource;
	}
	
	/**
	 * Returns the WebdavResource at the given path and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path) throws HttpException, IOException{
		//String thePath = (path==null)?null:getPath(path);
		//return new WebdavResource(getWebdavServerURL(getRootUserCredentials(),thePath));
		String thePath=path;
		return getWebdavResource(thePath,getRootUserCredentials());
	}
	
	/**
	 * Returns the WebdavResource at path "/" and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException{
		return getWebdavResourceAuthenticatedAsRoot(null);
	}
	
	/**
	 * <p>
	 * Gets the URI from with a path in the filesystem (e.g.) if the given path is '/files/public/myfile.pdf' then this
	 * method returns '/[contextpath]/content/files/public/myfile.pdf'
	 * </p>
	 * @param path
	 * @return
	 */
	public String getURI(String path) throws RemoteException{
		return getWebdavServerURI()+((path.startsWith(SLASH))?"":SLASH)+path;
	}
	
	public String getPath(String uri) throws RemoteException{
		String uriPrefix = getWebdavServerURI();
		if(uri==null){
			return null;
		}
		else{
			return ((uri.startsWith(uriPrefix))?uri.substring(uriPrefix.length()):uri);
		}
	}
	
	public boolean getExistence(String path) throws HttpException, IOException{
		if(path==null){
			return false;
		}
		try {
			String pathToCheck = ((path.startsWith(getWebdavServerURI()))?path:getURI(path));
	//		System.out.println("[IWSlideServiceBean]: getExistence("+path+")->headerMethod("+ pathToCheck+")");
			Enumeration prop = getWebdavResourceAuthenticatedAsRoot().propfindMethod(pathToCheck, WebdavResource.DISPLAYNAME);
			return !(prop == null || !prop.hasMoreElements());
		}
		catch (HttpException e) {
			if(e.getReasonCode()==WebdavStatus.SC_NOT_FOUND){
				return false;
			} else {
				throw e;
			}
		}
			
//		return getWebdavResourceAuthenticatedAsRoot().headMethod(pathToCheck);
	}

	
	public boolean generateUserFolders(String loginName) throws HttpException, IOException{
		boolean returner = false;

		if(loginName != null && !getExistence(getUserHomeFolderPath(loginName))){
			WebdavResource rootFolder = getWebdavResourceAuthenticatedAsRoot();
			
			String userFolderPath = getURI(getUserHomeFolderPath(loginName));
			rootFolder.mkcolMethod(userFolderPath);
			rootFolder.mkcolMethod(userFolderPath+FOLDER_NAME_DROPBOX);
			rootFolder.mkcolMethod(userFolderPath+FOLDER_NAME_PUBLIC);
			
//			try {
//				logOutAcesForUserFolders(loginName);
//			}
//			catch (HttpException e1) {
//				e1.printStackTrace();
//			}
//			catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			try {
//				AuthenticationBusiness aBusiness = getAuthenticationBusiness();
//				
//				
//				AclProperty userFolderProperty = rootFolder.aclfindMethod(userFolderPath);
//				Ace[] userFolderProperties = (userFolderProperty==null)?new Ace[0]:userFolderProperty.getAces();
//				
//				int homeLength = userFolderProperties.length;
//				Ace[] homeFolderAce = new Ace[homeLength+1];
//				System.arraycopy(userFolderProperties,0,homeFolderAce,0,homeLength);
//				
//				Ace userAllPrivilege =  new Ace(aBusiness.getUserURI(loginName));
//				userAllPrivilege.addPrivilege(Privilege.ALL);
//				homeFolderAce[homeLength] = userAllPrivilege;
//				rootFolder.aclMethod(userFolderPath,homeFolderAce);
//				
//				
//				
//				AclProperty userDropboxProperty = rootFolder.aclfindMethod(userFolderPath+FOLDER_NAME_DROPBOX);
//				Ace[] userDropboxProperties = (userDropboxProperty==null)?new Ace[0]:userDropboxProperty.getAces();
//				
//				int dropboxLength = userDropboxProperties.length;
//				Ace[] dropboxAce = new Ace[dropboxLength+1];
//				System.arraycopy(userDropboxProperties,0,dropboxAce,0,dropboxLength);
//				
//				dropboxAce[dropboxLength] = new Ace(aBusiness.getRoleURI(IWSlideConstants.ROLENAME_USERS));
//				dropboxAce[dropboxLength].addPrivilege(Privilege.WRITE);
//				rootFolder.aclMethod(userFolderPath+FOLDER_NAME_DROPBOX,dropboxAce);
//				
//				
//				AclProperty userPublicFolderProperty = rootFolder.aclfindMethod(userFolderPath+FOLDER_NAME_PUBLIC);
//				Ace[] userPublicFolderProperties = (userPublicFolderProperty==null)?new Ace[0]:userPublicFolderProperty.getAces();
//				
//				int publicLength = userPublicFolderProperties.length;
//				Ace[] publicAce = new Ace[publicLength+1];
//				System.arraycopy(userPublicFolderProperties,0,publicAce,0,publicLength);
//				
//				publicAce[publicLength] = new Ace(IWSlideConstants.SUBJECT_URI_ALL);
//				publicAce[publicLength].addPrivilege(Privilege.READ);
//				publicAce[publicLength].setInherited(true);
//				rootFolder.aclMethod(userFolderPath+FOLDER_NAME_PUBLIC,publicAce);
//			}
//			catch (IBOLookupException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (HttpException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//			try {
//				logOutAcesForUserFolders(loginName);
//			}
//			catch (HttpException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
////			if(transactionStarted){
////				returner = rootFolder.commitTransaction();
////			}
			rootFolder.close();
		} 
//		else {
//			logOutAcesForUserFolders(loginName);
//		}
		
		try {
			updateUserFolderPrivileges(loginName);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return returner;
	}
	
	public void updateUserFolderPrivileges(String loginName) throws IOException, IOException{
		
		String userFolderPath = getURI(getUserHomeFolderPath(loginName));
		
		AuthenticationBusiness aBusiness = getAuthenticationBusiness();
		String userPrincipal = aBusiness.getUserURI(loginName);
		
		// user folder
		AccessControlList userFolderList = getAccessControlList(userFolderPath);
		// should be 'all' for the user himself
		List userFolderUserACEs = userFolderList.getAccessControlEntriesForUsers();
		AccessControlEntry usersPositiveAce = null;
		AccessControlEntry usersNegativeAce = null;
		boolean madeChangesToUserFolderList = false;
		//Find the ace
		for (Iterator iter = userFolderUserACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = (AccessControlEntry) iter.next();
			if(ace.getPrincipal().equals(userPrincipal) && !ace.isInherited()){
				if(ace.isNegative()){
					usersNegativeAce = ace;
				} else {
					usersPositiveAce = ace;
				}
			}
		}
		if(usersPositiveAce == null){
			usersPositiveAce = new AccessControlEntry(userPrincipal,false,false,false,null,AccessControlEntry.PRINCIPAL_TYPE_USER);
			userFolderList.add(usersPositiveAce);
		}
		
		if(!usersPositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_ALL)){
			if(usersNegativeAce != null && usersNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_ALL)){
				// do nothing becuse this is not ment to reset permissions but to set them in the first
				// first place and update for legacy reasons.  If Administrator has closed someones user folder
				// for some reason, this is not supposed to reset that.
			} else {
				usersPositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_ALL);
				madeChangesToUserFolderList = true;
				
				// temporary at least:
				usersPositiveAce.setInherited(false);
				usersPositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if(madeChangesToUserFolderList){
			storeAccessControlList(userFolderList);
		}
		
		// dropbox
		updateUsersDropboxPrivileges(userFolderPath);
		
		
		//public folder
		updateUsersPublicFolderPrivileges(userFolderPath);
		
		
	}
	
	/**
	 * @param userFolderPath
	 * @throws HttpException
	 * @throws IOException
	 */
	private void updateUsersDropboxPrivileges(String userFolderPath) throws HttpException, IOException {
		//dropbox
		AccessControlList dropboxList = getAccessControlList(userFolderPath+FOLDER_NAME_DROPBOX);
		// should be 'write' for authenticated
		
		List publicFolderStandardACEs = dropboxList.getAccessControlEntriesForUsers();
		String principalAuthenticated = IWSlideConstants.SUBJECT_URI_AUTHENTICATED;
		AccessControlEntry prAuthenticatedPositiveAce = null;
		AccessControlEntry prAuthenticatedNegativeAce = null;
		boolean madeChangesToPublicFolderList = false;
		//Find the ace
		for (Iterator iter = publicFolderStandardACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = (AccessControlEntry) iter.next();
			if(ace.getPrincipal().equals(principalAuthenticated) && !ace.isInherited()){
				if(ace.isNegative()){
					prAuthenticatedNegativeAce = ace;
				} else {
					prAuthenticatedPositiveAce = ace;
				}
			}
		}
		if(prAuthenticatedPositiveAce == null){
			prAuthenticatedPositiveAce = new AccessControlEntry(principalAuthenticated,false,false,false,null,AccessControlEntry.PRINCIPAL_TYPE_STANDARD);
			dropboxList.add(prAuthenticatedPositiveAce);
		}
		
		if(!prAuthenticatedPositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_WRITE)){
			if(prAuthenticatedNegativeAce != null && prAuthenticatedNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_WRITE)){
				// do nothing becuse this is not ment to reset permissions but to set them in the first
				// first place and update for legacy reasons.
			} else {
				prAuthenticatedPositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_WRITE);
				madeChangesToPublicFolderList = true;
				
				// temporary at least:
				prAuthenticatedPositiveAce.setInherited(false);
				prAuthenticatedPositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if(madeChangesToPublicFolderList){
			storeAccessControlList(dropboxList);
		}
	}
	
	/**
	 * @param userFolderPath
	 * @throws HttpException
	 * @throws IOException
	 */
	private void updateUsersPublicFolderPrivileges(String userFolderPath) throws HttpException, IOException {
		//public folder
		AccessControlList publicFolderList = getAccessControlList(userFolderPath+FOLDER_NAME_PUBLIC);
		// should be 'read' for everyone (and preferably nothing set for 'write')
		
		List publicFolderStandardACEs = publicFolderList.getAccessControlEntriesForUsers();
		String principalEveryone = IWSlideConstants.SUBJECT_URI_ALL;
		AccessControlEntry prEveryonePositiveAce = null;
		AccessControlEntry prEveryoneNegativeAce = null;
		boolean madeChangesToPublicFolderList = false;
		//Find the ace
		for (Iterator iter = publicFolderStandardACEs.iterator(); iter.hasNext();) {
			AccessControlEntry ace = (AccessControlEntry) iter.next();
			if(ace.getPrincipal().equals(principalEveryone) && !ace.isInherited()){
				if(ace.isNegative()){
					prEveryoneNegativeAce = ace;
				} else {
					prEveryonePositiveAce = ace;
				}
			}
		}
		if(prEveryonePositiveAce == null){
			prEveryonePositiveAce = new AccessControlEntry(principalEveryone,false,false,false,null,AccessControlEntry.PRINCIPAL_TYPE_STANDARD);
			publicFolderList.add(prEveryonePositiveAce);
		}
		
		if(!prEveryonePositiveAce.containsPrivilege(IWSlideConstants.PRIVILEGE_READ)){
			if(prEveryoneNegativeAce != null && prEveryoneNegativeAce.containsPrivilege(IWSlideConstants.PRIVILEGE_READ)){
				// do nothing becuse this is not ment to reset permissions but to set them in the first
				// first place and update for legacy reasons.
			} else {
				prEveryonePositiveAce.addPrivilege(IWSlideConstants.PRIVILEGE_READ);
				madeChangesToPublicFolderList = true;
				
				// temporary at least:
				prEveryonePositiveAce.setInherited(false);
				prEveryonePositiveAce.setInheritedFrom(null);
				// temporary ends
			}
		}
		if(madeChangesToPublicFolderList){
			storeAccessControlList(publicFolderList);
		}
	}

	public AccessControlList getAccessControlList(String path) throws HttpException, IOException{
		WebdavResource rResource = getWebdavResourceAuthenticatedAsRoot();
		return getAccessControlList(path, new WebdavRootResource(rResource));
	}
	
	/**
	 * @param path
	 * @param rResource
	 * @return
	 * @throws RemoteException
	 * @throws HttpException
	 * @throws IOException
	 */
	public AccessControlList getAccessControlList(String path, WebdavRootResource rResource) throws HttpException, IOException {
		String thePath = null;
		if(path!=null){
			thePath = getPath(path);
		}
		AccessControlList acl = new AccessControlList(getWebdavServerURI(),thePath);
		
		AclProperty aclProperty = null;
		if(thePath!=null){ // && !"/".equals(path) && !"".equals(path)){
			aclProperty = rResource.aclfindMethod(getURI(thePath));
		} else {
			aclProperty = rResource.aclfindMethod();
		}
		if(aclProperty!=null){
			Ace[] aclProperties = aclProperty.getAces();
			if(aclProperties != null){
				acl.setAces(aclProperties);
			}
		}
		return acl;
	}

	public boolean storeAccessControlList(AccessControlList acl) throws HttpException, IOException{
		WebdavResource rResource = getWebdavResourceAuthenticatedAsRoot();
		return storeAccessControlList(acl, new WebdavRootResource(rResource));
	}
	
	/**
	 * @param acl
	 * @param rResource
	 * @return
	 * @throws RemoteException
	 * @throws HttpException
	 * @throws IOException
	 */
	public boolean storeAccessControlList(AccessControlList acl, WebdavRootResource rResource) throws HttpException, IOException {
		String resourceURI = getURI(acl.getResourcePath());
		Ace[] aces = acl.getAces();
//		System.out.println("Saving for resource: "+resourceURI);
//		for(int i = 0; i < aces.length; i++) {
//			System.out.print("Saving:"+aces[i]);
//			Enumeration e = aces[i].enumeratePrivileges();
//			while (e.hasMoreElements()) {
//				Privilege p = (Privilege) e.nextElement();
//				System.out.print(", "+p.getName());
//			}
//			System.out.println();
//		}
		
		boolean value = rResource.aclMethod(resourceURI,aces);
//		System.out.println("Success: "+value);
//		if (!value){
//			//try
//			String path = getPath(resourceURI);
//			System.out.println("Try path: "+path);
//			rResource.aclMethod(path,aces);
//			System.out.println("Success: "+value);
//		}
//		System.out.println("Done - ------------------");
		return value;
	}

	/**
	 * @return
	 * @throws IBOLookupException
	 */
	public AuthenticationBusiness getAuthenticationBusiness() throws IBOLookupException {
		return (AuthenticationBusiness) getServiceInstance(AuthenticationBusiness.class);
	}

	/**
	 * @param loginName
	 * @return
	 */
	public String getUserHomeFolderPath(String loginName) {
		return PATH_USERS_HOME_FOLDERS+SLASH+loginName;
	}
	
	/**
	* @param scope This parameter can be null and then the file name will be unique over the whole web.  If one needs unique name within a module or a folder one can set some (unique) string as a scope parameter
	**/
	public synchronized String createUniqueFileName(String scope){
		IWTimestamp timestamp = new IWTimestamp();
		String minuteString = "yyyyMMdd-HHmm";
		String name = timestamp.getDateString(minuteString);
		String lastName = null;
		if(scope != null && !"".equals(scope)){
			lastName = (String)lastUniqueFileNameScopeMap.get(scope);
		} else {
			lastName = lastGlobalUniqueFileName;
		}
		
		if(!(lastName==null || !lastName.startsWith(name))){
			if(lastName.length()==minuteString.length()){
				name += "-1";
			} else {
				String counter = lastName.substring(minuteString.length()+1);
				name += "-"+(Integer.parseInt(counter)+1);
			}
		}
		
		if(scope!=null){
			lastUniqueFileNameScopeMap.put(scope,name);
		}
		lastGlobalUniqueFileName = name;
		return name;
	}
	
	
	public Security getSecurityHelper(){
		if(security == null){
			NamespaceAccessToken token = (NamespaceAccessToken)getIWApplicationContext().getApplicationAttribute(WebdavServlet.ATTRIBUTE_NAME);
			security = token.getSecurityHelper();
		}
		return security;
	}
	
	
	
	/**
	 * Creates all the folders in path 
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPath(String path,UsernamePasswordCredentials credentials) throws HttpException, RemoteException, IOException {
		boolean hadToCreate = false;
		WebdavResource rootResource = getWebdavRootResource(credentials);
		
		hadToCreate = !getExistence(path);
		if(hadToCreate){
			StringBuffer createPath = new StringBuffer(getWebdavServerURI());
			StringTokenizer st = new StringTokenizer(path,"/");
			while(st.hasMoreTokens()) {
				createPath.append("/").append(st.nextToken());
				rootResource.mkcolMethod(createPath.toString());
			}
		}
		return hadToCreate;
		
	}

	
	/**
	 * Creates all the folders in path with credentatials of the root/administrator user.
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPathAsRoot(String path) throws HttpException, RemoteException, IOException {
		return createAllFoldersInPath(path,getRootUserCredentials());
	}
	
	/**
	 * Creates the parent folder if needed and uploads the content of the string as a utf8 encoded file of the contenttype/mimetype you specify
	 * 
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString, String contentType){
		boolean returnValue = false;
		try {
			createAllFoldersInPathAsRoot(parentPath);
			
			String filePath = parentPath+fileName;
			WebdavResource rootResource = getWebdavResourceAuthenticatedAsRoot();
			ByteArrayInputStream utf8stream = new ByteArrayInputStream(fileContentString.getBytes("UTF-8"));
			
			//Conflict fix: uri for creating but path for updating
			//Note! This is a patch to what seems to be a bug in WebDav
			//Apparently in verion below works in some cases and the other in other cases.
			//Seems to be connected to creating files in folders created in same tomcat session or similar
			//not quite clear...
			
			if(rootResource.putMethod(filePath,utf8stream)){
				if(contentType!=null){
					rootResource.proppatchMethod(filePath,WebdavResource.GETCONTENTTYPE,contentType,true);
				}
			}
			else{
				utf8stream = new ByteArrayInputStream(fileContentString.getBytes("UTF-8"));
				String fixedURL = getURI(filePath);
				rootResource.putMethod(fixedURL,utf8stream);
				if(contentType!=null){
					rootResource.proppatchMethod(fixedURL,WebdavResource.GETCONTENTTYPE,contentType,true);
				}
			}
			
			rootResource.close();
			
			
			log(rootResource.getStatusMessage());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
		
		return returnValue;
	}
	
	/**
	 * Uploads the supplied string as a file with the content type "text/xml"
	 * @param parentPath
	 * @param fileName
	 * @param fileContentString
	 * @param contentType
	 * @return
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString){
		return uploadFileAndCreateFoldersFromStringAsRoot(parentPath, fileName, fileContentString,"text/xml");
	}
	
	
	
}
