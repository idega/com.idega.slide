package com.idega.slide.schema;

import java.util.ArrayList;
import java.util.List;

import com.idega.util.dbschema.Index;
import com.idega.util.dbschema.PrimaryKey;
import com.idega.util.dbschema.Schema;
import com.idega.util.dbschema.SchemaColumn;
import com.idega.util.dbschema.UniqueKey;

/**
 * 
 * 
 *  Last modified: $Date: 2006/04/26 12:19:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.4 $
 */
public abstract class SlideSchema implements Schema {
	
	private String SQLTableName;
	private List columns = new ArrayList();
	private SlidePrimaryKey primaryKey = new SlidePrimaryKey();
	private List indexes = new ArrayList();
	private boolean autoIncrementColumn;
	private List uniqueKeys = new ArrayList();

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getUniqueEntityName()
	 */
	public String getUniqueName() {
		return getSQLName();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getSQLTableName()
	 */
	public String getSQLName() {
		return this.SQLTableName;
	}


	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getFields()
	 */
	public SchemaColumn[] getColumns() {
		return (SchemaColumn[]) this.columns.toArray(new SlideSchemaColumn[0]);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getPrimaryKeyDefinition()
	 */
	public PrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getInterfaceClass()
	 */
	public Class getInterfaceClass() {
		return null;
	}


	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getIndexes()
	 */
	public Index[] getIndexes()  {
		return (Index[])this.indexes.toArray(new Index[0]);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#hasAutoIncrementColumn()
	 */
	public boolean hasAutoIncrementColumn() {
		return this.autoIncrementColumn;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#setHasAutoIncrementColumn(boolean)
	 */
	public void setHasAutoIncrementColumn(boolean autoIncrementColumn) {
		this.autoIncrementColumn = autoIncrementColumn;

	}

	
	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityDefinition#getUniqueKeys()
	 */
	public UniqueKey[] getUniqueKeys() {
		return (UniqueKey[])this.uniqueKeys.toArray(new UniqueKey[0]);
	}
	
	public void addUniqueKey(SlideUniqueKey key){
		this.uniqueKeys.add(key);
	}
	
	protected void addColumn(SlideSchemaColumn column){
		this.columns.add(column);
	}
	
	protected void addColumn(SlideSchemaColumn column, boolean partOfPrimaryKey){
		this.columns.add(column);
		column.setPartOfPrimaryKey(true);
		this.primaryKey.addField(column);
		
	}
	
	protected void setPrimaryKey(SlidePrimaryKey primaryKey){
		this.primaryKey = primaryKey;
	}
	
	protected void addIndex(Index index) {
		indexes.add(index);
	}
	

}
