package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2005/01/20 14:09:44 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class LabelSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "LABEL" (
	"LABEL_ID" NUMBER(10) NOT NULL, 
    	"LABEL_STRING" VARCHAR2(512) NOT NULL, 
	PRIMARY KEY("LABEL_ID")
	)CACHE NOLOGGING;

	 */
	
	public LabelSchema(){
		super();
		
		SlideSchemaColumn labelID = new SlideSchemaColumn(this);
		labelID.setDataTypeClass(Integer.class);
		labelID.setMaxLength(10);
		labelID.setNullAllowed(false);
		labelID.setPartOfPrimaryKey(true);
		labelID.setSQLFieldName("LABEL_ID");
		
		SlideSchemaColumn labelString = new SlideSchemaColumn(this);
		labelString.setDataTypeClass(String.class);
		labelString.setNullAllowed(false);
		labelString.setSQLFieldName("LABEL_STRING");
		labelString.setMaxLength(4000);
		
		addColumn(labelID,true);
		addColumn(labelString);
		
		setHasAutoIncrementColumn(true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "LABEL";
	}

}
