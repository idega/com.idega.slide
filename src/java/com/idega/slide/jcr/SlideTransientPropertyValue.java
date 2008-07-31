package com.idega.slide.jcr;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

public class SlideTransientPropertyValue implements Value {

	private String value;

	public SlideTransientPropertyValue(String value) {
		this.value=value;
	}

	public boolean getBoolean() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public Calendar getDate() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong() throws ValueFormatException, IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public InputStream getStream() throws IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return value;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return PropertyType.STRING;
	}

}
