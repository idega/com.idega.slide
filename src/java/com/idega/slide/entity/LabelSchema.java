package com.idega.slide.entity;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
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
