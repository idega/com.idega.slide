package com.idega.slide.schema;

import com.idega.util.dbschema.IndexImpl;

/**
 * 
 * 
 *  Last modified: $Date: 2007/02/04 20:42:22 $ by $Author: valdas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public class ObjectSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "OBJECT" (
	"URI_ID" NUMBER(10),
    	"CLASS_NAME" VARCHAR2(255) NOT NULL,
	PRIMARY KEY("URI_ID"),
    	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID")
    	) CACHE NOLOGGING; 
	 
	 */

	public ObjectSchema(){
		super();
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		uriID.setOneToManyEntity(new UriSchema());
		
		
		SlideSchemaColumn className = new SlideSchemaColumn(this);
		className.setDataTypeClass(String.class);
		className.setNullAllowed(false);
		className.setSQLFieldName("CLASS_NAME");
		
		
		addColumn(uriID,true);
		addColumn(className);
		
		//CREATE INDEX object_idx1 ON object(class_name,uri_id);
		IndexImpl index1 = new IndexImpl("object_idx1",getSQLName());
		index1.addField("CLASS_NAME");
		index1.addField("URI_ID");
		addIndex(index1);
		
	
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "OBJECT";
	}
}
