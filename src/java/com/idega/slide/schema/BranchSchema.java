package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class BranchSchema extends SlideSchema {
	/*
	 * CREATE TABLE "BRANCH" (
	"BRANCH_ID" NUMBER(10) NOT NULL, 
    	"BRANCH_STRING" VARCHAR2(512) NOT NULL, 
	PRIMARY KEY("BRANCH_ID"), 
	UNIQUE("BRANCH_STRING")
	) CACHE NOLOGGING;
	
	 */
	
	public BranchSchema(){
		super();
		
		SlideSchemaColumn branchID = new SlideSchemaColumn(this);
		branchID.setDataTypeClass(Integer.class);
		branchID.setMaxLength(10);
		branchID.setPartOfPrimaryKey(true);
		branchID.setNullAllowed(false);
		branchID.setSQLFieldName("BRANCH_ID");
		
		SlideSchemaColumn branchString = new SlideSchemaColumn(this);
		branchString.setDataTypeClass(String.class);
		branchString.setNullAllowed(false);
		branchString.setSQLFieldName("BRANCH_STRING");
		
		addColumn(branchID,true);
		addColumn(branchString);
		
		setHasAutoIncrementColumn(true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "BRANCH";
	}
	
}
