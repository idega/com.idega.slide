/*
 * $Id: SlideFileBMPBean.java,v 1.2 2005/03/08 14:46:52 laddi Exp $
 * Created on 29.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.data;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileBMPBean;

/**
 * 
 *  Last modified: $Date: 2005/03/08 14:46:52 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class SlideFileBMPBean extends ICFileBMPBean implements ICFile , SlideFile{
    
    private static final String EXTERNALURL = "EXT_URL" ;

    /* (non-Javadoc)
     * @see com.idega.data.GenericEntity#initializeAttributes()
     */
    public void initializeAttributes() {
        // TODO Auto-generated method stub
        super.initializeAttributes();
        addAttribute(EXTERNALURL,"",true,true,String.class,1000);
    }
    
    public String getExternalURL(){
	    return getStringColumnValue(EXTERNALURL);
	}
	
	public void setExternalURL(String url){
	    setColumn(EXTERNALURL,url);
	}

}
