package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class PermissionsSchema extends SlideSchema {
	/*
	CREATE TABLE "PERMISSIONS" (
			"OBJECT_ID" NUMBER(10) NOT NULL, 
			"SUBJECT_ID" NUMBER(10) NOT NULL, 
			"ACTION_ID" NUMBER(10) NOT NULL, 
			"VERSION_NO" VARCHAR2(20), 
			"IS_INHERITABLE" NUMBER(1) NOT NULL, 
			"IS_NEGATIVE" NUMBER(1) NOT NULL, 
			"SUCCESSION" NUMBER(10) NOT NULL, 
			FOREIGN KEY("OBJECT_ID") REFERENCES "URI"("URI_ID"), 
			FOREIGN KEY("SUBJECT_ID") REFERENCES "URI"("URI_ID"), 
			FOREIGN KEY("ACTION_ID") REFERENCES "URI"("URI_ID"), 
			UNIQUE("OBJECT_ID", "SUBJECT_ID", "ACTION_ID"), 
			UNIQUE("OBJECT_ID", "SUCCESSION")
		) CACHE NOLOGGING;
		*/
	
	public PermissionsSchema(){
		super();
		
		UriSchema uriEntity = new UriSchema();
		
		SlideSchemaColumn objectID = new SlideSchemaColumn(this);
		objectID.setDataTypeClass(Integer.class);
		objectID.setMaxLength(10);
		objectID.setSQLFieldName("OBJECT_ID");
		objectID.setNullAllowed(false);
		objectID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn subjectID = new SlideSchemaColumn(this);
		subjectID.setDataTypeClass(Integer.class);
		subjectID.setMaxLength(10);
		subjectID.setSQLFieldName("SUBJECT_ID");
		subjectID.setNullAllowed(false);
		subjectID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn actionID = new SlideSchemaColumn(this);
		actionID.setDataTypeClass(Integer.class);
		actionID.setMaxLength(10);
		actionID.setSQLFieldName("ACTION_ID");
		actionID.setNullAllowed(false);
		actionID.setOneToManyEntity(uriEntity);
		
		SlideSchemaColumn versionNo = new SlideSchemaColumn(this);
		versionNo.setDataTypeClass(String.class);
		versionNo.setMaxLength(20);
		versionNo.setSQLFieldName("VERSION_NO");
		versionNo.setNullAllowed(true);
		
		SlideSchemaColumn isInheritable = new SlideSchemaColumn(this);
		isInheritable.setDataTypeClass(Integer.class);
		isInheritable.setMaxLength(1);
		isInheritable.setSQLFieldName("IS_INHERITABLE");
		isInheritable.setNullAllowed(false);
		
		SlideSchemaColumn isNegative = new SlideSchemaColumn(this);
		isNegative.setDataTypeClass(Integer.class);
		isNegative.setMaxLength(1);
		isNegative.setSQLFieldName("IS_NEGATIVE");
		isNegative.setNullAllowed(false);
		
		SlideSchemaColumn succession = new SlideSchemaColumn(this);
		succession.setDataTypeClass(Integer.class);
		succession.setMaxLength(10);
		succession.setSQLFieldName("SUCCESSION");
		succession.setNullAllowed(false);
		
		
		SlideUniqueKey uniqueKey1 = new SlideUniqueKey();
		uniqueKey1.addField(objectID);
		uniqueKey1.addField(subjectID);
		uniqueKey1.addField(actionID);
		
		SlideUniqueKey uniqueKey2 =new SlideUniqueKey();
		uniqueKey2.addField(objectID );
		uniqueKey2.addField(succession);
		
		addColumn(objectID);
		addColumn(subjectID);
		addColumn(actionID);
		addColumn(versionNo);
		addColumn(isInheritable);
		addColumn(isNegative);
		addColumn(succession);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "PERMISSIONS";
	}

}
