package com.idega.slide.business;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.slide.security.Security;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.slide.authentication.AuthenticationBusiness;
import com.idega.slide.util.AccessControlList;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.slide.util.WebdavRootResource;

public interface IWSlideService extends IBOService, IWSlideChangeListener {
	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURI
	 */
	public String getWebdavServerURI() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavServerURL
	 */
	public HttpURL getWebdavServerURL(UsernamePasswordCredentials credential,
			String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials,
			String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile(UsernamePasswordCredentials credentials)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavFile
	 */
	public WebdavFile getWebdavFile() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getRootUserCredentials
	 */
	public UsernamePasswordCredentials getRootUserCredentials()
			throws IBOLookupException, RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createSlideSchemas
	 */
	public void createSlideSchemas() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavRootResource
	 */
	public WebdavResource getWebdavExternalRootResource(UsernamePasswordCredentials credentials) throws HttpException, IOException, RemoteException,
		RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResource
	 */
	public WebdavResource getWebdavResource(String path,
			UsernamePasswordCredentials credentials) throws HttpException,
			IOException, RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavExtendedResource
	 */
	public WebdavExtendedResource getWebdavExtendedResource(String path,
			UsernamePasswordCredentials credentials) throws HttpException,
			IOException, RemoteException, RemoteException;
	
	public WebdavExtendedResource getWebdavExtendedResource(String path,
			UsernamePasswordCredentials credentials, boolean localResource) throws HttpException,
			IOException, RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResourceAuthenticatedAsRoot
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot(String path)
			throws HttpException, IOException, RemoteException;

	/**
	 * Returns LOCAL resource!
	 * @see com.idega.slide.business.IWSlideServiceBean#getWebdavResourceAuthenticatedAsRoot
	 */
	public WebdavResource getWebdavResourceAuthenticatedAsRoot() throws HttpException, IOException, RemoteException;

	public WebdavResource getWebdavExternalResourceAuthenticatedAsRoot() throws HttpException, IOException;
	
	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getURI
	 */
	public String getURI(String path) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getPath
	 */
	public String getPath(String uri) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getExistence
	 */
	public boolean getExistence(String path) throws HttpException, IOException,
			RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#generateUserFolders
	 */
	public boolean generateUserFolders(String loginName) throws HttpException,
			IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#updateUserFolderPrivileges
	 */
	public void updateUserFolderPrivileges(String loginName)
			throws IOException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path)
			throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAccessControlList
	 */
	public AccessControlList getAccessControlList(String path,
			WebdavRootResource rResource) throws HttpException, IOException,
			RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl)
			throws HttpException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#storeAccessControlList
	 */
	public boolean storeAccessControlList(AccessControlList acl,
			WebdavRootResource rResource) throws HttpException, IOException,
			RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getAuthenticationBusiness
	 */
	public AuthenticationBusiness getAuthenticationBusiness()
			throws IBOLookupException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getUserHomeFolderPath
	 */
	public String getUserHomeFolderPath(String loginName)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createUniqueFileName
	 */
	public String createUniqueFileName(String scope);

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getSecurityHelper
	 */
	public Security getSecurityHelper() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createAllFoldersInPath
	 */
	public boolean createAllFoldersInPath(String path,
			UsernamePasswordCredentials credentials) throws HttpException,
			RemoteException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#createAllFoldersInPathAsRoot
	 */
	public boolean createAllFoldersInPathAsRoot(String path)
			throws HttpException, RemoteException, IOException, RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString,
			String contentType) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString,
			String contentType, boolean deletePredecessor)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, InputStream fileInputStream,
			String contentType, boolean deletePredecessor)
			throws RemoteException;
	
	public boolean uploadFile(String uploadPath, String fileName, String contentType, InputStream fileInputStream);

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadXMLFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadXMLFileAndCreateFoldersFromStringAsRoot
	 */
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(
			String parentPath, String fileName, String fileContentString,
			boolean deletePredecessor) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getIWSlideChangeListeners
	 */
	public IWSlideChangeListener[] getIWSlideChangeListeners()
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setIWSlideChangeListeners
	 */
	public void setIWSlideChangeListeners(List<IWSlideChangeListener> iwSlideChangeListeners) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#addIWSlideChangeListeners
	 */
	public void addIWSlideChangeListeners(
			IWSlideChangeListener iwSlideChangeListener) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildCountExcludingFoldersAndHiddenFiles
	 */
	public int getChildCountExcludingFoldersAndHiddenFiles(String folderURI)
			throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderCount
	 */
	public int getChildFolderCount(String folderURI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildCount
	 */
	public int getChildCount(String folderURI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#isHiddenFile
	 */
	public boolean isHiddenFile(String fileName) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsExcludingFoldersAndHiddenFiles
	 */
	public List<String> getChildPathsExcludingFoldersAndHiddenFiles(String folderURI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderPaths
	 */
	public List<String> getChildFolderPaths(String folderURI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPaths
	 */
	public List<String> getChildPaths(String folderURI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#invalidateCacheForAllFoldersInURIPath
	 */
	public void invalidateCacheForAllFoldersInURIPath(String URI) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildFolderPathsCacheMap
	 */
	public Map<String, List<String>> getChildFolderPathsCacheMap() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildFolderPathsCacheMap
	 */
	public void setChildFolderPathsCacheMap(Map<String, List<String>> childFolderPathsCacheMap) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsCacheMap
	 */
	public Map<String, List<String>> getChildPathsCacheMap() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildPathsCacheMap
	 */
	public void setChildPathsCacheMap(Map<String, List<String>> childPathsCacheMap) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getChildPathsExcludingFolderAndHiddenFilesCacheMap
	 */
	public Map<String, List<String>> getChildPathsExcludingFolderAndHiddenFilesCacheMap() throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#setChildPathsExcludingFolderAndHiddenFilesCacheMap
	 */
	public void setChildPathsExcludingFolderAndHiddenFilesCacheMap(Map<String, List<String>> childPathsExcludingFolderAndHiddenFilesCacheMap)
		throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#onSlideChange
	 */
	public void onSlideChange(IWContentEvent contentEvent);

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getParentPath
	 */
	public String getParentPath(WebdavResource resource) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getParentPath
	 */
	public String getParentPath(String path) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#uploadZipFileContents
	 */
	public boolean uploadZipFileContents(ZipInputStream zipInputStream, String uploadPath) throws RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getInputStream
	 */
	public InputStream getInputStream(String path) throws IOException,
			RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getOutputStream
	 */
	public OutputStream getOutputStream(File file) throws IOException,
			RemoteException;

	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getOutputStream
	 */
	public OutputStream getOutputStream(String path) throws IOException,
			RemoteException;
	
	/**
	 * @see com.idega.slide.business.IWSlideServiceBean#getFile
	 */
	public File getFile(String path)throws URIException, RemoteException;
	
	public boolean deleteAsRootUser(String path) throws RemoteException;
	
	public boolean delete(String path, UsernamePasswordCredentials credentials) throws RemoteException;
}