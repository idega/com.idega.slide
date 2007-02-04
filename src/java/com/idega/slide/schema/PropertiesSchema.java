package com.idega.slide.schema;

import com.idega.util.dbschema.IndexImpl;

/**
 * 
 * 
 *  Last modified: $Date: 2007/02/04 20:42:22 $ by $Author: valdas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public class PropertiesSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "PROPERTIES" (
	"VERSION_ID" NUMBER(10) NOT NULL, 
	"PROPERTY_NAMESPACE" VARCHAR2(50) NOT NULL, 
	"PROPERTY_NAME" VARCHAR2(255) NOT NULL, 
	"PROPERTY_VALUE" VARCHAR2(4000), 
	"PROPERTY_TYPE" VARCHAR2(255),
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
		nameSpace.setMaxLength(255);
		nameSpace.setSQLFieldName("PROPERTY_NAMESPACE");
		nameSpace.setNullAllowed(false);
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setMaxLength(255);
		name.setSQLFieldName("PROPERTY_NAME");
		name.setNullAllowed(false);
		
		SlideSchemaColumn value = new SlideSchemaColumn(this);
		value.setDataTypeClass(String.class);
		value.setUnlimitedLength();
		value.setSQLFieldName("PROPERTY_VALUE");
		value.setNullAllowed(true);
		
		SlideSchemaColumn type = new SlideSchemaColumn(this);
		type.setDataTypeClass(String.class);
		type.setMaxLength(255);
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
		
//		CREATE INDEX properties_idx1 ON properties(version_id,property_name);
//		CREATE INDEX properties_idx2 ON properties(version_id,property_namespace,property_name);
		
		IndexImpl index1 = new IndexImpl("properties_idx1",getSQLName());
		index1.addField("VERSION_ID");
		index1.addField("PROPERTY_NAME");
		addIndex(index1);
		
		IndexImpl index2 = new IndexImpl("properties_idx2", getSQLName());
		index2.addField("VERSION_ID");
		index2.addField("PROPERTY_NAMESPACE");
		index2.addField("PROPERTY_NAME");
		addIndex(index2);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "PROPERTIES";
	}

}
