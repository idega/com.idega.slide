/*
 * $Id: WebdavExtendedServlet.java,v 1.8 2009/01/07 11:41:27 tryggvil Exp $
 * Created on 31.5.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.webdavservlet;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.slide.webdav.WebdavServlet;

import com.idega.servlet.ServletConfigWrapper;
import com.idega.servlet.ServletWrapper;
import com.idega.util.expression.ELUtil;


/**
 * <p>
 * TODO tryggvil Describe Type WebavExtendedServlet
 * </p>
 *  Last modified: $Date: 2009/01/07 11:41:27 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class WebdavExtendedServlet extends ServletWrapper {
	
	private static final long serialVersionUID = 8066220379268246523L;
	
	private static ReentrantLock LOCK = new ReentrantLock();
	
	@Override
	protected void initializeServletWrapper(ServletConfig config) {
		setServlet(new WebdavServlet());
	}
	
	@Override
	public void init() throws ServletException{
		super.init();
	}

	/* (non-Javadoc)
	 * @see com.idega.slide.webdavservlet.ServletWrapper#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletConfig newConfig=config;
		
		String domainparam = config.getInitParameter("domain");
		if (domainparam == null || domainparam.equals("autodetect")) {
			ServletConfigWrapper wrapperConfig = new ServletConfigWrapper(config.getServletContext(),config.getServletName());
			DomainConfig domainConfig = ELUtil.getInstance().getBean(DomainConfig.SPRING_BEAN_IDENTIFIER);
			domainConfig.setServletConfig(newConfig);
			domainConfig.initialize();
			
			setDefaultConfig(wrapperConfig);
			newConfig=wrapperConfig;
		}
		else {
			ServletConfigWrapper wrapperConfig = new ServletConfigWrapper(config);
			setDefaultConfig(wrapperConfig);
			newConfig=wrapperConfig;
		}
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
	
	public static synchronized boolean isLocked() {
		return LOCK.isLocked();
	}
	
	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		LOCK.lock();
		try {
			super.service(request, response);
		} finally {
			try {
				LOCK.unlock();
			} catch (IllegalMonitorStateException e) {}
		}
	}
}