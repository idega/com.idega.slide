package com.idega.slide.business;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;

import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.security.NodePermission;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.WebdavResources;

import com.idega.user.data.User;

/**
* Proxy for Simple Slide API's methods. Check {@link IWSimpleSlideServiceImp} for implementation.
* 
* @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
* @version $Revision: 1.1 $
*
* Last modified: $Date: 2009/05/08 08:08:46 $ by: $Author: valdas $
*/
public interface IWSimpleSlideService extends Serializable {

	public boolean checkExistance(String pathToFile) throws Exception;
	
	public InputStream getInputStream(String pathToFile);
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user) throws Exception;
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user, boolean closeStream) throws Exception;
	
	public boolean setContent(String pathToFile, InputStream contentStream);
	
	public Enumeration<NodePermission> getPermissions(String path);
	
	public boolean setPermissions(String path, Ace[] aces);
	
	public boolean createStructure(String path);
	
	public boolean delete(String path);
	
	public NodeRevisionDescriptor getRevisionDescriptor(String path);
	
	public WebdavResources getResources(String path);
	
	public void deletetDefinitionFile(String path);
}