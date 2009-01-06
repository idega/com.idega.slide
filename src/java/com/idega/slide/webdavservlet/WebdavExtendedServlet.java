/*
 * $Id: WebdavExtendedServlet.java,v 1.7 2009/01/06 15:17:20 tryggvil Exp $
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
import com.idega.util.expression.ELUtil;


/**
 * <p>
 * TODO tryggvil Describe Type WebavExtendedServlet
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */
public class WebdavExtendedServlet extends ServletWrapper {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8066220379268246523L;

	
	
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

			//domainparam = getDomainPath(newConfig);
			/*
			DomainConfig domainConfig = new DomainConfig(newConfig);
			String domainConfigPath = domainConfig.getConfigPath();
			domainparam=domainConfigPath;
			//newConfig.setInitParameter("domain", domainparam);
			
			*/
			DomainConfig domainConfig = ELUtil.getInstance().getBean(DomainConfig.SPRING_BEAN_IDENTIFIER);
			domainConfig.setServletConfig(newConfig);
			domainConfig.initialize();
			
			setDefaultConfig(newConfig);
		}
		//setServletConfig(newConfig);*/
		super.init(newConfig);
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
