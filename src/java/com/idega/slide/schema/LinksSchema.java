package com.idega.slide.schema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/05 08:44:59 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class LinksSchema extends SlideSchema {
	
	/*
	 CREATE TABLE "LINKS" (
	"URI_ID" NUMBER(10) NOT NULL,
	"LINK_TO_ID" NUMBER(10) NOT NULL,
	PRIMARY KEY("URI_ID", "LINK_TO_ID"),
	FOREIGN KEY("URI_ID") REFERENCES "URI"("URI_ID"),
	FOREIGN KEY("LINK_TO_ID") REFERENCES "URI"("URI_ID")
	) CACHE NOLOGGING;
	 
	 */
	
	public LinksSchema(){
		super();
		
		SlideSchemaColumn uriID = new SlideSchemaColumn(this);
		uriID.setDataTypeClass(Integer.class);
		uriID.setMaxLength(10);
		uriID.setPartOfPrimaryKey(true);
		uriID.setNullAllowed(false);
		uriID.setSQLFieldName("URI_ID");
		uriID.setOneToManyEntity(new UriSchema());
		
		SlideSchemaColumn linkToID = new SlideSchemaColumn(this);
		linkToID.setDataTypeClass(Integer.class);
		linkToID.setMaxLength(10);
		linkToID.setPartOfPrimaryKey(true);
		linkToID.setNullAllowed(false);
		linkToID.setSQLFieldName("LINK_TO_ID");
		linkToID.setOneToManyEntity(new UriSchema());
		
		addColumn(uriID,true);
		addColumn(linkToID,true);
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.slide.entity.SlideEntity#getSQLTableName()
	 */
	public String getSQLName() {
		return "LINKS";
	}

}
