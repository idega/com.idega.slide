/*
 * $Id: ResourceHelper.java,v 1.1 2004/11/22 09:58:36 aron Exp $
 * Created on 22.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;


/**
 *  ResourceHelper provides file urls for user's home folders and the system's basic folder structure
 * 
 *  Last modified: $Date: 2004/11/22 09:58:36 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class ResourceHelper {
    
    private final static String DIR_APPLICATIONS = "Applications";
    private final static String DIR_LIBRARY = "Library";
    private final static String DIR_DOCUMENTS = "Documents";
    private final static String SLASH = "/";
    private static String rootFileFolder = "files";
    private static String rootUserFolder = "Users";
    
    private String userName;

    public ResourceHelper(String userName){
        this.userName = userName;
    }
    
    /**
     * Gets a uri to the user's Home folder
     * @param user
     * @return
     */
    public String getHomeFolder(){
        return createURL("");
    	}
    
    /**
     * Gets the uri to the user's Application folder
     * @return
     */
    public String getApplications(){
        return createURL(DIR_APPLICATIONS);
    }
    
    /**
     * Gets the uri to the user's Library folder
     * @return
     */
    public String getLibrary(){
        	return createURL(DIR_LIBRARY);
    }
    
    /**
     * Gets the uri to user's Documents folder
     * @return
     */
    public String getDocuments(){
        	return createURL(DIR_DOCUMENTS);
    }
    
    private String createURL(String folder){
       return SLASH+rootFileFolder+SLASH+(userName!=null?rootUserFolder+SLASH+userName+SLASH:"")+folder+(folder.length()>0?SLASH:"");
    }
    
    /**
     * Gets a uri to a folder in the user's home
     * @param folder
     * @return
     */
    public String getFolderURL(String folder){
        return createURL(folder);
    }
    
    public static void main(String[] args){
        ResourceHelper helper = new ResourceHelper("john");
        System.out.println(helper.getHomeFolder());
        System.out.println(helper.getApplications());
        System.out.println(helper.getLibrary());
        System.out.println(helper.getDocuments());
        
        helper = new ResourceHelper(null);
        System.out.println(helper.getHomeFolder());
        System.out.println(helper.getApplications());
        System.out.println(helper.getLibrary());
        System.out.println(helper.getDocuments());
    }
    
}
