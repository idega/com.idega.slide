package com.idega.slide.entity;
/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class ParentBindingSchema extends SlideSchema {

	/*
	 CREATE TABLE "PARENT_BINDING" (
	"URI_ID" NUMBER(10) NOT NULL,
	"NAME" VARCHAR2(512) NOT NULL,
    	"PARENT_UURI_ID" NUMBER(10) NOT NULL,
	PRIMARY KEY("URI_ID", "NAME", "PARENT_UURI_ID"),
	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("PARENT_UURI_ID") REFERENCES "URI"("URI_ID")
		) CACHE NOLOGGING;
	 */
	
	public ParentBindingSchema(){
		super();
		
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		uriID.setOneToManyEntity(new UriEntity());
		
		SlideSchemaColumn parentUriID = new SlideSchemaColumn(this);
		parentUriID.setDataTypeClass(Integer.class);
		parentUriID.setMaxLength(10);
		parentUriID.setPartOfPrimaryKey(true);
		parentUriID.setNullAllowed(false);
		parentUriID.setSQLFieldName("PARENT_UURI_ID");
		parentUriID.setOneToManyEntity(new UriEntity());
		
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setNullAllowed(false);
		name.setSQLFieldName("NAME");
		
		addColumn(uriID,true);
		addColumn(name,true);
		addColumn(parentUriID,true);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "PARENT_BINDING";
	}
}
