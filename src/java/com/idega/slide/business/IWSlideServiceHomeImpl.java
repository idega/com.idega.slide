/*
 * $Id: IWSlideServiceHomeImpl.java,v 1.4 2004/11/15 14:07:06 aron Exp $
 * Created on 15.11.2004
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
 *  Last modified: $Date: 2004/11/15 14:07:06 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.4 $
 */
public class IWSlideServiceHomeImpl extends IBOHomeImpl implements
        IWSlideServiceHome {
    protected Class getBeanInterfaceClass() {
        return IWSlideService.class;
    }

    public IWSlideService create() throws javax.ejb.CreateException {
        return (IWSlideService) super.createIBO();
    }

}
