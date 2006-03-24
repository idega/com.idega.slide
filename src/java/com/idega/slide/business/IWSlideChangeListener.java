/*
 * $Id: IWSlideChangeListener.java,v 1.1 2006/03/24 16:44:09 eiki Exp $
 * Created on Mar 24, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import org.apache.slide.event.ContentEvent;


public interface IWSlideChangeListener {	
	public void onSlideChange(ContentEvent contentEvent);	
}
