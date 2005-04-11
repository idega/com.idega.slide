package com.idega.slide.util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.ResponseEntity;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.properties.CheckedinProperty;
import org.apache.webdav.lib.properties.CheckedoutProperty;
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;

/**
 * A helper class to perform version control operations on WebdavResources
 * 
 * @author <a href="mailto:gimmi@idega.is>Grimur Jonsson </a>, <a
 *         href="mailto:eiki@idega.is>Eirikur S. Hrafnsson </a>
 */
public class VersionHelper {

	public static final String PROPERTY_DISPLAY_NAME = "displayname";
	public static final String PROPERTY_PREDECESSOR_SET = "predecessor-set";
	public static final String PROPERTY_SUCCESSOR_SET = "successor-set";
	public static final String PROPERTY_VERSION_NAME = "version-name";
	public static final String PROPERTY_VERSION_TREE = "version-tree";
	public static final String PROPERTY_LATEST_ACTIVITY_VERSION = "latest-activity-version";
	public static final String PROPERTY_CREATOR_DISPLAY_NAME = "creator-displayname";
	public static final String PROPERTY_CHECKED_OUT_SET = "checkedout-set";
	public static final String PROPERTY_CHECKED_OUT = CheckedoutProperty.TAG_NAME;
	public static final String PROPERTY_CHECKED_IN = CheckedinProperty.TAG_NAME;
	public static final String PROPERTY_LOCK_DISCOVERY = "lockdiscovery";
	public static final String PROPERTY_RESOURCE_TYPE = "resourcetype";
	public static final String PROPERTY_CONTENT_TYPE = WebdavResource.GETCONTENTTYPE;
	public static final String PROPERTY_CONTENT_LENGTH = WebdavResource.GETCONTENTLENGTH;
	public static final String PROPERTY_LAST_MODIFIED = WebdavResource.GETLASTMODIFIED;
	public static final String PROPERTY_CREATION_DATE = WebdavResource.CREATIONDATE;
	public static final String PROPERTY_COMMENT = "comment";
	public static final int DEFAULT_LOCK_TIMEOUT = 86400;
	public static final String CHECKED_OUT_PREFIX = "Checked-out by : ";
	
	/**
	 * An expensive method, you should rather create a WebdavExtendedResource and use its getVersionName method
	 * @param resource
	 * @return
	 */
	public static String getLatestVersion(WebdavResource resource) {
		List list = getAllVersions(resource);
		if (!list.isEmpty()) {
			return list.get(0).toString();
		}	else {
			return null;
		}
	}
	
	public static String getLatestVersion(String resourcePath) throws HttpException, RemoteException, IOException {
		List list = getAllVersions(resourcePath);
		if (!list.isEmpty()) {
			return list.get(0).toString();
		}	else {
			return null;
		}
	}
	
	
	public static List getAllVersions(String resourcePath) throws HttpException, RemoteException, IOException {
		IWContext iwc = IWContext.getInstance();
		IWSlideService service = (IWSlideService)IBOLookup.getServiceInstance(iwc,IWSlideService.class);
		return getAllVersions(service.getWebdavResourceAuthenticatedAsRoot(resourcePath));
	}
	
	/**
	 * 
	 * @param resource
	 * @return A list of WebdavResourceVersion
	 */
	public static List getAllVersions(WebdavResource resource) {
		List versions = new ArrayList();
		try {
			Vector p = new Vector();
			Map propMap = new HashMap();
			p.add(PROPERTY_VERSION_NAME);
			p.add(PROPERTY_CREATOR_DISPLAY_NAME);
			p.add(PROPERTY_COMMENT);
			p.add(PROPERTY_CHECKED_OUT);
			p.add(PROPERTY_CHECKED_IN);
			p.add(PROPERTY_LAST_MODIFIED);
			p.add(PROPERTY_CREATION_DATE);
			//p.add(PROPERTY_SUCCESSOR_SET);
			Enumeration props = resource.reportMethod(resource.getHttpURL(), p);
			while (props.hasMoreElements()) {
				ResponseEntity responseEntity = (ResponseEntity) props.nextElement();
				
				for (Enumeration e = responseEntity.getProperties(); e.hasMoreElements();) {
					BaseProperty property = (BaseProperty) e.nextElement();
					String propertyName = property.getLocalName();
					propMap.put(propertyName, property);
				}
				versions.add(new WebdavResourceVersion(propMap));
			}
		}
		catch (HttpException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Collections.sort(versions);
		Collections.reverse(versions);
		
		return versions;
	}

	public static boolean lock(WebdavResource resource) {
		boolean success = false;
		if (resource == null) {
			return false;
		}
		try {
			//	            String owner = lockOwner == null
			//	                    ? DEFAULT_OWNER
			//	                    : lockOwner;
			//use resource.lockMethod(DEFAULT_LOCK_TIMEOUT); ?
			success = resource.lockMethod(resource.getOwner(), DEFAULT_LOCK_TIMEOUT);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean unlock(WebdavResource resource) {
		boolean success = false;
		if (resource == null) {
			return false;
		}
		try {
			//	            String owner = lockOwner == null
			//	                    ? DEFAULT_OWNER
			//	                    : lockOwner;
			success = resource.unlockMethod(resource.getPath(), resource.getOwner());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean checkOut(WebdavResource resource, String performer) {
		boolean success = false;
		if (resource == null) {
			return false;
		}
		try {
			success = resource.checkoutMethod();
			resource.proppatchMethod(new PropertyName("DAV:", "comment"), CHECKED_OUT_PREFIX + performer,	true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	public static boolean hasUserCheckedOutResource(WebdavExtendedResource resource, String userName) {
		if (userName != null && !"".equalsIgnoreCase(userName)) {
			return userName.equals(getCheckedOutName(resource));
		}
		return false;
	}
	
	/**
	 * 
	 * @return Returns the name of the user that checked out the resource
	 */
	public static String getCheckedOutName(WebdavExtendedResource resource) {
		return resource.getComment().replaceFirst(CHECKED_OUT_PREFIX, "");
	}

	public static boolean unCheckOut(WebdavResource resource) {
		boolean success = false;
		if (resource == null) {
			return false;
		}
		try {
			success = resource.uncheckoutMethod();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean checkIn(WebdavResource resource) {
		boolean success = false;
		if (resource == null) {
			return false;
		}
		try {
			success = resource.checkinMethod();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean delete(WebdavResource resource) {
		boolean success = false;
		try {
			//comment from SwingDaver:
			// Okay, check this out. I spent like forever trying to
			// get files such as SomJavaFile$1.class to delete, however
			// after what seems like centuries, I finally reach the
			// conclusion that the escaped path doesn't cut it and symbols
			// such as $ and : need to be decoded/unescaped back to their
			// original format before they will delete successfully.
			String resPath = getDecodedPath(resource);
			success = resource.deleteMethod(resPath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static String getDecodedPath(WebdavResource resource) throws IOException {
		return URIUtil.decode(resource.getPath());
	}
}