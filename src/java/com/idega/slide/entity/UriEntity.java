/*
 * Created on 22.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.entity;


/**
 * @author aron
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UriEntity extends SlideSchema {
	/*
	 
	 CREATE TABLE "URI" (
	"URI_ID" NUMBER(10) NOT NULL,
    	"URI_STRING" VARCHAR2(2500) NOT NULL,
	PRIMARY KEY("URI_ID"),
    	UNIQUE("URI_STRING")
    	) CACHE NOLOGGING; 
	 
	 */

	public UriEntity(){
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
		
		addColumn(uriID,true);
		addColumn(uriString);
		
		setHasAutoIncrementColumn(true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "URI";
	}
	
}