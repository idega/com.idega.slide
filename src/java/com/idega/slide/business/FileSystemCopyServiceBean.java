/*
 * $Id: FileSystemCopyServiceBean.java,v 1.8 2006/02/22 22:07:52 laddi Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;

import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.slide.data.SlideFile;
import com.idega.slide.data.SlideFileHome;
import com.idega.util.database.ConnectionBroker;

/**
 * 
 *  Last modified: $Date: 2006/02/22 22:07:52 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.8 $
 */
public class FileSystemCopyServiceBean extends IBOServiceBean  implements FileSystemCopyService{
    
    private SlideFileHome fileHome = null;
    private ICFileHome icFileHome = null;
   
    private WebdavResource webdavResource = null;
    private String path = null;
    private HttpURL httpURL = null;
    private boolean overwrite = true;
    
    private String pageFolderName = "Pages";
    private String templateFolderName = "Templates";
    private String publicFolderName = "Public";
    private String userFolderName = "Users";
    
    
    private SlideFileHome getFileHome(){
        try {
            if(fileHome==null)
                fileHome = (SlideFileHome)IDOLookup.getHome(SlideFile.class);
            return fileHome;
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        }
    }
    
    private ICFileHome getICFileHome(){
        try {
            if(icFileHome==null)
                icFileHome = (ICFileHome)IDOLookup.getHome(ICFile.class);
            return icFileHome;
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        }
    }
    
    public void run() throws Exception{
        httpURL = getService().getWebdavServerURL();
       
        if(httpURL!=null){
            httpURL.getEscapedURI();
            connect();
	        copyPageFiles();
	        copyGroupFiles();
	        copyMediaSystem();
	        copyLeftovers();
	        disconnect();
        }
        else{
            log("Filesystem could not be copied to Slide, no server url was provided");
        	}
            
    }
    
    private void copyPageFiles(){
        try {
            ICPage startPage = getIWApplicationContext().getDomain().getStartPage();
            String folder = pageFolderName;
            checkAndCreateFolder(folder);
            copy2(folder,startPage.getFile(),"ibxml");
            Collection childs = startPage.getChildren();
            String subFolder = folder+"/"+startPage.getName();
            checkAndCreateFolder(subFolder);
            if(childs!=null)
                copyPages(subFolder,childs);
            
            ICPage templatePage = getIWApplicationContext().getDomain().getStartTemplate();
            folder = templateFolderName;
            checkAndCreateFolder(folder);
            copy2(folder,templatePage.getFile(),"ibxml");
            childs = templatePage.getChildren();
            subFolder = folder+"/"+templatePage.getName();
            checkAndCreateFolder(subFolder);
            if(childs!=null)
                copyPages(subFolder,childs);
            
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        } catch (Exception e) {
            throw new IBORuntimeException(e);
        }
        
    }
    
    public void copyPages(String folder,Collection pages)throws Exception{
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            ICPage element = (ICPage) iter.next();
            ICFile file = element.getFile();
            if(file.getName()==null)
                file.setName(element.getName());
            copy2(folder,file,"ibxml");
            if(element.getChildCount()>0){
                Collection childs = element.getChildren();
                checkAndCreateFolder(folder+"/"+element.getName());
                copyPages(folder+"/"+element.getName(),childs);
            }
 
        }
    }
    
    
    private void copyGroupFiles(){
        String sql = "select user_login,ic_group_id,home_folder_id"
            +" from ic_login l, ic_group g "
            +" where l.ic_user_id = g.ic_group_id "
            +"and g.home_folder_id is not null";
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        
        try {
            
            conn = ConnectionBroker.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                String username = rs.getString(1);
                Integer fileID = new Integer(rs.getInt(3));
                ICFile root = getICFileHome().findByPrimaryKey(fileID);
                if(root.isFolder()){
                    Collection children = getICFileHome().findChildren(root,null,null,null);
                    copy(userFolderName+"/"+username,children);
                }
                else{
                    copy(userFolderName+"/"+username,root);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FinderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if(stmt !=null){
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if(conn!=null){
                ConnectionBroker.freeConnection(conn);
            }
        }
        
        
    }
    
    private void copyMediaSystem(){
        try {
            ICFile root = getICFileHome().findRootFolder();
            Collection children = getICFileHome().findChildren(root,null,null,null);
            copy(publicFolderName,children);
            
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        } catch (FinderException e) {
            throw new IBORuntimeException(e);
        } catch (Exception e) {
            throw new IBORuntimeException(e);
        }
    }
    
    private void copyLeftovers(){
        // lets copy everything still not copied, and not folders
        String sql = "select ic_file_id from ic_file where ext_url is null and mime_type != 'application/vnd.iw-folder'";
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
           
            conn = ConnectionBroker.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
               
                Integer fileID = new Integer(rs.getInt(1));
                ICFile root = getFileHome().findByPrimaryKey(fileID);
                /* skip the folders
                if(root.isFolder()){
                    Collection children = getICFileHome().findChildren(root,null,null,null);
                    copy(publicFolderName+"/attic",children);
                }
                else*/{
                    copy(userFolderName+"/attic",root);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FinderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if(stmt !=null){
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if(conn!=null){
                ConnectionBroker.freeConnection(conn);
            }
        }
    }
    
    private void connect(){
        
        /*
        if (!uri.endsWith("/") && !uri.endsWith("\\")) {
            // append / to the path
             uri+="/";
        }
        */
        //System.out.println("connect " + uri);
        try {
          //httpURL = uriToHttpURL(uri);
//          httpURL.setUserinfo("root","root");
        if (webdavResource == null) {
            webdavResource = getService().getWebdavResourceAuthenticatedAsRoot();
            //webdavResource.setDebug(Integer.MAX_VALUE);
            
            // is not a collection?
            if (!webdavResource.getResourceType().isCollection()) {
                webdavResource = null;
                httpURL = null;
                //System.out.println("Error: " + uri + " is not a collection! Use open/connect only for collections!");
            }
            
        } else {
            webdavResource.close();
//            webdavResource.setHttpURL(httpURL);
            webdavResource = getService().getWebdavResourceAuthenticatedAsRoot();
        }
        setPath(webdavResource.getPath()+"/files");
        }
        catch (HttpException we) {
            System.out.print("HttpException.getReasonCode(): "+ we.getReasonCode());
            if (we.getReasonCode() == HttpStatus.SC_UNAUTHORIZED) {
                try {
                   
                    String userName = "root";
                    if ((userName==null) || (userName.length()==0)) {
                        disconnect();
                        return;
                    }
                    userName = userName.trim();
                    String password = "root";
                    if (password != null)
                        password= password.trim();
                    try {
                        if (webdavResource != null)
                            webdavResource.close();
                    } catch (IOException e) {
                    } finally {
                        httpURL = null;
                        webdavResource = null;
                    }
                    //httpURL = uriToHttpURL(uri);
                    // It should be used like this way.
                    httpURL.setUserinfo(userName, password);
                    webdavResource = new WebdavResource(httpURL);
                    //webdavResource.setDebug(Integer.MAX_VALUE);
                    setPath(webdavResource.getPath());


                    if (!webdavResource.getResourceType().isCollection()) {
                        webdavResource = null;
                        httpURL = null;
                        System.out.println("Error: " + httpURL.getURI() + " is not a collection! Use open/connect only for collections!");
                    }
                }
                catch (Exception ex) {
                    handleException(ex);
                    httpURL = null;
                    webdavResource = null;
                }
            }
            else  {
                handleException(we);
                httpURL = null;
                webdavResource = null;
            }
        }
        catch (Exception ex) {
            handleException(ex);
            webdavResource = null;
            httpURL = null;
        }
        
    }
    
    void disconnect()
    {
        System.out.println("disconnect");
        try {
            webdavResource.close();
        } catch (IOException e) {
        } finally {
            // Make sure the connection closed.
            httpURL = null;
            webdavResource = null;
        }
        
    }
    
    private void handleException(Exception ex)
    {
        if (ex instanceof HttpException) {
            if (((HttpException) ex).getReasonCode() == HttpStatus.SC_METHOD_NOT_ALLOWED) {
                System.out.println("Warning: Not WebDAV-enabled?");
            }
            else if (((HttpException) ex).getReasonCode() == HttpStatus.SC_UNAUTHORIZED) {
                System.out.println("Warning: Unauthorized");
            }
            else {
                System.out.println("Warning: " + ex.getMessage());
            }
        }
        else if (ex instanceof IOException) {
            System.out.println("Error: " + ex.getMessage());
        }
        else {
            System.out.println("Fatal Error: " + ex.getMessage());
            ex.printStackTrace(System.out);
            System.out.println("Please, email to slide-user@jakarta.apache.org");
           
        }
    }
    
    public void copy(String folder,Collection files)throws Exception{
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            ICFile element = (ICFile) iter.next();
            if(element.isFolder()){
                Collection childs = getICFileHome().findChildren(element,null,null,null);
                checkAndCreateFolder(folder);
                copy(folder+"/"+element.getName(),childs);
            }
            else{
                checkAndCreateFolder(folder);
                copy2(folder,element,null);
            }
            
        }
    }
    
    public void checkAndCreateFolder(String folder){
        try {
            webdavResource.setPath(folder);
            if(!webdavResource.exists())
                mkcol(folder);
        } catch (HttpException e) {
           mkcol(folder);
           createFolderIfNotExists(folder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public void createFolderIfNotExists(String folderPath){
       
        int index = folderPath.indexOf("/",1);
        while(index>0){
            String path = folderPath.substring(0,index+1);
            //System.out.println(path);
            mkcol(path);
            index = folderPath.indexOf("/",index+1);
        }
    }
    
    void put(ICFile file, String path)
    {
        try {
            String dest = getRemoteTargetFileName( file.getName(),  path);
            
            String currentPath = webdavResource.getPath();
            
            try {
                webdavResource.setPath(dest);
                
                if (webdavResource.exists()) {
                    System.out.print("Aleady exists. ");
                }
                webdavResource.setPath(currentPath);
            } catch (Exception ex) {
            } 
            		if(overwrite){
                    System.out.print("Uploading  '" + file.getName() + "' to '" + dest + "' ");
                    if (webdavResource.putMethod(dest, file.getFileValue())) {
                        updateFile(file, dest);
                        System.out.println("succeeded.");
                    }
                    else {
                        System.out.println("failed.");
                        System.out.println(webdavResource.getStatusMessage());
                    }
            		}
               
            }
        
        catch (Exception ex) {
            handleException(ex);
        }
    }
    
    private void updateFile(ICFile file, String path){
        try {
            SlideFile slideFile = getFileHome().findByPrimaryKey(file.getPrimaryKey());
            slideFile.setExternalURL(path);
            slideFile.store();
        } catch (IDOStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EJBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FinderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    
        
    }
    
    void mkcol(String path)
    {
        try {
            path = checkUri(path);
            System.out.print("Making '" + path + "' collection: ");
            if (webdavResource.mkcolMethod(path)) {
                System.out.println("succeeded.");
            } else {
                System.out.println("failed.");
                System.out.println(webdavResource.getStatusMessage());
            }
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }
    
    void delete(String path)
    {
        try {
            path = checkUri(path);
            System.out.print("Deleting '" + path + "': ");
            if (webdavResource.deleteMethod(path)) {
                System.out.println("succeeded.");
            } else {
                System.out.println("failed.");
                System.out.println(webdavResource.getStatusMessage());
            }
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }
    
    private void copy2(String folder,ICFile file,String extension)throws Exception{
        //delete(folder);
        //mkcol(folder);
        String fileName = file.getName();
        if(extension!=null && !fileName.endsWith(extension)){
            fileName += "."+extension;
        }
        put(file,folder+"/"+fileName);
    }
    
    private void copy(String folder, ICFile file)throws Exception{
        /*
        System.out.println("Moving "+file.getName()+" in folder "+folder);
        
        WebdavFile rootFile = getService().getWebdavFile();
        
        File[] files = rootFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
        String rootpath = rootFile.getAbsolutePath();
        WebdavFile filefolder = new WebdavFile(rootFile,"files");
        WebdavFile newfolder = new WebdavFile(filefolder,"myFolder");
        if(!newfolder.exists())
            newfolder.mkdir();
        
        files = rootFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
        */
        copy2(folder, file, null);
        
        
    }
    
    
    protected WebdavResource getResource(String path)throws Exception{
    		IWSlideService service = getService();
        return service.getWebdavResourceAuthenticatedAsRoot(path);
    }
    
    public IWSlideService getService(){
        try {
            return (IWSlideService) getServiceInstance(IWSlideService.class);
        } catch (IBOLookupException e) {
            throw new IBORuntimeException(e);
        }
    }
    
    private String getRemoteTargetFileName(String filename, String path) {
        
        String srcPathName = null;
        String target = null;
       
 

        // get traget filename from last portion of filename
        StringTokenizer st = new StringTokenizer(filename, "/\\");
        while (st.hasMoreTokens()) {
            srcPathName = st.nextToken();
        }
        
        
        try {
                
            if (path != null) {
                target = checkUri(path);
                
                // check is path a collection ?
                String currentPath = webdavResource.getPath();
                
                webdavResource.setPath(target);
                
                if (webdavResource.exists()) {
                    if (webdavResource.isCollection()) {
                        target += "/" + srcPathName;
                    } 
                } 
                
                webdavResource.setPath(currentPath);
                
            } else {
                target = checkUri(getPath() + "/" + srcPathName);
            }
                
                            
        } catch (Exception ex) {
        }
        
        return target;
               

    }
    
    private String checkUri(String uri) throws IOException
    {

        if (webdavResource == null) {
            throw new IOException("Not connected yet.");
        }

        if (uri==null) {
            uri=webdavResource.getPath();
        }

        if (!uri.startsWith("/")) {
            uri = getPath() + uri;
        }

        return normalize(uri);
    }

    /**
     * Get the path.
     *
     * @return the path string.
     */
    private String getPath()
    {
        return path;
    }
    
    
    /**
     * Return a context-relative path, beginning with a "/", that represents
     * the canonical version of the specified path after ".." and "." elements
     * are resolved out.  If the specified path attempts to go outside the
     * boundaries of the current context (i.e. too many ".." path elements
     * are present), return <code>null</code> instead.
     *
     * @param path the path to be normalized.
     * @return the normalized path.
     */
    private String normalize(String path)
    {
        if (path == null)
            return null;

        String normalized = path;

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
            break;
            normalized = normalized.substring(0, index) +
            normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
            break;
            if (index == 0)
            return ("/");  // The only left path is the root.
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
            normalized.substring(index + 3);
        }

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
            break;
            normalized = normalized.substring(0, index) +
            normalized.substring(index + 1);
        }

        // Return the normalized path that we have completed
        return (normalized);
    }
    
    private void setPath(String path){
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        this.path = normalize(path);
    }   
}