package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2005/01/20 14:09:44 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class LocksSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "LOCKS" (
	"LOCK_ID" NUMBER(10) NOT NULL, 
    	"OBJECT_ID" NUMBER(10) NOT NULL, 
	"SUBJECT_ID" NUMBER(10) NOT NULL, 
	"TYPE_ID" NUMBER(10) NOT NULL, 
	"EXPIRATION_DATE" NUMBER(14) NOT NULL,
	"IS_INHERITABLE" NUMBER(1) NOT NULL, 
    	"IS_EXCLUSIVE" NUMBER(1) NOT NULL, 
	"OWNER" VARCHAR2(512), 
	PRIMARY KEY("LOCK_ID"), 
	FOREIGN KEY("LOCK_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("OBJECT_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("SUBJECT_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("TYPE_ID") REFERENCES "URI"("URI_ID")
) CACHE NOLOGGING;

	 */
	
	public LocksSchema(){
		super();
		
		UriSchema uriEntity = new UriSchema();
		
		SlideSchemaColumn lockID = new SlideSchemaColumn(this);
		lockID.setDataTypeClass(Integer.class);
		lockID.setMaxLength(10);
		lockID.setPartOfPrimaryKey(true);
		lockID.setNullAllowed(false);
		lockID.setSQLFieldName("LOCK_ID");
		lockID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn objectID = new SlideSchemaColumn(this);
		objectID.setDataTypeClass(Integer.class);
		objectID.setMaxLength(10);
		objectID.setNullAllowed(false);
		objectID.setSQLFieldName("OBJECT_ID");
		objectID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn subjectID = new SlideSchemaColumn(this);
		subjectID.setDataTypeClass(Integer.class);
		subjectID.setMaxLength(10);
		subjectID.setNullAllowed(false);
		subjectID.setSQLFieldName("SUBJECT_ID");
		subjectID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn typeID = new SlideSchemaColumn(this);
		typeID.setDataTypeClass(Integer.class);
		typeID.setMaxLength(10);
		typeID.setNullAllowed(false);
		typeID.setSQLFieldName("TYPE_ID");
		typeID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn expirationDate = new SlideSchemaColumn(this);
		expirationDate.setDataTypeClass(Integer.class);
		expirationDate.setMaxLength(14);
		expirationDate.setSQLFieldName("EXPIRATION_DATE");
		expirationDate.setNullAllowed(false);
		
		SlideSchemaColumn isInheritable = new SlideSchemaColumn(this);
		isInheritable.setDataTypeClass(Integer.class);
		isInheritable.setMaxLength(1);
		isInheritable.setSQLFieldName("IS_INHERITABLE");
		isInheritable.setNullAllowed(false);
		
		SlideSchemaColumn isExlusive = new SlideSchemaColumn(this);
		isExlusive.setDataTypeClass(Integer.class);
		isExlusive.setMaxLength(1);
		isExlusive.setSQLFieldName("IS_EXCLUSIVE");
		isExlusive.setNullAllowed(false);
		
		SlideSchemaColumn owner = new SlideSchemaColumn(this);
		owner.setDataTypeClass(String.class);
		//isExlusive.setMaxLength(1);
		owner.setSQLFieldName("OWNER");
		owner.setNullAllowed(true);
		owner.setMaxLength(4000);
		
		addColumn(lockID,true);
		addColumn(objectID);
		addColumn(subjectID);
		addColumn(typeID);
		addColumn(expirationDate);
		addColumn(isInheritable);
		addColumn(isExlusive);
		addColumn(owner);
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "LOCKS";
	}

}
