/*
 * $Id: FileSystemCopyServiceHomeImpl.java,v 1.1 2004/11/15 19:03:22 aron Exp $
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
 *  Last modified: $Date: 2004/11/15 19:03:22 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class FileSystemCopyServiceHomeImpl extends IBOHomeImpl implements
        FileSystemCopyServiceHome {
    protected Class getBeanInterfaceClass() {
        return FileSystemCopyService.class;
    }

    public FileSystemCopyService create() throws javax.ejb.CreateException {
        return (FileSystemCopyService) super.createIBO();
    }

}
