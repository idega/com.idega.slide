/*
 * $Id: IWSlideServiceBean.java,v 1.18 2005/02/14 14:55:14 gummi Exp $
 * Created on 23.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.properties.AclProperty;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.schema.SlideSchemaCreator;
import com.idega.slide.util.WebdavRootResource;
import com.idega.util.IWTimestamp;


/**
 * 
 *  Last modified: $Date: 2005/02/14 14:55:14 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.18 $
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
	
//	private static Credentials guestCredentials = new UsernamePasswordCredentials("guest","guest");
	
	public IWSlideServiceBean() {
		super();
	}
	
	public String getWebdavServerURI(){
		String appContext = getIWMainApplication().getApplicationContextURI();
		if (appContext.endsWith("/")){
			appContext = appContext.substring(0, appContext.lastIndexOf("/"));			
		}
		return appContext+WEBDAV_SERVLET_URI;
	}
	
	/**
	 * Gets the URL from with a path in the filesystem (e.g.) /files/content
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
	
	/**
	 * Gets the root url for the webdav server with authentication
	 * @return
	 */
	private HttpURL getWebdavServerURL(UsernamePasswordCredentials credential,String path,String servletPath){
	    
	    try {
	       String server = getIWApplicationContext().getDomain().getServerName();
	       if(server!=null){
	       		int port = 80;
		       if(server.endsWith("/"))
		           server = server.substring(0,server.lastIndexOf("/"));
		       if(server.startsWith("http://"))
		       		server = server.substring(7,server.length());
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
		       HttpURL hrl = new HttpURL(server,port,realPath);
		       
		       
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
	 */
	public UsernamePasswordCredentials getRootUserCredentials(){
//		if(lInfo!=null){
//			String password = (String)lInfo.getAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME);
//			if(password == null){
//				password = StringHandler.getRandomString(10);
//				lInfo.setAttribute(SLIDE_PASSWORD_ATTRIBUTE_NAME,password);
//			}
			return new UsernamePasswordCredentials("root","root");
//		}
		
//		return null;
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
	 * Returns the WebdavResource at the given path and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path) throws HttpException, IOException{
		return new WebdavResource(getWebdavServerURL(getRootUserCredentials(),path));
	}
	
	/**
	 * Returns the WebdavResource at path "/" and authenticated as root
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException{
		return getWebdavResourceAuthenticatedAsRoot(null);
	}
	
	public String getURI(String path) throws RemoteException{
		return getWebdavServerURI()+((path.startsWith(SLASH))?"":SLASH)+path;
	}
	
	public String getPath(String uri) throws RemoteException{
		String uriPrefix = getWebdavServerURI();
		return ((uri.startsWith(uriPrefix))?uri.substring(uriPrefix.length()):uri);
	}
	
	public boolean getExistence(String path) throws HttpException, IOException{
		if(path==null){
			return false;
		}
		String pathToCheck = ((path.startsWith(getWebdavServerURI()))?path:getURI(path));
//		System.out.println("[IWSlideServiceBean]: getExistence("+path+")->headerMethod("+ pathToCheck+")");
		return getWebdavResourceAuthenticatedAsRoot().headMethod(pathToCheck);
	}

	
	private void logOutAcesForUserFolders(String loginName) throws HttpException, IOException{
		WebdavResource user = getWebdavResourceAuthenticatedAsRoot();
		String userPath = getUserHomeFolderPath(loginName);
		AclProperty userFolderProperty = user.aclfindMethod(userPath);
		System.out.println("[IWSlideService#generateUserFolders("+user.getPath()+userPath+")]");
		if(userFolderProperty!=null){
			Ace[] userFolderProperties = userFolderProperty.getAces();
			
			for (int i = 0; i < userFolderProperties.length; i++) {
				Ace ace = userFolderProperties[i];
				System.out.print("\t"+i+":"+ace);
				Enumeration privileges = ace.enumeratePrivileges();
				while(privileges.hasMoreElements()){
					System.out.print("   "+((Privilege)privileges.nextElement()).getName());
				}
				System.out.println();
			}
		}
		
		AclProperty userDropboxProperty = user.aclfindMethod(user.getPath()+userPath+FOLDER_NAME_DROPBOX);
		System.out.println("[IWSlideService#generateUserFolders("+user.getPath()+userPath+FOLDER_NAME_DROPBOX+")]");
		if(userDropboxProperty!=null){
			Ace[] userDropboxProperties = userDropboxProperty.getAces();
			
			for (int i = 0; i < userDropboxProperties.length; i++) {
				Ace ace = userDropboxProperties[i];
				System.out.print("\t"+i+":"+ace);
				Enumeration privileges = ace.enumeratePrivileges();
				while(privileges.hasMoreElements()){
					System.out.print("   "+((Privilege)privileges.nextElement()).getName());
				}
				System.out.println();
			}
		}
		
		AclProperty userPublicFolderProperty = user.aclfindMethod(user.getPath()+userPath+FOLDER_NAME_PUBLIC);
		System.out.println("[IWSlideService#generateUserFolders("+user.getPath()+userPath+FOLDER_NAME_PUBLIC+")]");
		if(userPublicFolderProperty!=null){
			Ace[] userPublicFolderProperties = userPublicFolderProperty.getAces();
			
			for (int i = 0; i < userPublicFolderProperties.length; i++) {
				Ace ace = userPublicFolderProperties[i];
				System.out.print("\t"+i+":"+ace);
				Enumeration privileges = ace.enumeratePrivileges();
				while(privileges.hasMoreElements()){
					System.out.print("   "+((Privilege)privileges.nextElement()).getName());
				}
				System.out.println();
			}
		}
		
		user.close();
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
		
		return returner;
	}
	
	/**
	 * Creates all the folders in path 
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 */
	public void createAllFoldersInPath(String path) throws HttpException, RemoteException, IOException {
		IWUserContext iwuc = IWContext.getInstance();
		IWApplicationContext iwac = iwuc.getApplicationContext();
		
		IWSlideSession session = (IWSlideSession)IBOLookup.getSessionInstance(iwuc,IWSlideSession.class);
		IWSlideService service = (IWSlideService)IBOLookup.getServiceInstance(iwac,IWSlideService.class);
	
		WebdavRootResource rootResource = session.getWebdavRootResource();
		
		StringBuffer createPath = new StringBuffer(service.getWebdavServerURI());
		StringTokenizer st = new StringTokenizer(path,"/");
		while(st.hasMoreTokens()) {
			createPath.append("/").append(st.nextToken());
			System.out.println("Creaet path "+createPath);
			rootResource.mkcolMethod(createPath.toString());
		}
		
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
				name += "-0";
			} else {
				String counter = lastName.substring(minuteString.length());
				name += "-"+(Integer.parseInt(counter)+1);
			}
		}
		
		if(scope!=null){
			lastUniqueFileNameScopeMap.put(scope,name);
		}
		lastGlobalUniqueFileName = name;
		return name;
	}
	
}
