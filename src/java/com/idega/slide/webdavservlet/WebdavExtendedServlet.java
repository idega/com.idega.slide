/*
 * $Id: WebdavExtendedServlet.java,v 1.6 2008/03/31 15:55:03 anton Exp $
 * Created on 31.5.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.webdavservlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.slide.webdav.WebdavServlet;

import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.ServletConfigWrapper;
import com.idega.servlet.ServletWrapper;
import com.idega.servlet.filter.IWBundleResourceFilter;


/**
 * <p>
 * TODO tryggvil Describe Type WebavExtendedServlet
 * </p>
 *  Last modified: $Date: 2008/03/31 15:55:03 $ by $Author: anton $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class WebdavExtendedServlet extends ServletWrapper {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8066220379268246523L;
	public static final String SLIDE_STORE_TYPE = "slide.store.type";
	public static final String TYPE_TXFILE = "txfile";
	public static final String TYPE_RDBMS = "rdbms";
	
	
	protected void initializeServletWrapper(ServletConfig config) {
		setServlet(new WebdavServlet());
	}
	
	
	public void init() throws ServletException{
		super.init();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.webdavservlet.ServletWrapper#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		ServletConfigWrapper newConfig = new ServletConfigWrapper(config);
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(config.getServletContext());
		
		String domainparam = newConfig.getInitParameter("domain");
		if(domainparam==null||domainparam.equals("autodetect")){

			domainparam = getDomainPath(newConfig);
			newConfig.setInitParameter("domain", domainparam);
			
			//Temporary workaround to copy config file to webapp

			IWBundleResourceFilter.copyResourceFromJarToWebapp(iwma, domainparam);			
			setDefaultConfig(newConfig);
		}
		//setServletConfig(newConfig);*/
		super.init(newConfig);
	}

	/**
	 * <p>
	 * Gets the path to the Domain.xml file that is used to initialize Slide
	 * </p>
	 * @return
	 */
	protected String getDomainPath(ServletConfig config) {
		String domainparam;
		String domainTxPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain-FileStore.xml";
		String domainRdbmsPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain.xml";
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(config.getServletContext());
		domainparam=domainTxPath;
		try {
			//First check if a written application property is set:
			String prop = iwma.getSettings().getProperty(SLIDE_STORE_TYPE);
			if(prop!=null){
				if(prop.equals(TYPE_TXFILE)){
					domainparam=domainTxPath;
				}
				else if(prop.equals(TYPE_RDBMS)){
					domainparam=domainRdbmsPath;
				}
			}
			else{
				domainparam=domainTxPath;
			}
			
			//Register the usage for future reference
			if(domainparam.equals(domainTxPath)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_TXFILE);
			}
			else if(domainparam.equals(domainRdbmsPath)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_RDBMS);
			}
			

			//THE DEFAULT WILL NOW BE TXFILE!!
			//Eiki
			//Secondly check the database if it supports slide:
//		    Connection conn = ConnectionBroker.getConnection();
//		    String datastoreType = SQLSchemaAdapter.detectDataStoreType(conn);
//		    ConnectionBroker.freeConnection(conn);
//		    SQLSchemaAdapter adapter = SQLSchemaAdapter.getInstance(datastoreType);
//		    if(adapter.getSupportsSlide()){
//		    	domainparam=domainRdbmsPath;
//		    }
		    
		    
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	
		return domainparam;
	}


	/**
	 * <p>
	 * Set default init-properties for Slide for idegaWeb if they are not set in web.xml
	 * </p>
	 * @param newConfig
	 */
	private void setDefaultConfig(ServletConfigWrapper newConfig) {
		
		newConfig.setInitParameterIfNotSet("namespace","slide");
		newConfig.setInitParameterIfNotSet("scope","");
		newConfig.setInitParameterIfNotSet("depth-limit","1000");
		newConfig.setInitParameterIfNotSet("default-mime-type","application/octet-stream");
		newConfig.setInitParameterIfNotSet("default-servlet","false");
		newConfig.setInitParameterIfNotSet("directory-browsing","false");
		newConfig.setInitParameterIfNotSet("directory-browsing-hide-acl","true");
		newConfig.setInitParameterIfNotSet("directory-browsing-hide-locks","true");
		newConfig.setInitParameterIfNotSet("optimizePropfindOutput","true");
		newConfig.setInitParameterIfNotSet("debug","0");
		newConfig.setInitParameterIfNotSet("extendedAllprop","false");
		newConfig.setInitParameterIfNotSet("lockdiscoveryIncludesPrincipalURL","false");
		newConfig.setInitParameterIfNotSet("updateLastModified","true");
	}
	
	
}
