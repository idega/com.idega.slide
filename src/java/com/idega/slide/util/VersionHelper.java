package com.idega.slide.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;

import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideSession;

/**
 * @author gimmi
 */
public class VersionHelper {
	
	public static String getVersion(WebdavResource resource) {
		try {
			WebdavResources rs = getAllVersions(resource);
			Enumeration rsEnum = rs.getResources();

			while (rsEnum.hasMoreElements()) {
				WebdavResource enumR = (WebdavResource) rsEnum.nextElement();
				return enumR.getName();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static WebdavResources getAllVersions(WebdavResource resource) {
		try {
			IWSlideSession ss = (IWSlideSession) IBOLookup.getSessionInstance(IWContext.getInstance(), IWSlideSession.class);
			String webDavServerURI = ss.getWebdavServerURI();
			
			Enumeration enumer = resource.propfindMethod("version-history");
			while (enumer.hasMoreElements()) {
				Object el = enumer.nextElement();
				Set childResourcePath = PropertyParser.parsePropertyString(null, el.toString(), true);
				Iterator iter = childResourcePath.iterator();
				while (iter.hasNext()) {
					String element = (String) iter.next();
					element = element.replaceFirst(webDavServerURI, "");
					WebdavResource r = ss.getWebdavResource(element);
					return r.getChildResources();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebdavResources();
	}
	
}
