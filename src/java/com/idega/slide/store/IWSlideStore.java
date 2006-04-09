/*
 * $Id: IWSlideStore.java,v 1.2 2006/04/09 11:44:15 laddi Exp $
 * Created on Jun 24, 2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.store;

import java.sql.Connection;
import java.util.Hashtable;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.search.basic.IBasicExpressionFactoryProvider;
import org.apache.slide.store.ContentStore;
import org.apache.slide.store.LockStore;
import org.apache.slide.store.NodeStore;
import org.apache.slide.store.RevisionDescriptorStore;
import org.apache.slide.store.RevisionDescriptorsStore;
import org.apache.slide.store.SecurityStore;
import org.apache.slide.store.SequenceStore;
import org.apache.slide.store.impl.rdbms.DB2RDBMSAdapter;
import org.apache.slide.store.impl.rdbms.DerbyRDBMSAdapter;
import org.apache.slide.store.impl.rdbms.HsqlRDBMSAdapter;
import org.apache.slide.store.impl.rdbms.J2EEStore;
import org.apache.slide.store.impl.rdbms.JDBCStore;
import org.apache.slide.store.impl.rdbms.MySql41RDBMSAdapter;
import org.apache.slide.store.impl.rdbms.OracleRDBMSAdapter;
import org.apache.slide.store.impl.rdbms.SQLServerRDBMSAdapter;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.database.PoolManager;
import com.idega.util.dbschema.SQLSchemaAdapter;


/**
 * <p>
 * Store implementation for idegaWeb for Slide that intercepts the regular store mechanism by Slide,
 * auto-detects the right store implementation to use (JDBCStore,J2EEStore)
 * </p>
 * Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class IWSlideStore extends AbstractSlideStore{
	
	private Hashtable _parameters = new Hashtable();
	/**
	 * 
	 */
	public IWSlideStore() {
		super();
		detectStoreImplementation();
	}

	/**
	 * <p>
	 * Called by constructor
	 * </p>
	 */
	private void detectStoreImplementation() {
		
		Service service = initStoreImpl();
		setService(service);
		
		setNodeStore((NodeStore)service);
		setContentStore((ContentStore) service);
		setSecurityStore((SecurityStore) service);
		setLockStore((LockStore) service);
		setRevisionDescriptorsStore((RevisionDescriptorsStore) service);
		setRevisionDescriptorStore((RevisionDescriptorStore) service);

		try{
			setSecurityStore((SecurityStore) service);
		}
		catch(ClassCastException cce){
			System.err.println("Class "+service+" does not implement SecurityStore");
		}
		try{
			setSequenceStore((SequenceStore) service);
		}
		catch(ClassCastException cce){
			System.err.println("Class "+service+" does not implement SequenceStore");
		}
		try{
			setIbasicExpressionFactoryProvider((IBasicExpressionFactoryProvider) service);
		}
		catch(ClassCastException cce){
			System.err.println("Class "+service+" does not implement IBasicExpressionFactoryProvider");
		}
		
	}

	
	/**
	 * <p>
	 * TODO tryggvil describe method getNodeStoreImpl
	 * </p>
	 * @return
	 */
	private Service initStoreImpl() {
		
		if(ConnectionBroker.isUsingIdegaPool()){
	        PoolManager pManager = PoolManager.getInstance();
	        Connection conn = pManager.getConnection();
	        String adapter = getRDBMSAdapterClassName(conn);
	        pManager.freeConnection(conn);
	        
//	      <parameter name="adapter">org.apache.slide.store.impl.rdbms.MySql41RDBMSAdapter</parameter>
//		    <parameter name="driver">com.mysql.jdbc.Driver</parameter>
//		    <parameter name="url">jdbc:mysql://localhost/db_name?useUnicode=true&characterEncoding=UTF-8</parameter>
//		    <parameter name="user">root</parameter>
//		    <parameter name="dbcpPooling">true</parameter>  // still handled in Domain.xml
//		    <parameter name="maxPooledConnections">10</parameter>
//		    <parameter name="isolation">SERIALIZABLE</parameter> // still handled in Domain.xml
//		    <parameter name="compress">false</parameter> // still handled in Domain.xml
	        
	        this._parameters.put("adapter",adapter);
	        this._parameters.put("driver",pManager.getDriverClassForPool());
	        this._parameters.put("url",pManager.getURLForPool());
	        this._parameters.put("user",pManager.getUserNameForPool());
	        this._parameters.put("password",pManager.getPasswordForPool());
	        this._parameters.put("maxPooledConnections",String.valueOf(pManager.getMaximumConnectionCount()));
	        
	        JDBCStore store = new JDBCStore();
	        return store;
	        
		}
		else if(ConnectionBroker.isUsingJNDIDatasource()){
			
			
	        Connection conn = ConnectionBroker.getConnection();
	        String adapter = getRDBMSAdapterClassName(conn);
	        ConnectionBroker.freeConnection(conn);
			
	        this._parameters.put("adapter",adapter);
	        this._parameters.put("datasource",ConnectionBroker.getDefaultJNDIUrl());
	        
	        J2EEStore store = new J2EEStore();
	        return store;
		}
		
		throw new RuntimeException("Store implementation could not be detected");
	}
	
	
	public String getRDBMSAdapterClassName(Connection conn){
		String datastoreType = SQLSchemaAdapter.detectDataStoreType(conn);
		String adapter = "";
	    if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_ORACLE)){
	    		adapter = OracleRDBMSAdapter.class.getName();
	    } else if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_MSSQLSERVER)){
				adapter = SQLServerRDBMSAdapter.class.getName();
	    } else if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_MYSQL)){
				adapter = MySql41RDBMSAdapter.class.getName();
	    } else if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_DB2)){
				adapter = DB2RDBMSAdapter.class.getName();
	    } else if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_HSQL)){
	    		adapter = HsqlRDBMSAdapter.class.getName();
	    		this._parameters.put("dbcpPooling","false");
	    }else if(datastoreType.equals(SQLSchemaAdapter.DBTYPE_DERBY)){
			adapter = DerbyRDBMSAdapter.class.getName();
			this._parameters.put("dbcpPooling","false");
		}
        else {
    			throw new RuntimeException("Datastore of type "+datastoreType+" is not supported by Slide");
        }
	    return adapter;
	}

	/* (non-Javadoc)
	 * @see org.apache.slide.store.AbstractStore#setParameters(java.util.Hashtable)
	 */
	public void setParameters(Hashtable inParams) throws ServiceParameterErrorException, ServiceParameterMissingException {
		inParams.putAll(this._parameters);
		super.setParameters(inParams);
	}
	
	
	
}
