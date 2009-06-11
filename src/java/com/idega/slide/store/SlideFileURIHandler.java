package com.idega.slide.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResources;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.file.util.FileInfo;
import com.idega.core.file.util.FileURIHandler;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.slide.SlideConstants;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2009/06/11 12:37:27 $ by $Author: valdas $
 *
 */
@Service
@Scope("singleton")
public class SlideFileURIHandler implements FileURIHandler {
	
	public String getSupportedScheme() {
		return SlideConstants.SLIDE_SCHEME;
	}

	public InputStream getFile(URI uri) throws FileNotFoundException {

		try {
			final WebdavExtendedResource resource = getWebdavExtendedResource(uri);

			if (resource == null || !resource.exists())
				throw new IllegalArgumentException("Expected webdav resource wasn't found by uri provided="+uri);
			
			InputStream is = resource.getMethodData();
			return is;
			
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private WebdavExtendedResource getWebdavExtendedResource(String path) throws HttpException, IOException, RemoteException, IBOLookupException {
		IWSlideService service = getIWSlideService();
		return service.getWebdavExtendedResource(path, service.getRootUserCredentials());
	}
	
	private WebdavExtendedResource getWebdavExtendedResource(URI uri) throws HttpException, IOException, RemoteException, IBOLookupException {
		WebdavExtendedResource resource = getWebdavExtendedResource(getRealPath(uri, true));
		if (!resource.exists()) {
			return getWebdavExtendedResource(getRealPath(uri, false));
		}
		
		WebdavResources children = resource.getChildResources();
		if (children != null && !children.isEmpty()) {
			return getWebdavExtendedResource(getRealPath(uri, false));
		}
		
		return resource;
	}
	
	private IWSlideService getIWSlideService() throws IBOLookupException {
		
		try {
			return (IWSlideService) IBOLookup.getServiceInstance(getIWApplicationContext(), IWSlideService.class);
		} catch (IBOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}
	
	private synchronized IWApplicationContext getIWApplicationContext() {
		
		IWApplicationContext iwac;
		IWContext iwc = IWContext.getCurrentInstance();
		
		if(iwc != null)
			iwac = iwc;
		else
			iwac = IWMainApplication.getDefaultIWApplicationContext();
		
	    return iwac;
	}

	private String getRealPath(URI uri, boolean decode) throws UnsupportedEncodingException {
		String resourcePath = uri.getPath();
		if (resourcePath == null) {
			resourcePath = uri.toString();
		}
		
		String scheme = SlideConstants.SLIDE_SCHEME + CoreConstants.COLON;
		if (resourcePath.startsWith(scheme)) {
			resourcePath = resourcePath.replaceFirst(scheme, CoreConstants.EMPTY);
		}
		
		if (resourcePath != null) {
			return decode ? getDecodedPath(resourcePath) : resourcePath;
		}
		
		resourcePath = uri.toString();
		return decode ? getDecodedPath(resourcePath) : resourcePath;
	}
	
	private String getDecodedPath(String resourcePath) throws UnsupportedEncodingException {
		return URLDecoder.decode(resourcePath, CoreConstants.ENCODING_UTF8);
	}
	
	public FileInfo getFileInfo(URI uri) {
		
		try {
			final WebdavExtendedResource resource = getWebdavExtendedResource(uri);
			final String fileName = resource.getDisplayName();
			final Long contentLength = resource.getGetContentLength();
				
			final FileInfo fi = new FileInfo();
			fi.setFileName(fileName);
			fi.setContentLength(contentLength);
			
			return fi;
			
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}