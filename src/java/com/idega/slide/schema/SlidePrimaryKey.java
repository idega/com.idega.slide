/*
 * Created on 23.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.schema;

import java.util.ArrayList;
import java.util.List;

import com.idega.util.dbschema.CompositePrimaryKeyException;
import com.idega.util.dbschema.PrimaryKey;
import com.idega.util.dbschema.SchemaColumn;

/**
 * @author aron
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SlidePrimaryKey implements PrimaryKey {
	
	private List fields = new ArrayList();
	private boolean composite = false;
	
	public SlidePrimaryKey(){
		
	}
	
	public SlidePrimaryKey(SlideSchemaColumn field ){
		addField(field);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.PrimaryKeyDefinition#getFields()
	 */
	public SchemaColumn[] getColumns() {
		return (SchemaColumn[]) fields.toArray(new SlideSchemaColumn[0]);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.PrimaryKeyDefinition#getField()
	 */
	public SchemaColumn getColumn() throws CompositePrimaryKeyException {
		if(fields.size()==1)
			return (SlideSchemaColumn)fields.get(0);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.PrimaryKeyDefinition#isComposite()
	 */
	public boolean isComposite() {
		return composite;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.PrimaryKeyDefinition#getPrimaryKeyClass()
	 */
	public Class getPrimaryKeyClass() {
		if(fields.size()==1)
			return ((SlideSchemaColumn)fields.get(0)).getDataTypeClass();
		return null;
	}
	
	protected void addField(SlideSchemaColumn field){
		fields.add(field);
		if(fields.size()>1){
			composite = true;
		}
			
	}
	
	

}
