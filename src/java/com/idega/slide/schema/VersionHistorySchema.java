package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class VersionHistorySchema extends SlideSchema {
	
	/*
	 CREATE TABLE "VERSION_HISTORY" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"URI_ID" NUMBER(10) NOT NULL, 
	"BRANCH_ID" NUMBER(10) NOT NULL, 
	"REVISION_NO" VARCHAR2(20) NOT NULL, 
    	PRIMARY KEY("VERSION_ID"), 
	UNIQUE("URI_ID", "BRANCH_ID", "REVISION_NO"), 
	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID"), 
	FOREIGN KEY("BRANCH_ID") REFERENCES "BRANCH"("BRANCH_ID")
	) CACHE NOLOGGING;
	 */
	
	protected VersionHistorySchema(){
		super();
		
		SlideSchemaColumn versionID = new SlideSchemaColumn(this);
		versionID.setDataTypeClass(Integer.class);
		versionID.setMaxLength(10);
		versionID.setSQLFieldName("VERSION_ID");
		versionID.setNullAllowed(false);
		versionID.setPartOfPrimaryKey(true);
		
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setSQLFieldName("URI_ID");
		uriID.setNullAllowed(false);
		uriID.setOneToManyEntity(new UriSchema());
		
		SlideSchemaColumn branchID = new SlideSchemaColumn(this);
		branchID.setDataTypeClass(Integer.class);
		branchID.setMaxLength(10);
		branchID.setSQLFieldName("BRANCH_ID");
		branchID.setNullAllowed(false);
		branchID.setOneToManyEntity(new BranchSchema());
		
		SlideSchemaColumn revisionNo = new SlideSchemaColumn(this);
		revisionNo.setDataTypeClass(String.class);
		revisionNo.setMaxLength(20);
		revisionNo.setSQLFieldName("REVISION_NO");
		revisionNo.setNullAllowed(false);
		
		SlideUniqueKey uniqueKey = new SlideUniqueKey();
		uniqueKey.addField(uriID);
		uniqueKey.addField(branchID);
		uniqueKey.addField(revisionNo);
		
		addColumn(versionID,true);
		addColumn(uriID);
		addColumn(branchID);
		addColumn(revisionNo);
		
		addUniqueKey(uniqueKey);
		
		setHasAutoIncrementColumn(true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "VERSION_HISTORY";
	}

}
