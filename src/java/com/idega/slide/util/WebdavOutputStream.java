/*
 * $Id: WebdavOutputStream.java,v 1.1 2006/05/29 18:23:18 tryggvil Exp $
 * Created on 24.5.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.io.IOException;
import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResource;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;


/**
 * <p>
 * TODO tryggvil Describe Type WebdavOutputStream
 * </p>
 *  Last modified: $Date: 2006/05/29 18:23:18 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class WebdavOutputStream extends MemoryOutputStream {

	private WebdavResource webdavResource;
	boolean committed=false;
	
	/**
	 * @param buffer
	 */
	public WebdavOutputStream(WebdavResource resource) {
		super(new MemoryFileBuffer());
		setWebdavResource(resource);
		// TODO Auto-generated constructor stub
	}

	public void commit() throws IOException {
		if(!committed){
			getWebdavResource().putMethod(new MemoryInputStream(getBuffer()));
			committed=true;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.io.MemoryOutputStream#close()
	 */
	public void close() throws IOException {
		commit();
		getWebdavResource().close();
		// TODO Auto-generated method stub
		super.close();
	}

	
	/**
	 * @return the webdavResource
	 */
	public WebdavResource getWebdavResource() {
		return webdavResource;
	}

	
	/**
	 * @param webdavResource the webdavResource to set
	 */
	public void setWebdavResource(WebdavResource webdavResource) {
		this.webdavResource = webdavResource;
	}
}
