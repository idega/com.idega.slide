/*
 * $Id: SlideFileSessionBean.java,v 1.3 2006/02/22 22:07:52 laddi Exp $
 * Created on 30.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import com.idega.business.IBOSessionBean;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.business.ICFileSystemSession;
import com.idega.core.file.business.ICFileVersion;
import com.idega.core.file.data.ICFile;

/**
 * 
 *  Last modified: $Date: 2006/02/22 22:07:52 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public class SlideFileSessionBean extends IBOSessionBean implements ICFileSystemSession {
   
    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#getHome()
     */
    public ICFile getHome() {
        //getSlideSession().getWebdavResource(helper.getHomeFolder());
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#getDocumentsHome()
     */
    public ICFile getDocumentsHome() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#getLibraryHome()
     */
    public ICFile getLibraryHome() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#list()
     */
    public String[] list() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#touch(java.lang.String)
     */
    public void touch(String name) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#mkdir(java.lang.String)
     */
    public void mkdir(String name) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#mktree(com.idega.core.data.ICTreeNode)
     */
    public void mktree(ICTreeNode tree) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#cd(java.lang.String)
     */
    public void cd(String name) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#rm(com.idega.core.file.data.ICFile)
     */
    public void rm(ICFile file) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#checkin(com.idega.core.file.data.ICFile)
     */
    public void checkin(ICFile file) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#checkout(com.idega.core.file.data.ICFile)
     */
    public void checkout(ICFile file) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#lock(com.idega.core.file.data.ICFile)
     */
    public void lock(ICFile file) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#unlock(com.idega.core.file.data.ICFile)
     */
    public void unlock(ICFile file) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.idega.core.file.business.ICFileSession#getVersionHistory(com.idega.core.file.data.ICFile)
     */
    public ICFileVersion[] getVersionHistory(ICFile file) {
        // TODO Auto-generated method stub
        return null;
    }

}
