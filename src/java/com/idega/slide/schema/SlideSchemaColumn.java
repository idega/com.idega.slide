/*
 * Created on 23.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.schema;

import com.idega.util.dbschema.Schema;
import com.idega.util.dbschema.SchemaColumn;


/**
 * 
 * 
 *  Last modified: $Date: 2006/02/22 22:07:52 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class SlideSchemaColumn implements SchemaColumn {
	
	private String SQLName;
	private Class dataTypeClass;
	private int maxLength = -1;
	private boolean nullAllowed = true;
	private boolean partOfPrimaryKey = false;
	private boolean unique = false;
	private SlideSchema declaredEntity;
	private SlideSchema oneToManyEntity;
	
	public SlideSchemaColumn(SlideSchema declaredEntity){
		this.declaredEntity = declaredEntity;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getDeclaredEntity()
	 */
	public Schema getSchema() {
		return declaredEntity;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getUniqueFieldName()
	 */
	public String getUniqueName() {
		return getSQLName();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getSQLFieldName()
	 */
	public String getSQLName() {
		return SQLName;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getDataTypeClass()
	 */
	public Class getDataTypeClass() {
		return dataTypeClass;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#isNullAllowed()
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#isPartOfPrimaryKey()
	 */
	public boolean isPartOfPrimaryKey() {
		return partOfPrimaryKey;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#isUnique()
	 */
	public boolean isUnique() {
		return unique;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getMaxLength()
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#isPartOfManyToOneRelationship()
	 */
	public boolean isPartOfManyToOneRelationship() {
		return oneToManyEntity!=null;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.EntityField#getManyToOneRelated()
	 */
	public Schema getManyToOneRelated() {
		return oneToManyEntity;
	}

	/**
	 * @param dataTypeClass The dataTypeClass to set.
	 */
	public void setDataTypeClass(Class dataTypeClass) {
		this.dataTypeClass = dataTypeClass;
	}
	/**
	 * @param declaredEntity The declaredEntity to set.
	 */
	public void setDeclaredEntity(SlideSchema declaredEntity) {
		this.declaredEntity = declaredEntity;
	}
	/**
	 * @param maxLength The maxLength to set.
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	/**
	 * @param nullAllowed The nullAllowed to set.
	 */
	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}
	/**
	 * @param partOfPrimaryKey The partOfPrimaryKey to set.
	 */
	public void setPartOfPrimaryKey(boolean partOfPrimaryKey) {
		this.partOfPrimaryKey = partOfPrimaryKey;
	}
	/**
	 * @param fieldName The sQLFieldName to set.
	 */
	public void setSQLFieldName(String fieldName) {
		SQLName = fieldName;
	}
	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	/**
	 * @param uniqueFieldName The uniqueFieldName to set.
	 */
	public void setUniqueFieldName(String uniqueFieldName) {
	}
	
	public void setOneToManyEntity(SlideSchema relatedEntity){
		this.oneToManyEntity = relatedEntity;
	}
}
