package com.idega.slide.schema;

import com.idega.util.dbschema.IndexImpl;

/**
 * 
 * 
 *  Last modified: $Date: 2006/04/11 15:12:46 $ by $Author: eiki $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public class BindingSchema extends SlideSchema {
	/*
	 
	 CREATE TABLE "BINDING" (
	"URI_ID" NUMBER(10) NOT NULL,
	"NAME" VARCHAR2(512) NOT NULL,
	"CHILD_UURI_ID" NUMBER(10) NOT NULL,
	PRIMARY KEY("URI_ID", "NAME", "CHILD_UURI_ID"),
	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("CHILD_UURI_ID") REFERENCES "URI"("URI_ID")
	) CACHE NOLOGGING;
	 */
	public BindingSchema(){
		super();
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		uriID.setOneToManyEntity(new UriSchema());
		
		SlideSchemaColumn childUriID = new SlideSchemaColumn(this);
		childUriID.setDataTypeClass(Integer.class);
		childUriID.setMaxLength(10);
		childUriID.setPartOfPrimaryKey(true);
		childUriID.setNullAllowed(false);
		childUriID.setSQLFieldName("CHILD_UURI_ID");
		childUriID.setOneToManyEntity(new UriSchema());
		
		
		SlideSchemaColumn name = new SlideSchemaColumn(this);
		name.setDataTypeClass(String.class);
		name.setNullAllowed(false);
		name.setSQLFieldName("NAME");
		name.setMaxLength(4000);
		
		addColumn(uriID,true);
		addColumn(name,true);
		addColumn(childUriID,true);
		
//		CREATE INDEX binding_idx1 ON binding(name);
//		CREATE INDEX binding_idx2 ON binding(uri_id,child_uuri_id);
		
		IndexImpl index1 = new IndexImpl("binding_idx1",getSQLName());
		index1.addField("NAME");
		addIndex(index1);
		
		IndexImpl index2 = new IndexImpl("binding_idx2", getSQLName());
		index2.addField("URI_ID");
		index2.addField("CHILD_UURI_ID");
		addIndex(index2);
		
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "BINDING";
	}

}
