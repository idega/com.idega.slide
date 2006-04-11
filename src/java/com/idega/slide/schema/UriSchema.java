/*
 * Created on 22.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.schema;

import com.idega.util.dbschema.IndexImpl;


/**
 * @author aron
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UriSchema extends SlideSchema {
	/*
	 
	 CREATE TABLE "URI" (
	"URI_ID" NUMBER(10) NOT NULL,
    	"URI_STRING" VARCHAR2(4000) NOT NULL,
	PRIMARY KEY("URI_ID"),
    	UNIQUE("URI_STRING")
    	) CACHE NOLOGGING; 
	 
	 */

	public UriSchema(){
		super();
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		
		
		SlideSchemaColumn uriString = new SlideSchemaColumn(this);
		uriString.setDataTypeClass(String.class);
		uriString.setNullAllowed(false);
		uriString.setSQLFieldName("URI_STRING");
		uriString.setMaxLength(4000);
				
		addColumn(uriID,true);
		addColumn(uriString);
		
//		CREATE INDEX uri_idx1 ON uri(uri_string);
//		CREATE INDEX uri_idx2 ON uri(uri_id,uri_string);

		IndexImpl index1 = new IndexImpl("uri_idx1",getSQLName());
		index1.addField("URI_STRING");
		addIndex(index1);
		
		IndexImpl index2 = new IndexImpl("uri_idx2", getSQLName());
		index2.addField("URI_ID");
		index2.addField("URI_STRING");
		addIndex(index2);
		
		setHasAutoIncrementColumn(true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "URI";
	}
	
}