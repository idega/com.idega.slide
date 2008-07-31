package com.idega.slide.jcr;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

import org.apache.slide.content.NodeProperty;

public class SlideProperty implements Property {

	
	SlideNode slideNode;
	NodeProperty nodeProperty;
	SlidePropertyValue slidePropertyValue;
	String name;
	boolean isnew=false;
	
	public SlideProperty(SlideNode slideNode, String propertyName) {
		this.slideNode=slideNode;
		this.name=propertyName;
		this.setNew(true);
	}
	
	public SlideProperty(SlideNode slideNode, NodeProperty nodeProp) {
		this.slideNode=slideNode;
		this.nodeProperty=nodeProp;
		this.name=this.nodeProperty.getName();
	}

	public SlideProperty(SlideNode slideNode, String propertyName, SlidePropertyValue value) {
		this.slideNode=slideNode;
		this.name=propertyName;
		this.slidePropertyValue=value;
	}
	
	public boolean getBoolean() throws ValueFormatException,
			RepositoryException {
		return getValue().getBoolean();
	}

	public Calendar getDate() throws ValueFormatException, RepositoryException {
		return getValue().getDate();
	}

	public PropertyDefinition getDefinition() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble() throws ValueFormatException, RepositoryException {
		return getValue().getDouble();
	}

	public long getLength() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return -1;
	}

	public long[] getLengths() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLong() throws ValueFormatException, RepositoryException {
		return getValue().getLong();
	}

	public Node getNode() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return getSlideNode();
	}

	public InputStream getStream() throws ValueFormatException,
			RepositoryException {
		return getValue().getStream();
	}

	public String getString() throws ValueFormatException, RepositoryException {
		return getValue().getString();
	}

	public int getType() throws RepositoryException {
		// TODO Auto-generated method stub
		return getValue().getType();
	}

	public Value getValue() throws ValueFormatException, RepositoryException {
		if(slidePropertyValue==null){
			this.slidePropertyValue=new SlidePropertyValue(this);
		}
		return slidePropertyValue;
	}

	public Value[] getValues() throws ValueFormatException, RepositoryException {
		Value[] values = {getValue()};
		return values;
	}

	public void setValue(Value value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value.getString()));
	}

	public void setValue(Value[] value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		//slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value.getString()));
		throw new UnsupportedOperationException("Multiple values not supported");
	}

	public void setValue(String value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value));

	}

	public void setValue(String[] arg0) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw new UnsupportedOperationException("Multiple values not supported");
	}

	public void setValue(InputStream arg0) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw new UnsupportedOperationException("Binary values not supported");
	}

	public void setValue(long value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value));
	}

	public void setValue(double value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value));
	}

	public void setValue(Calendar value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value));

	}

	public void setValue(boolean value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value));

	}

	public void setValue(Node value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		slidePropertyValue= new SlidePropertyValue(this,new NodeProperty(this.name,value.getPath()));

	}

	public void accept(ItemVisitor value) throws RepositoryException {
		throw new UnsupportedOperationException("ItemVisitor values not supported");
	}

	public Item getAncestor(int arg0) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		return getNode();
	}

	public int getDepth() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() throws RepositoryException {
		return this.name;
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return this.getNode();
	}

	public String getPath() throws RepositoryException {
		// TODO Auto-generated method stub
		return getNode()+"/"+getName();
	}

	public Session getSession() throws RepositoryException {
		return this.getNode().getSession();
	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return isnew;
	}

	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSame(Item arg0) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh(boolean arg0) throws InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		this.getSlideNode().revisionDescriptor.removeProperty(getName());
		this.getSlideNode().properties.remove(this);
	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setNew(boolean isnew){
		this.isnew=isnew;
	}
	
	public SlideNode getSlideNode(){
		return this.slideNode;
	}
	
	public SlidePropertyValue getSlidePropertyValue(){
		return this.slidePropertyValue;
	}
	
}
