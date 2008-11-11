package com.idega.slide.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.slide.util.WebdavExtendedResource;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0 
 *
 * Last modified: Oct 15, 2008 by Author: Anton 
 *
 */

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWSlideResourceBundle extends IWResourceBundle implements MessageResource {
	
	private final Logger logger;
	private String bundleIdentifier;
	private Level usagePriorityLevel = MessageResourceImportanceLevel.FIRST_ORDER;
	private boolean autoInsert = true;
	
	private static final String LOCALISATION_PATH = "/files/public/localisation/";
	private static final String NON_BUNDLE_LOCALISATION_FILE_NAME = "Localizable_no_bundle";
	private static final String BUNDLE_LOCALISATION_FILE_NAME = "Localizable";
	private static final String NON_BUNDLE_LOCALISATION_FILE_EXTENSION = ".strings";
	private static final String LOCALISATION_MIME_TYPE = "text/plain";
	
	public static final String RESOURCE_IDENTIFIER = "slide_resource";

	public IWSlideResourceBundle() throws IOException {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@SuppressWarnings("unchecked")
	protected void initialize(Locale locale) throws IOException {
		setLocale(locale);
		
		InputStream slideSourceStream = getResourseInputStream(getLocalisableFilePath());
		
		Properties localisationProps = new Properties();
		localisationProps.load(slideSourceStream);

		setLookup(new TreeMap(localisationProps));
	}

	protected InputStream getResourseInputStream(String resourcePath) {
		try {
			WebdavExtendedResource resource = getWebdavExtendedResource(resourcePath);
		
			InputStream is = resource.getMethodData();
			return is;
			
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void storeState() {

		Properties props = getProperties();
		props.clear();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		if (getLookup() != null) {
			Iterator iter = getLookup().keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				if (key != null) {
					Object value = getLookup().get(key);
					if (value != null) {
						props.put(key, value);
					}
				}
			}

			try {
				props.store(byteStream, "");
			} catch (IOException e) {
				logger.log(Level.WARNING, "Can't store properties to ByteArrayOutputStream", e);
			}
		}
		
		try {
			WebdavExtendedResource resource = getWebdavExtendedResource(getLocalisableFilePath());
			resource.putMethod(byteStream.toByteArray());

		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "Can't save localisation file to repository", e);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Can't save localisation file to repository");
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getLocalizedString(String key, String returnValueIfNotFound) {
		Object returnObj = getLookup().get(key);
		if(returnObj != null) {
			return String.valueOf(returnObj);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setString(String key, String value) {
		getLookup().put(key, value);
	}
	
	/**
	 * @return <code>true</code> - if the value presents in slide bundle. <code>false</code> - in other case
	 * 
	 */
	@Override
	protected boolean checkBundleLocalizedString(String key, String value) {
		if(StringUtil.isEmpty( (String)handleGetObject(key)) ) {
			return false;
		} else {
			return true;
		}
	}
	
	private String getLocalisableFilePath() {
		return getLocalisableFolderPath() + getLocalisableFileName();
	}
	
	private String getLocalisableFolderPath() {
		StringBuffer filePath = new StringBuffer(LOCALISATION_PATH);
		
		if(!StringUtil.isEmpty(getBundleIdentifier()) && !MessageResource.NO_BUNDLE.equals(getBundleIdentifier())) {
			filePath.append(getBundleIdentifier()).append(CoreConstants.SLASH);
		}
		
		return filePath.toString();
	}
	
	private String getLocalisableFileName() {
		StringBuffer fileName = new StringBuffer();
		
		if(StringUtil.isEmpty(getBundleIdentifier()) || MessageResource.NO_BUNDLE.equals(getBundleIdentifier())) {
			fileName.append(NON_BUNDLE_LOCALISATION_FILE_NAME)
					.append(CoreConstants.UNDER).append(getLocale())
					.append(NON_BUNDLE_LOCALISATION_FILE_EXTENSION);
		} else {
			fileName.append(BUNDLE_LOCALISATION_FILE_NAME)
					.append(CoreConstants.UNDER).append(getLocale())
					.append(NON_BUNDLE_LOCALISATION_FILE_EXTENSION);
		}
		
		return fileName.toString();
	}
	
	private IWSlideService getIWSlideService() throws IBOLookupException {
		try {
			return (IWSlideService) IBOLookup.getServiceInstance(getIWApplicationContext(), IWSlideService.class);
		} catch (IBOLookupException e) {
			logger.log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}

	private IWApplicationContext getIWApplicationContext() {
		IWApplicationContext iwac = IWMainApplication
				.getDefaultIWApplicationContext();
		return iwac;
	}

	private WebdavExtendedResource getWebdavExtendedResource(String path) throws IOException, RemoteException, IBOLookupException {
		IWSlideService service = getIWSlideService();

		if(!service.getExistence(path)) {
			service.uploadFileAndCreateFoldersFromStringAsRoot(getLocalisableFolderPath(), getLocalisableFileName(), CoreConstants.EMPTY, LOCALISATION_MIME_TYPE, false);
		}
		
		WebdavExtendedResource resource = service.getWebdavExtendedResource(path, service.getRootUserCredentials());
		return resource;
	}

	public String getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	@Override
	public Level getLevel() {
		return this.usagePriorityLevel;
	}

	/**
	 * @param key - message key
	 * @param autoInsertValue - value to be inserted in case if nothing is found and autoInsert is enabled
	 * @param bundleIdentifier - bundle in which messages should be located
	 * @return object that was found in resource or set to it, null - if there are no values with specified key
	 */
	@Override
	public Object getMessage(Object key, Object autoInsertValue, String bundleIdentifier) {
		setBundleIdentifier(bundleIdentifier);
		
		IWContext iwc = IWContext.getCurrentInstance();
		try {
			initialize(iwc.getCurrentLocale());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with current locale: " + iwc.getCurrentLocale(), e);
		}

		String returnStr =  getLocalizedString(String.valueOf(key), String.valueOf(autoInsertValue));
		if(returnStr == null && isAutoInsert()) {
			return setMessage(key, autoInsertValue, bundleIdentifier);
		} else 
			return returnStr;
	}
	
	/**
	 * @param key - message key
	 * @param autoInsertValue - value to be inserted in case if nothing is found and autoInsert is enabled
	 * @param bundleIdentifier - bundle in which messages should be located
	 * @return object that was found in resource or set to it, null - if there are no values with specified key
	 */
	@Override
	public Object getMessage(Object key, Object valueIfNotFound, String bundleIdentifier, Locale locale) {
		setBundleIdentifier(bundleIdentifier);
		
		IWContext iwc = IWContext.getCurrentInstance();
		try {
			initialize(locale);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with current locale: " + iwc.getCurrentLocale(), e);
		}

		String returnStr =  getLocalizedString(String.valueOf(key), String.valueOf(valueIfNotFound));
		if(returnStr == null && isAutoInsert()) {
			return setMessage(key, valueIfNotFound, bundleIdentifier);
		} else 
			return returnStr;
	}

	@Override
	public void setLevel(Level priorityLevel) {
		usagePriorityLevel = priorityLevel;
	}


	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	@Override
	public Object setMessage(Object key, Object value, String bundleIdentifier) {
		setBundleIdentifier(bundleIdentifier);
		
		IWContext iwc = IWContext.getCurrentInstance();
		try {
			if(getLocale() == null) {
				initialize(iwc.getCurrentLocale());
			} else {
				initialize(getLocale());
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with locale: " + iwc.getCurrentLocale(), e);
			return null;
		}
		
		setString(String.valueOf(key), String.valueOf(value));
		this.storeState();
		return value;
	}
	

	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	@Override
	public Object setMessage(Object key, Object value, String bundleIdentifier, Locale locale) {
		setLocale(locale);
		return setMessage(key, value, bundleIdentifier);
	}
	
	@Override
	public void setMessages(Map<Object, Object> values, String bundleIdentifier, Locale locale) {
		setBundleIdentifier(bundleIdentifier);
		setLocale(locale);
		
		IWContext iwc = IWContext.getCurrentInstance();
		try {
			if(getLocale() == null) {
				initialize(iwc.getCurrentLocale());
			} else {
				initialize(getLocale());
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with locale: " + locale, e);
		}
		
		for(Object key : values.keySet()) {
			setString(String.valueOf(key), String.valueOf(values.get(key)));
		}
		
		this.storeState();
	}
	
	@Override
	public Set<Object> getAllLocalisedKeys(String bundleIdentifier, Locale locale) {
		if(bundleIdentifier.equals(NO_BUNDLE))
			bundleIdentifier = null;
		setBundleIdentifier(bundleIdentifier);
		setLocale(locale);
		
		try {
			initialize(getLocale());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with locale: " + locale, e);
		}
		return getLookup().keySet();
	}
	
	@Override
	public void removeMessage(Object key, String bundleIdentifier, Locale locale) {
		if(bundleIdentifier.equals(NO_BUNDLE))
			bundleIdentifier = null;
		setBundleIdentifier(bundleIdentifier);
		setLocale(locale);
		
		try {
			initialize(getLocale());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception initialising resource with locale: " + locale, e);
		}
		getLookup().remove(key);
		this.storeState();
	}

	@Override
	public boolean isAutoInsert() {
		return autoInsert;
	}

	@Override
	public void setAutoInsert(boolean autoInsert) {
		this.autoInsert = autoInsert;
	}
	
	@Override
	public String getIdentifier() {
		return RESOURCE_IDENTIFIER;
	}
}
