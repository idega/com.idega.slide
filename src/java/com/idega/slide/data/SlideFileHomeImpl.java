/*
 * $Id: SlideFileHomeImpl.java,v 1.1 2004/11/29 16:13:51 aron Exp $
 * Created on 29.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.data;

import com.idega.data.IDOFactory;

/**
 * 
 *  Last modified: $Date: 2004/11/29 16:13:51 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class SlideFileHomeImpl extends IDOFactory implements SlideFileHome {
    protected Class getEntityInterfaceClass() {
        return SlideFile.class;
    }

    public SlideFile create() throws javax.ejb.CreateException {
        return (SlideFile) super.createIDO();
    }

    public SlideFile findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException {
        return (SlideFile) super.findByPrimaryKeyIDO(pk);
    }

}
