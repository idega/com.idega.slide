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

public class SlideTransientProperty implements Property {

	private SlideNode slideNode;
	private String name;
	private SlideTransientPropertyValue value;

	public SlideTransientProperty(SlideNode slideNode,
			String name, String value) {
		this.slideNode=slideNode;
		this.name=name;
		this.value=new SlideTransientPropertyValue(value);
	}

	public boolean getBoolean() throws ValueFormatException,
			RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public Calendar getDate() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyDefinition getDefinition() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLength() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long[] getLengths() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLong() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node getNode() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return this.slideNode;
	}

	public InputStream getStream() throws ValueFormatException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return getValue().getString();
	}

	public int getType() throws RepositoryException {
		// TODO Auto-generated method stub
		return getValue().getType();
	}

	public Value getValue() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return value;
	}

	public Value[] getValues() throws ValueFormatException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(Value value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(Value[] values) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(String value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(String[] values) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(InputStream value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(long value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(double value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(Calendar value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(boolean value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void setValue(Node value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void accept(ItemVisitor visitor) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDepth() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() throws RepositoryException {
		// TODO Auto-generated method stub
		return this.name;
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSame(Item otherItem) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		// TODO Auto-generated method stub

	}

}
