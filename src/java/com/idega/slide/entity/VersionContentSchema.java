package com.idega.slide.entity;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class VersionContentSchema extends SlideSchema {
	
	
	/*
	 CREATE TABLE "VERSION_CONTENT" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"CONTENT" BLOB, 
	PRIMARY KEY("VERSION_ID"), 
	FOREIGN KEY("VERSION_ID") REFERENCES "VERSION_HISTORY"("VERSION_ID")
	) CACHE NOLOGGING
	LOB ("CONTENT") STORE AS (NOCACHE NOLOGGING STORAGE(MAXEXTENTS UNLIMITED));
	 */
	
	public VersionContentSchema(){
		super();
		
		SlideSchemaColumn versionID = new SlideSchemaColumn(this);
		versionID.setDataTypeClass(Integer.class);
		versionID.setMaxLength(10);
		versionID.setSQLFieldName("VERSION_ID");
		versionID.setNullAllowed(false);
		versionID.setPartOfPrimaryKey(true);
		versionID.setOneToManyEntity(new VersionHistorySchema());
		
		SlideSchemaColumn content = new SlideSchemaColumn(this);
		content.setDataTypeClass(java.sql.Blob.class);
		content.setSQLFieldName("CONTENT");
		
		addColumn(versionID,true);
		addColumn(content);
		
	}

	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "VERSION_CONTENT";
	}
}
