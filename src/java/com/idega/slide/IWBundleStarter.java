/*
 * $Id: IWBundleStarter.java,v 1.13 2008/07/08 17:04:15 eiki Exp $
 * Created on 15.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.event.IWSlideChangeEventClient;
import com.idega.slide.util.DirtyUnloader;

/**
 * 
 *  Last modified: $Date: 2008/07/08 17:04:15 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.13 $
 */
public class IWBundleStarter implements IWBundleStartable {

    /* (non-Javadoc)
     * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
     */
    public void start(IWBundle starterBundle) {
    	
//    		System.out.println("[System.property]:java.security.auth.login.config="+starterBundle.getPropertiesRealPath()+ FileUtil.getFileSeparator()+"jaas.config");
//		System.setProperty("java.security.auth.login.config",starterBundle.getPropertiesRealPath()+ FileUtil.getFileSeparator()+"jaas.config");
	//	System.setProperty("file.encoding",CoreConstants.ENCODING_UTF8);
        DirtyUnloader unloader = new DirtyUnloader();
        unloader.reset();
    	
        IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
        try {
            IWSlideService service = (IWSlideService) IBOLookup.getServiceInstance(iwac,IWSlideService.class);
            //TODO don't create unless using rdbms
            //DISABLED CURRENTLY WE DON'T SUPPORT RDBMS because of serious performance problems. (e.g. changing one property results in 1000's of requests)
            //service.createSlideSchemas();
            
            //see com.idega.core.event
            IWSlideChangeEventClient client = new IWSlideChangeEventClient();
            
            //add it as a slide change listener for caching purposes
            service.addIWSlideChangeListeners(service);
            //add the event client, new core event system (for clustering and more)
            service.addIWSlideChangeListeners(client);
   
        } catch (IBOLookupException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
       

    }
    /* (non-Javadoc)
     * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
     */
    public void stop(IWBundle starterBundle) {
    	DirtyUnloader unloader = new DirtyUnloader();
    	unloader.unload();
    }
}
