package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class VersionLabelsSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "VERSION_LABELS" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"LABEL_ID" NUMBER(10) NOT NULL, 
	UNIQUE("VERSION_ID", "LABEL_ID"), 
	FOREIGN KEY("VERSION_ID") REFERENCES "VERSION_HISTORY"("VERSION_ID"), 
	FOREIGN KEY("LABEL_ID") REFERENCES "LABEL"("LABEL_ID")
	) CACHE NOLOGGING;
	 */
	
	public VersionLabelsSchema(){
		super();
		
		SlideSchemaColumn versionID = new SlideSchemaColumn(this);
		versionID.setDataTypeClass(Integer.class);
		versionID.setMaxLength(10);
		versionID.setSQLFieldName("VERSION_ID");
		versionID.setNullAllowed(false);
		versionID.setOneToManyEntity(new VersionHistorySchema());
		
		SlideSchemaColumn labelID = new SlideSchemaColumn(this);
		labelID.setDataTypeClass(Integer.class);
		labelID.setMaxLength(10);
		labelID.setSQLFieldName("LABEL_ID");
		labelID.setNullAllowed(false);
		labelID.setOneToManyEntity(new LabelSchema());
		 
		SlideUniqueKey uniqueKey = new SlideUniqueKey();
		uniqueKey.addField(versionID);
		uniqueKey.addField(labelID);
		
		addColumn(versionID);
		addColumn(labelID);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "VERSION_LABELS";
	}

}
