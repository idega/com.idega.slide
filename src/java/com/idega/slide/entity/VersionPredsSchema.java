package com.idega.slide.entity;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class VersionPredsSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "VERSION_PREDS" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"PREDECESSOR_ID" NUMBER(10) NOT NULL, 
	FOREIGN KEY("VERSION_ID") REFERENCES "VERSION_HISTORY"("VERSION_ID"), 
	FOREIGN KEY("PREDECESSOR_ID") REFERENCES "VERSION_HISTORY"("VERSION_ID"), 
    	UNIQUE("VERSION_ID", "PREDECESSOR_ID")
    	) CACHE NOLOGGING;

	 */
	
	public VersionPredsSchema(){
		super();
		
		VersionHistorySchema versionHistory = new VersionHistorySchema();
		
		SlideSchemaColumn versionID = new SlideSchemaColumn(this);
		versionID.setDataTypeClass(Integer.class);
		versionID.setMaxLength(10);
		versionID.setSQLFieldName("VERSION_ID");
		versionID.setNullAllowed(false);
		versionID.setOneToManyEntity(versionHistory);
		
		SlideSchemaColumn preDecessorID = new SlideSchemaColumn(this);
		preDecessorID.setDataTypeClass(Integer.class);
		preDecessorID.setMaxLength(10);
		preDecessorID.setSQLFieldName("PREDECESSOR_ID");
		preDecessorID.setNullAllowed(false);
		preDecessorID.setOneToManyEntity(versionHistory);
		
		SlideUniqueKey uniqueKey = new SlideUniqueKey();
		uniqueKey.addField(versionID);
		uniqueKey.addField(preDecessorID);
		
		addColumn(versionID);
		addColumn(preDecessorID);
		addUniqueKey(uniqueKey);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "VERSION_PREDS";
	}

}
