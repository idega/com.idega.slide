/*
 * $Id: DerbyRDBMSAdapter.java,v 1.2 2006/06/02 10:21:21 tryggvil Exp $
 * Created on 12.4.2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package org.apache.slide.store.impl.rdbms;

import org.apache.slide.common.Service;
import org.apache.slide.util.logger.Logger;


/**
 * <p>
 * This is the Apache Derby database adapter for Slide
 * </p>
 *  Last modified: $Date: 2006/06/02 10:21:21 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class DerbyRDBMSAdapter extends StandardRDBMSAdapter {

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DerbyRDBMSAdapter(Service arg0, Logger arg1) {
		super(arg0, arg1);
	}
}
