/*
 * $Id: ICFile2Slide.java,v 1.2 2004/11/12 16:44:46 aron Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.business;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.ejb.FinderException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.WebdavFile;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.properties.ResourceTypeProperty;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;

/**
 * 
 *  Last modified: $Date: 2004/11/12 16:44:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class ICFile2Slide {
    
    private ICFileHome fileHome = null;
    private ICPageHome pageHome = null;
    private IWApplicationContext iwac = null;
    private WebdavResource webdavResource = null;
    private String path = null;
    private HttpURL httpURL = null;
    private boolean overwrite = true;
    
    public ICFile2Slide(final IWApplicationContext iwac){
      this.iwac = iwac;   
    }
    
    private ICFileHome getFileHome(){
        try {
            if(fileHome==null)
                fileHome = (ICFileHome)IDOLookup.getHome(ICFile.class);
            return fileHome;
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        }
    }
    
    public void run(IWUserContext iwuc) throws Exception{
        connect(getWebdavServerURL(iwuc));
        
        copyPageFiles(iwuc);
        
        copyGroupFiles(iwuc);
        
        copyMediaSystem(iwuc);
        
        copyLeftovers(iwuc);
        
        disconnect();
    }
    
    private void copyPageFiles(IWUserContext iwc){
        try {
            pageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
            ICPage startPage = iwc.getApplicationContext().getDomain().getStartPage();
            String folder = "Pages";
            checkAndCreateFolder(folder);
            copy2(iwc,folder,startPage.getFile(),"ibxml");
            Collection childs = startPage.getChildren();
            String subFolder = folder+"/"+startPage.getName();
            checkAndCreateFolder(subFolder);
            if(childs!=null)
                copyPages(iwc,subFolder,childs);
            
            ICPage templatePage = iwc.getApplicationContext().getDomain().getStartTemplate();
            folder = "Templates";
            checkAndCreateFolder(folder);
            copy2(iwc,folder,templatePage.getFile(),"ibxml");
            childs = templatePage.getChildren();
            subFolder = folder+"/"+templatePage.getName();
            checkAndCreateFolder(subFolder);
            if(childs!=null)
                copyPages(iwc,subFolder,childs);
            
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        } catch (Exception e) {
            throw new IBORuntimeException(e);
        }
        
    }
    
    public void copyPages(IWUserContext iwc,String folder,Collection pages)throws Exception{
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            ICPage element = (ICPage) iter.next();
            ICFile file = element.getFile();
            if(file.getName()==null)
                file.setName(element.getName());
            copy2(iwc,folder,file,"ibxml");
            if(element.getChildCount()>0){
                Collection childs = element.getChildren();
                checkAndCreateFolder(folder+"/"+element.getName());
                copyPages(iwc,folder+"/"+element.getName(),childs);
            }
 
        }
    }
    
    
    private void copyTemplateFiles(IWUserContext iwc){
        
    }
    
    
    
    private void copyGroupFiles(IWUserContext iwc){
        
    }
    
    private void copyMediaSystem(IWUserContext iwuc){
        try {
            ICFile root = getFileHome().findRootFolder();
            Collection children = fileHome.findChildren(root,null,null,null);
            copy(iwuc,"public",children);
        } catch (IDOLookupException e) {
            throw new IBORuntimeException(e);
        } catch (FinderException e) {
            throw new IBORuntimeException(e);
        } catch (Exception e) {
            throw new IBORuntimeException(e);
        }
    }
    
    private void copyLeftovers(IWUserContext iwc){
        
    }
    
    private void connect(String uri){
        if (!uri.endsWith("/") && !uri.endsWith("\\")) {
            // append / to the path
             uri+="/";
        }

        System.out.println("connect " + uri);
        try {
          httpURL = uriToHttpURL(uri);
          httpURL.setUserinfo("root","root");
        if (webdavResource == null) {
            webdavResource = new WebdavResource(httpURL);
            webdavResource.setDebug(Integer.MAX_VALUE);
            
            // is not a collection?
            if (!((ResourceTypeProperty)webdavResource.getResourceType()).isCollection()) {
                webdavResource = null;
                httpURL = null;
                System.out.println("Error: " + uri + " is not a collection! Use open/connect only for collections!");
            }
            
        } else {
            webdavResource.close();
            webdavResource.setHttpURL(httpURL);
        }
        setPath(webdavResource.getPath());
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
                    httpURL = uriToHttpURL(uri);
                    // It should be used like this way.
                    httpURL.setUserinfo(userName, password);
                    webdavResource = new WebdavResource(httpURL);
                    webdavResource.setDebug(Integer.MAX_VALUE);
                    setPath(webdavResource.getPath());


                    if (!((ResourceTypeProperty)webdavResource.getResourceType()).isCollection()) {
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
    
    private static HttpURL uriToHttpURL(String uri) throws URIException {
        return uri.startsWith("https") ? new HttpsURL(uri)
                                       : new HttpURL(uri);
    }
    
    public void copy(IWUserContext iwc,String folder,Collection files)throws Exception{
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            ICFile element = (ICFile) iter.next();
            if(element.isFolder()){
                Collection childs = fileHome.findChildren(element,null,null,null);
                checkAndCreateFolder(folder);
                copy(iwc,folder+"/"+element.getName(),childs);
            }
            else{
                checkAndCreateFolder(folder);
                copy2(iwc,folder,element,null);
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    void put(ICFile file, String path)
    {
        try {
            String src  = file.getName();
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
    
    public void copy2(IWUserContext iwc,String folder,ICFile file,String extension)throws Exception{
        //delete(folder);
        //mkcol(folder);
        String fileName = file.getName();
        if(extension!=null && !fileName.endsWith(extension)){
            fileName += "."+extension;
        }
        put(file,folder+"/"+fileName);
    }
    
    public void copy(IWUserContext iwc,String folder, ICFile file)throws Exception{
        System.out.println("Moving "+file.getName()+" in folder "+folder);
        
        WebdavFile rootFile = getSession(iwc).getWebdavFile();
        
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
        
        
    }
    
    public String getWebdavServerURL(IWUserContext iwuc){
	    
	    try {
	       String server = iwuc.getApplicationContext().getDomain().getServerName();
	       if(server.endsWith("/"))
	           server = server.substring(0,server.lastIndexOf("/"));
	       server += getWebdavServletURL(iwuc)+"/files";
           /*
	       HttpURL hrl = new HttpURL(server);
            hrl.setUserinfo("root","root");
            //hrl.setUserInfo("user","pass");
            return hrl;*/
            return server;
        } catch (Exception e) {
           throw new IBORuntimeException(e);
        }
	}
    
    public String getWebdavServletURL(IWUserContext iwuc){
		return iwuc.getApplicationContext().getIWMainApplication().getApplicationContextURI()+WEBDAV_SERVLET_URI;
	}
    
    protected static final String WEBDAV_SERVLET_URI = "/servlet/webdav";
    
    public WebdavResource getWebdavResource(IWUserContext iwuc){
	    try {
            return new WebdavResource(getWebdavServerURL(iwuc));
        } catch (HttpException e) {
            throw new IBORuntimeException(e);
        } catch (IOException e) {
            throw new IBORuntimeException(e);
        }
	}
    
    public WebdavResource getResource(String path)throws Exception{
        return new WebdavResource(path);
    }
    
    public IWSlideService getService(){
        try {
            return (IWSlideService) IBOLookup.getServiceInstance(iwac,IWSlideService.class);
        } catch (IBOLookupException e) {
            throw new IBORuntimeException(e);
        }
    }
    
    public IWSlideSession getSession(IWUserContext iwuc){
        try {
            return (IWSlideSession) IBOLookup.getSessionInstance(iwuc,IWSlideSession.class);
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
    
    private void setPath(String path)
    {
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        this.path = normalize(path);
    }

    
    

}
