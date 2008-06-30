package com.idega.slide.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.file.util.FileInfo;
import com.idega.core.file.util.FileURIHandler;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.util.WebdavExtendedResource;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/30 13:33:52 $ by $Author: civilis $
 *
 */
@Service
@Scope("singleton")
public class SlideFileURIHandler implements FileURIHandler {

	private static final String SCHEME = "slide";
	
	public String getSupportedScheme() {
		return SCHEME;
	}

	public InputStream getFile(URI uri) throws FileNotFoundException {

		try {
			final String resourcePath = uri.getPath();
			final WebdavExtendedResource resource = getWebdavExtendedResource(resourcePath);

			if(!resource.exists())
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

	public FileInfo getFileInfo(URI uri) {
		
		try {
			
			final String resourcePath = uri.getPath();
			final WebdavExtendedResource resource = getWebdavExtendedResource(resourcePath);
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