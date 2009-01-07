/**
 * $Id: DomainConfig.java,v 1.2 2009/01/07 11:41:27 tryggvil Exp $
 * Created in 2009 by tryggvil
 *
 * Copyright (C) 2000-2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.webdavservlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;

import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.content.Content;
import org.apache.slide.security.Security;
import org.apache.slide.structure.Structure;
import org.apache.tools.ant.filters.ReplaceTokens;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;
import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * <p>
 * This class manages the configuration and startup of the Slide instance. 
 * e.g. manages what Domain xml config file is read to initialize Slide.
 * </p>
 * 
 * Last modified: $Date: 2009/01/07 11:41:27 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
@Scope("singleton")
@Service(DomainConfig.SPRING_BEAN_IDENTIFIER)
public class DomainConfig {
	
	private static final String SLIDE_BASEPATH_PROPERTY = "slide.basepath";
	public static final String SLIDE_STORE_TYPE = "slide.store.type";
	public static final String SLIDE_DATA_PATH = "slide.data.path";
	public static final String TYPE_TXFILE = "txfile";
	public static final String TYPE_RDBMS = "rdbms";
	
	public static final String SPRING_BEAN_IDENTIFIER="slideDomainConfig";
	
	public static final String STORE_FILESTORE_DYNAMIC_FILENAME="Domain-FileStore-Dynamic.xml";
	public static final String STORE_FILESTORE_FILENAME="Domain-FileStore.xml";
	public static final String STORE_RDBMS_FILENAME="Domain-RDBMS.xml";
	
	public static final String STORES_CLASSPATH="com/idega/slide/properties/";
	
	String domainConfigPath;
	private ServletConfig servletConfig;
	
	//Slide config members:
	private NamespaceAccessToken namespace;
	private Structure structure;
	private Content content;
	private Security security;
	private URL configurationURL;
	private boolean initialized=false;
	private boolean jndiLookupEnabled=true;
	private boolean iwAppPropertyLookupEnabled=true;
	//private String storeConfigClasspath=STORES_CLASSPATH+STORE_FILESTORE_DYNAMIC_NAME;
	private String storeType=TYPE_TXFILE;
	
	private boolean usingVariableBase=true;
	
	String basePath;

	//Just for testing purposes
	public DomainConfig() {
		//initializeContextVariables();
	}

	public DomainConfig(ServletConfig config) {
		this.setServletConfig(config);
		//this.domainConfigPath=getDomainPath(config);
		//createDomainFile(config);
		//initializeContextVariables();
	}
	
	public void initializeContextVariables() {
		if(getBasePath()==null && getSlideBasePathFromSystemProperty()!=null){
			this.setBasePath(getSlideBasePathFromSystemProperty());
		}
		else{
			setBasePathFromFurrentFolder();
		}
		
		if(isJndiLookupEnabled()){
			if(getSlideBasePathFromJNDI()!=null){
				this.setBasePath(getSlideBasePathFromJNDI());
			}
		}
		
		if(isIwAppPropertyLookupEnabled()){
			lookupDomainSettingfromApplicationProperties();
		}
	}
	
	private void setBasePathFromFurrentFolder() {
		if(getBasePath()==null){
			String currentProcessPath = System.getProperty("user.dir");
			setBasePath(currentProcessPath);
		}
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public synchronized void initialize() {
		if (initialized) {
			return;
		}
		else{
			initializeContextVariables();
			try {
				if(!Domain.isInitialized()){
					if(initializeByStream()){
						InputStream configurationInputStream=null;
						if(getStoreType().equals(TYPE_TXFILE)){
							if(usingVariableBase){
								configurationInputStream = this.getClass().getClassLoader().getResourceAsStream(STORES_CLASSPATH+STORE_FILESTORE_DYNAMIC_FILENAME);
								
								MemoryFileBuffer memory = new MemoryFileBuffer();
								MemoryOutputStream memoryOut = new MemoryOutputStream(memory);
								parseAndReplaceConfigInputStream(configurationInputStream,new BufferedWriter(new OutputStreamWriter(memoryOut)));
								//Now read from the replaced buffer
								configurationInputStream = new MemoryInputStream(memory);
							}
							else{
								configurationInputStream = this.getClass().getClassLoader().getResourceAsStream(STORES_CLASSPATH+STORE_FILESTORE_FILENAME);
							}
						}
						else if(getStoreType().equals(TYPE_RDBMS)){
							configurationInputStream = this.getClass().getClassLoader().getResourceAsStream(STORES_CLASSPATH+STORE_RDBMS_FILENAME);
						}
						Domain.init(configurationInputStream);

					}
					else if(initializeByUrl()){
						if(configurationURL==null){
							URL defaultConfigURL = this.getClass().getClassLoader().getResource(STORES_CLASSPATH+STORE_FILESTORE_FILENAME);
							configurationURL=defaultConfigURL;
						}
						Domain.init(configurationURL);
					}
					
					

				}
				//InputStream configStream = this.getClass().getClassLoader().getResourceAsStream("com/idega/slide/jcr/Domain.xml");
				//InputStream configStream = new FileInputStream(new File("src/java/com/idega/slide/jcr/Domain.xml"));
				//Domain.init(configStream);
				namespace = Domain.accessNamespace(new SecurityToken(""), Domain.getDefaultNamespace());
				structure = namespace.getStructureHelper();
				content = namespace.getContentHelper();
				security = namespace.getSecurityHelper();
				this.initialized=true;
			} catch(Exception e) {
				initialized = false;
				e.printStackTrace();
			}
		}
	}
	
	public void stop(){
		Domain.closeNamespace(this.getNamespace());
		this.initialized=false;
	}
	
	public void reInitialize(){
		stop();
		initialize();
	}

	
	private boolean initializeByUrl() {
		return false;
	}

	private boolean initializeByStream() {
		// TODO Auto-generated method stub
		return true;
	}

	private static Context getEnvContext()throws NamingException{
		InitialContext initialContext = new InitialContext();
		if(initialContext==null){
			initialContext = new InitialContext();
		}
		return (Context) initialContext.lookup("java:comp/env");
	}
	
	public String getSlideBasePathFromJNDI(){
		try {
			String path = (String) getEnvContext().lookup("properties/"+SLIDE_BASEPATH_PROPERTY);
			return path;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error looking up from JNDI: "+e.getMessage());
		}
		return null;
	}
	
	public String getSlideBasePathFromSystemProperty(){
		return System.getProperty(SLIDE_BASEPATH_PROPERTY);
	}
	
	public static void main(String[] args){
		
		DomainConfig config = new DomainConfig();
		//config.domainConfigPath="C:\\Documents and Settings\\Tryggvi\\idega\\lucid\\org.apache.slide\\properties\\Domain-FileStore.xml";
		config.domainConfigPath="/Users/tryggvil/Documents/idega/Development/slide/org.apache.slide/properties/Domain-FileStore-Dynamic.xml";
		config.setBasePath("/Users/tryggvil/Documents/idega/Development/stores/pga-bin");
		config.parseAndReplaceConfigFile();
		
	}
	
	private void createDomainFile(ServletConfig config) {
		
		//Temporary workaround to copy config file to webapp
		if(config!=null){
			IWMainApplication iwma = IWMainApplication.getIWMainApplication(config.getServletContext());
			IWBundleResourceFilter.copyResourceFromJarToWebapp(iwma, this.domainConfigPath);
		}
		if(usingVariableBase) {
			parseAndReplaceConfigFile();
		}
		
	}

	private void parseAndReplaceConfigFile() {
        FilterChain filterChain = new FilterChain();
        
        // add all the token specified in the containers configuration into the filterchain
        addTokensToFilterChain(filterChain, getProperties());

        //configureFiles(filterChain);
        copyFileAndReplace(this.domainConfigPath,this.domainConfigPath+"-parsed",filterChain);
	}
	
	protected void parseAndReplaceConfigInputStream(InputStream input,Writer outConfigWriter) throws IOException{
        FilterChain filterChain = new FilterChain();
        
        // add all the token specified in the containers configuration into the filterchain
        addTokensToFilterChain(filterChain, getProperties());

        //configureFiles(filterChain);
        replaceConfigfileStream(input,outConfigWriter, filterChain);
	}
	
    private Map getProperties() {
    	Map properties = new HashMap();
    	properties.put(SLIDE_BASEPATH_PROPERTY,basePath);
		return properties;
	}

    /**
     * 
     */
    public void copyFileAndReplace(String source, String target, FilterChain filterChain)
    {
        try
        {
        	FileUtils fileUtils = FileUtils.newFileUtils();
        	
        	if(!source.equals(target)){
        		fileUtils.copyFile(new File(source).getAbsolutePath(),
        				new File(target).getAbsolutePath());
        	}
            InputStream fileIS = new FileInputStream(source);
            if (fileIS == null)
            {
                throw new IOException("File [" + source + "] not found");
            }

		    FileWriter outWriter = new FileWriter(target);

            replaceConfigfileStream(fileIS,outWriter, filterChain);
        } 
        catch (IOException e)
        {
            throw new RuntimeException("Failed to copy source file [" + source + "] to [" + target
                    + "] with FilterChain", e);
        }
    }

	private void replaceConfigfileStream(InputStream configInputStream, Writer targetOut,
			FilterChain filterChain) throws IOException {
		BufferedReader in = null;
		BufferedWriter out = null;
		try
		{
		    ChainReaderHelper helper = new ChainReaderHelper();
		    helper.setBufferSize(8192);
		    helper.setPrimaryReader(new BufferedReader(new InputStreamReader(configInputStream)));
		    Vector filterChains = new Vector();
		    filterChains.add(filterChain);
		    helper.setFilterChains(filterChains);
		    in = new BufferedReader(helper.getAssembledReader());

		    out = new BufferedWriter(targetOut);

		    String line;
		    while ((line = in.readLine()) != null)
		    {
		        if (line.length() == 0)
		        {
		            out.newLine();
		        } 
		        else
		        {
		            out.write(line);
		            out.newLine();
		        }
		    }
		} 
		finally
		{
		    if (in != null)
		    {
		        in.close();
		    }
		    if (out != null)
		    {
		        out.close();
		    }
		}
	}
    
	/**
     * Add a token to an existing filter chain.
     *
     * @param filterChain the filter chain to augment
     * @param key the token key
     * @param value the token value
     */
    public void addTokenToFilterChain(FilterChain filterChain, String key,
        String value)
    {
        ReplaceTokens replaceToken = new ReplaceTokens();
        ReplaceTokens.Token token = new ReplaceTokens.Token();
        token.setKey(key);
        token.setValue(value);
        replaceToken.addConfiguredToken(token);
        filterChain.addReplaceTokens(replaceToken);
    }
  
    /**
     * Add the map of tokens to the filterChain.
     * 
     * @param filterChain The filterchain to use
     * @param map The map
     */
    public void addTokensToFilterChain(FilterChain filterChain, Map map)
    {
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            String value = (String) map.get(key);
            addTokenToFilterChain(filterChain, key, value);
        }
    }

	/**
	 * <p>
	 * Gets the path to the Domain.xml file that is used to initialize Slide
	 * </p>
	 * @return
	 */
	protected String getDomainPath(ServletConfig config) {
		String domainparam;
		String domainTxPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain-FileStore.xml";
		String domainRdbmsPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain.xml";

		IWMainApplication iwma = IWMainApplication.getIWMainApplication(config.getServletContext());
		domainparam=domainTxPath;
		try {
			//First check if a written application property is set:
			String typeProperty = iwma.getSettings().getProperty(SLIDE_STORE_TYPE);
			String basePathProperty = iwma.getSettings().getProperty(SLIDE_DATA_PATH);
			if(basePathProperty!=null){
				usingVariableBase=true;
				this.basePath=basePathProperty;
			}
			if(typeProperty!=null){
				if(typeProperty.equals(TYPE_TXFILE)){
					domainparam=domainTxPath;
				}
				else if(typeProperty.equals(TYPE_RDBMS)){
					domainparam=domainRdbmsPath;
				}
			}
			else{
				if(basePathProperty==null){
					basePathProperty="../data";
					iwma.getSettings().setProperty(SLIDE_DATA_PATH, basePathProperty);
					usingVariableBase=true;
					this.basePath=basePathProperty;
				}
				domainparam=domainTxPath;
			}
			
			//Register the usage for future reference
			if(domainparam.equals(domainTxPath)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_TXFILE);
			}
			else if(domainparam.equals(domainRdbmsPath)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_RDBMS);
			}
			
			

			//THE DEFAULT WILL NOW BE TXFILE!!
			//Eiki
			//Secondly check the database if it supports slide:
//		    Connection conn = ConnectionBroker.getConnection();
//		    String datastoreType = SQLSchemaAdapter.detectDataStoreType(conn);
//		    ConnectionBroker.freeConnection(conn);
//		    SQLSchemaAdapter adapter = SQLSchemaAdapter.getInstance(datastoreType);
//		    if(adapter.getSupportsSlide()){
//		    	domainparam=domainRdbmsPath;
//		    }
		    
		    
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	
		return domainparam;
	}
	
	/**
	 * <p>
	 * Gets the path to the Domain.xml file that is used to initialize Slide
	 * </p>
	 * @return
	 */
	protected void lookupDomainSettingfromApplicationProperties() {
		String domainparam;
		//String domainTxPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain-FileStore.xml";
		//String domainRdbmsPath = "/idegaweb/bundles/org.apache.slide.bundle/properties/Domain.xml";

		IWMainApplication iwma=null;
		if(this.getServletConfig()!=null){
			iwma = IWMainApplication.getIWMainApplication(getServletConfig().getServletContext());
		}
		else{
			iwma = IWMainApplication.getDefaultIWMainApplication();
		}
		//domainparam=domainTxPath;
		try {
			//First check if a written application property is set:
			String typeProperty = iwma.getSettings().getProperty(SLIDE_STORE_TYPE);
			String basePathProperty = iwma.getSettings().getProperty(SLIDE_DATA_PATH);
			if(basePathProperty!=null){
				usingVariableBase=true;
				this.basePath=basePathProperty;
			}
			if(typeProperty!=null){
				if(typeProperty.equals(TYPE_TXFILE)){
					//domainparam=domainTxPath;
					setStoreType(TYPE_TXFILE);
				}
				else if(typeProperty.equals(TYPE_RDBMS)){
					//domainparam=domainRdbmsPath;
					setStoreType(TYPE_RDBMS);
				}
			}
			else{
				/*if(basePathProperty==null){
					basePathProperty="../data";
					iwma.getSettings().setProperty(SLIDE_DATA_PATH, basePathProperty);
					usingVariableBase=true;
					this.basePath=basePathProperty;
				}*/
				//domainparam=domainTxPath;
				setStoreType(TYPE_TXFILE);
			}
			
			//Register the usage for future reference
			//if(domainparam.equals(domainTxPath)){
			if(getStoreType().equals(TYPE_TXFILE)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_TXFILE);
			}
			//else if(domainparam.equals(domainRdbmsPath)){
			else if(getStoreType().equals(TYPE_RDBMS)){
				iwma.getSettings().setProperty(SLIDE_STORE_TYPE, TYPE_RDBMS);
			}
			
			

			//THE DEFAULT WILL NOW BE TXFILE!!
			//Eiki
			//Secondly check the database if it supports slide:
//		    Connection conn = ConnectionBroker.getConnection();
//		    String datastoreType = SQLSchemaAdapter.detectDataStoreType(conn);
//		    ConnectionBroker.freeConnection(conn);
//		    SQLSchemaAdapter adapter = SQLSchemaAdapter.getInstance(datastoreType);
//		    if(adapter.getSupportsSlide()){
//		    	domainparam=domainRdbmsPath;
//		    }
		    
		    
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	
		//return domainparam;
	}



	public String getConfigPath() {
		return this.domainConfigPath;
	}

	public NamespaceAccessToken getNamespace() {
		return namespace;
	}

	public void setNamespace(NamespaceAccessToken namespace) {
		this.namespace = namespace;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public void setJndiLookupEnabled(boolean jndiLookupEnabled) {
		this.jndiLookupEnabled = jndiLookupEnabled;
	}

	public boolean isJndiLookupEnabled() {
		return jndiLookupEnabled;
	}

	private void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	private String getStoreType() {
		return storeType;
	}

	public void setIwAppPropertyLookupEnabled(boolean iwAppPropertyLookupEnabled) {
		this.iwAppPropertyLookupEnabled = iwAppPropertyLookupEnabled;
	}

	public boolean isIwAppPropertyLookupEnabled() {
		return iwAppPropertyLookupEnabled;
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public ServletConfig getServletConfig() {
		return servletConfig;
	}

}
