/*
 * $Id: IWSlideServiceHome.java,v 1.13 2006/08/30 16:54:01 valdas Exp $
 * Created on May 24, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface IWSlideServiceHome extends IBOHome {
	public IWSlideService create() throws CreateException, RemoteException;
}