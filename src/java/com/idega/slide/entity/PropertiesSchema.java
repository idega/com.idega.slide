package com.idega.slide.entity;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class PropertiesSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "PROPERTIES" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"PROPERTY_NAMESPACE" VARCHAR2(50) NOT NULL, 
	"PROPERTY_NAME" VARCHAR2(50) NOT NULL, 
	"PROPERTY_VALUE" VARCHAR2(255), 
	"PROPERTY_TYPE" VARCHAR2(50),
	"IS_PROTECTED" NUMBER(1) NOT NULL, 
	UNIQUE("VERSION_ID", "PROPERTY_NAMESPACE", "PROPERTY_NAME"), 
	FOREIGN KEY("VERSION_ID") REFERENCES "VERSION_HISTORY"("VERSION_ID")
) CACHE NOLOGGING;

	 */
	
	public PropertiesSchema(){
		super();
		
		SlideSchemaColumn versionID = new SlideSchemaColumn(this);
		versionID.setDataTypeClass(Integer.class);
		versionID.setMaxLength(10);
		versionID.setSQLFieldName("VERSION_ID");
		versionID.setNullAllowed(false);
		versionID.setPartOfPrimaryKey(true);
		versionID.setOneToManyEntity(new VersionHistorySchema());
		
		SlideSchemaColumn nameSpace = new SlideSchemaColumn(this);
		nameSpace.setDataTypeClass(String.class);
		nameSpace.setMaxLength(50);
		nameSpace.setSQLFieldName("PROPERTY_NAMESPACE");
		nameSpace.setNullAllowed(false);
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setMaxLength(50);
		name.setSQLFieldName("PROPERTY_NAME");
		name.setNullAllowed(false);
		
		SlideSchemaColumn value = new SlideSchemaColumn(this);
		value.setDataTypeClass(String.class);
		value.setMaxLength(255);
		value.setSQLFieldName("PROPERTY_VALUE");
		value.setNullAllowed(true);
		
		SlideSchemaColumn type = new SlideSchemaColumn(this);
		type.setDataTypeClass(String.class);
		type.setMaxLength(50);
		type.setSQLFieldName("PROPERTY_TYPE");
		type.setNullAllowed(true);
		
		SlideSchemaColumn isProtected = new SlideSchemaColumn(this);
		isProtected.setDataTypeClass(Integer.class);
		isProtected.setMaxLength(1);
		isProtected.setSQLFieldName("IS_PROTECTED");
		isProtected.setNullAllowed(false);
		
		SlideUniqueKey uniqueKey = new SlideUniqueKey();
		uniqueKey.addField(versionID);
		uniqueKey.addField(nameSpace);
		uniqueKey.addField(name);
		
		addColumn(versionID);
		addColumn(nameSpace);
		addColumn(name);
		addColumn(value);
		addColumn(type);
		addColumn(isProtected);
		
		addUniqueKey(uniqueKey);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "PROPERTIES";
	}

}
