/*
 * $Id: IWSlideJDBCStore.java,v 1.5 2004/12/13 22:55:14 eiki Exp $
 * Created on 19.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.store;

import java.sql.Connection;
import java.util.Hashtable;

import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.store.impl.rdbms.DB2RDBMSAdapter;
import org.apache.slide.store.impl.rdbms.HsqlRDBMSAdapter;
import org.apache.slide.store.impl.rdbms.JDBCStore;
import org.apache.slide.store.impl.rdbms.MySql41RDBMSAdapter;
import org.apache.slide.store.impl.rdbms.OracleRDBMSAdapter;
import org.apache.slide.store.impl.rdbms.SQLServerRDBMSAdapter;

import com.idega.util.database.PoolManager;
import com.idega.util.dbschema.SQLSchemaAdapter;


/**
 * 
 *  Last modified: $Date: 2004/12/13 22:55:14 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
 */
public class IWSlideJDBCStore extends JDBCStore {	
	
	private Hashtable _parameters = null;
	
    public IWSlideJDBCStore() {
        PoolManager pManager = PoolManager.getInstance();
        Connection conn = pManager.getConnection();
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
        } 
        
//        else if(datastorType == DatastoreInterface.DBTYPE_POSTGRES){
//			adapter = PostgresRDBMSAdapter.class.getName();
//        } else if(datastorType == DatastoreInterface.DBTYPE_SYBASE){
//			adapter = SybaseRDBMSAdapter.class.getName();
//        }
        else {
        		throw new RuntimeException("Datastore of type "+datastoreType+" is not supported by Slide");
        }
        
        
//      <parameter name="adapter">org.apache.slide.store.impl.rdbms.MySql41RDBMSAdapter</parameter>
//	    <parameter name="driver">com.mysql.jdbc.Driver</parameter>
//	    <parameter name="url">jdbc:mysql://localhost/db_name?useUnicode=true&characterEncoding=UTF-8</parameter>
//	    <parameter name="user">root</parameter>
//	    <parameter name="dbcpPooling">true</parameter>  // still handled in Domain.xml
//	    <parameter name="maxPooledConnections">10</parameter>
//	    <parameter name="isolation">SERIALIZABLE</parameter> // still handled in Domain.xml
//	    <parameter name="compress">false</parameter> // still handled in Domain.xml
        
        
        _parameters = new Hashtable();
        _parameters.put("adapter",adapter);
        _parameters.put("driver",pManager.getDriverClassForPool());
        _parameters.put("url",pManager.getURLForPool());
        _parameters.put("user",pManager.getUserNameForPool());
        _parameters.put("password",pManager.getPasswordForPool());
        _parameters.put("maxPooledConnections",String.valueOf(pManager.getMaximumConnectionCount()));
        _parameters.put("dbcpPooling","true");
        
        
        
       
    		
    }

	
	
	
	/**
     * Initializes the data source with a set of parameters.
     *
     * @param parameters a Hashtable containing the parameters' name and 
     *                   associated value
     * @exception ServiceParameterErrorException a service parameter holds an 
     *            invalid value
     * @exception ServiceParameterMissingException a required parameter is 
     *            missing
     * @see org.apache.slide.store.impl.rdbms.JDBCStore#setParameters();
     */
    public void setParameters(Hashtable parameters) throws ServiceParameterErrorException, ServiceParameterMissingException {
    		parameters.putAll(_parameters);
		super.setParameters(parameters);
    }
}
