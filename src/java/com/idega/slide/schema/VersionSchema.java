package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class VersionSchema extends SlideSchema {
	/*
	 CREATE TABLE "VERSION" (
	"URI_ID" NUMBER(10) NOT NULL, 
    	"IS_VERSIONED" NUMBER(1) NOT NULL, 
	PRIMARY KEY("URI_ID"), 
    	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID")
    	) CACHE NOLOGGING;
	 */
	
	public VersionSchema(){
		super();
		
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setSQLFieldName("URI_ID");
		uriID.setNullAllowed(false);
		uriID.setPartOfPrimaryKey(true);
		uriID.setOneToManyEntity(new UriSchema());
		
		SlideSchemaColumn isVersioned = new SlideSchemaColumn(this);
		isVersioned.setDataTypeClass(Integer.class);
		isVersioned.setMaxLength(1);
		isVersioned.setSQLFieldName("IS_VERSIONED");
		isVersioned.setNullAllowed(false);
		
		addColumn(uriID,true);
		addColumn(isVersioned);
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "VERSION";
	}

}
