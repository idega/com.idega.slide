package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
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
		
	
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "OBJECT";
	}
}
