package com.idega.slide.entity;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
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
		uriID.setOneToManyEntity(new UriEntity());
		
		SlideSchemaColumn childUriID = new SlideSchemaColumn(this);
		childUriID.setDataTypeClass(Integer.class);
		childUriID.setMaxLength(10);
		childUriID.setPartOfPrimaryKey(true);
		childUriID.setNullAllowed(false);
		childUriID.setSQLFieldName("CHILD_UURI_ID");
		childUriID.setOneToManyEntity(new UriEntity());
		
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setNullAllowed(false);
		name.setSQLFieldName("NAME");
		
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
