/*
 * $Id: SlideFile.java,v 1.1 2004/11/29 16:13:51 aron Exp $
 * Created on 29.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.data;

import com.idega.core.file.data.ICFile;
import com.idega.data.IDOEntity;

/**
 * 
 *  Last modified: $Date: 2004/11/29 16:13:51 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface SlideFile extends IDOEntity, ICFile {
    /**
     * @see com.idega.slide.data.SlideFileBMPBean#getExternalURL
     */
    public String getExternalURL();

    /**
     * @see com.idega.slide.data.SlideFileBMPBean#setExternalURL
     */
    public void setExternalURL(String url);

}
