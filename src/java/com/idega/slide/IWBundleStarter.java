/*
 * $Id: IWBundleStarter.java,v 1.2 2004/11/17 12:35:40 aron Exp $
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
import com.idega.slide.business.FileSystemCopyService;
import com.idega.slide.business.IWSlideService;

/**
 * 
 *  Last modified: $Date: 2004/11/17 12:35:40 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class IWBundleStarter implements IWBundleStartable {

    /* (non-Javadoc)
     * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
     */
    public void start(IWBundle starterBundle) {
        
        IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
        try {
            IWSlideService service = (IWSlideService) IBOLookup.getServiceInstance(iwac,IWSlideService.class);
            service.createSlideSchemas();
            
        } catch (IBOLookupException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
      
    	    try {
                ((FileSystemCopyService)IBOLookup.getServiceInstance(iwac,FileSystemCopyService.class)).run();
            } catch (IBOLookupException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    /* (non-Javadoc)
     * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
     */
    public void stop(IWBundle starterBundle) {
        

    }
}
