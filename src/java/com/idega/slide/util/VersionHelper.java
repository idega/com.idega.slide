package com.idega.slide.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.ResponseEntity;
import org.apache.webdav.lib.WebdavResource;

/**
 * @author gimmi
 */
public class VersionHelper {
	
	public static final String PROPERTY_PREDECESSOR_SET = "predecessor-set";
	public static final String PROPERTY_SUCCESSOR_SET   = "successor-set";
	public static final String PROPERTY_VERSION_NAME    = "version-name";
	public static final String PROPERTY_CREATOR_DISPLAYNAME = "creator-displayname";
	public static final String PROPERTY_VERSION_COMMENT = "comment";
	
	public static String getLatestVersion(WebdavResource resource) {
		try {
			Vector properties = new Vector();
			properties.add(PROPERTY_VERSION_NAME);
			
			Enumeration rsEnum = resource.reportMethod(resource.getHttpURL(), properties);

			while (rsEnum.hasMoreElements()) {
				ResponseEntity entity = (ResponseEntity) rsEnum.nextElement();
				Enumeration props = entity.getProperties();
				while (props.hasMoreElements()) {
					BaseProperty property = (BaseProperty) props.nextElement(); 
					// First (and only) property is always version name (properties Vector) 
					return property.getPropertyAsString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param resource
	 * @return An Enumeration of ResponseEntity objects...
	 */
	public static Enumeration getAllVersions(WebdavResource resource) {
		try {
			Vector p = new Vector();
			p.add(PROPERTY_VERSION_NAME);
			p.add(PROPERTY_CREATOR_DISPLAYNAME);
			p.add(PROPERTY_VERSION_COMMENT);

			return resource.reportMethod(resource.getHttpURL(), p);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
