package com.idega.slide.entity;

import com.idega.util.dbschema.SQLSchemaAdapter;
import com.idega.util.dbschema.SQLSchemaCreator;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class SlideSchemaCreator {
	
	public void createEntities(String dataStoreType) throws Exception{
		
		SQLSchemaAdapter dsi = SQLSchemaAdapter.getInstance(dataStoreType);
		SQLSchemaCreator tableCreator = dsi.getTableCreator();
		
		tableCreator.generateSchema(new UriEntity());
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
