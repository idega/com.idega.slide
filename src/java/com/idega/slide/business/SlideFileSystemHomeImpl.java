/*
 * $Id: SlideFileSystemHomeImpl.java,v 1.2 2004/12/15 16:14:53 palli Exp $
 * Created on Dec 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/12/15 16:14:53 $ by $Author: palli $
 * 
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.2 $
 */
public class SlideFileSystemHomeImpl extends IBOHomeImpl implements SlideFileSystemHome {

	protected Class getBeanInterfaceClass() {
		return SlideFileSystem.class;
	}

	public SlideFileSystem create() throws javax.ejb.CreateException {
		return (SlideFileSystem) super.createIBO();
	}
}
