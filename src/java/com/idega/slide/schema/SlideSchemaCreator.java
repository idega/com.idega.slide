package com.idega.slide.schema;

import java.sql.Connection;

import com.idega.util.database.PoolManager;
import com.idega.util.dbschema.SQLSchemaAdapter;
import com.idega.util.dbschema.SQLSchemaCreator;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/15 18:58:05 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class SlideSchemaCreator {
    
    public void createSchemas(){
        try {
            PoolManager pManager = PoolManager.getInstance();
            Connection conn = pManager.getConnection();
            String datastoreType = SQLSchemaAdapter.detectDataStoreType(conn);
            pManager.freeConnection(conn);
            createSchemas(datastoreType);
        } catch (Exception e) {
           
            e.printStackTrace();
        }
    }
	
	public void createSchemas(String dataStoreType) throws Exception{
		
		SQLSchemaAdapter dsi = SQLSchemaAdapter.getInstance(dataStoreType);
		SQLSchemaCreator tableCreator = dsi.getTableCreator();
		
		tableCreator.generateSchema(new UriSchema());
		tableCreator.generateSchema(new ObjectSchema());
		tableCreator.generateSchema(new BindingSchema());
		tableCreator.generateSchema(new ParentBindingSchema());
		tableCreator.generateSchema(new LinksSchema());
		tableCreator.generateSchema(new LocksSchema());
		tableCreator.generateSchema(new BranchSchema());
		tableCreator.generateSchema(new LabelSchema());
		tableCreator.generateSchema(new VersionSchema());
		tableCreator.generateSchema(new VersionHistorySchema());
		tableCreator.generateSchema(new VersionPredsSchema());
		tableCreator.generateSchema(new VersionLabelsSchema());
		tableCreator.generateSchema(new VersionContentSchema());
		tableCreator.generateSchema(new PropertiesSchema());
		tableCreator.generateSchema(new PermissionsSchema());
		
		
	}

}
