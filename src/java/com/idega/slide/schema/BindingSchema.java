package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2005/01/20 14:09:44 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class BindingSchema extends SlideSchema {
	/*
	 
	 CREATE TABLE "BINDING" (
	"URI_ID" NUMBER(10) NOT NULL,
	"NAME" VARCHAR2(512) NOT NULL,
	"CHILD_UURI_ID" NUMBER(10) NOT NULL,
	PRIMARY KEY("URI_ID", "NAME", "CHILD_UURI_ID"),
	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("CHILD_UURI_ID") REFERENCES "URI"("URI_ID")
	) CACHE NOLOGGING;
	 */
	public BindingSchema(){
		super();
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		uriID.setOneToManyEntity(new UriSchema());
		
		SlideSchemaColumn childUriID = new SlideSchemaColumn(this);
		childUriID.setDataTypeClass(Integer.class);
		childUriID.setMaxLength(10);
		childUriID.setPartOfPrimaryKey(true);
		childUriID.setNullAllowed(false);
		childUriID.setSQLFieldName("CHILD_UURI_ID");
		childUriID.setOneToManyEntity(new UriSchema());
		
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setNullAllowed(false);
		name.setSQLFieldName("NAME");
		name.setMaxLength(4000);
		
		addColumn(uriID,true);
		addColumn(name,true);
		addColumn(childUriID,true);
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "BINDING";
	}

}
