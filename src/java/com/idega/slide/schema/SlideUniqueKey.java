/*
 * Created on 26.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.schema;

import java.util.ArrayList;
import java.util.List;

import com.idega.util.dbschema.SchemaColumn;
import com.idega.util.dbschema.UniqueKey;

/**
 * @author aron
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SlideUniqueKey implements UniqueKey {
	
	private List fields = new ArrayList();

	/* (non-Javadoc)
	 * @see com.idega.data.store.UniqueKey#getFields()
	 */
	public SchemaColumn[] getFields() {
		return (SchemaColumn[]) fields.toArray(new SchemaColumn[0]);
	}
	
	public void addField(SlideSchemaColumn field){
		fields.add(field);
	}

}
