package com.idega.slide.jcr;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;

public class SlidePropertyValue implements Value {

	private SlideProperty slideProperty;
	int type;

	public SlidePropertyValue(SlideProperty slideProperty) {
		this.slideProperty=slideProperty;
	}

	public SlidePropertyValue(SlideProperty slideProperty2,
			NodeProperty newProperty) {
		this.slideProperty=slideProperty2;
		this.slideProperty.nodeProperty=newProperty;
		SlideNode slideNode = this.slideProperty.getSlideNode();
		NodeRevisionDescriptor revisionDescriptor = slideNode.revisionDescriptor;
		revisionDescriptor.setProperty(newProperty);
	}

	public boolean getBoolean() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return (Boolean)slideProperty.nodeProperty.getValue();
	}

	public Calendar getDate() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return (Calendar)slideProperty.nodeProperty.getValue();
	}

	public double getDouble() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return (Double)slideProperty.nodeProperty.getValue();
	}

	public long getLong() throws ValueFormatException, IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return (Long)slideProperty.nodeProperty.getValue();
	}

	public InputStream getStream() throws IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return (InputStream)slideProperty.nodeProperty.getValue();
	}

	public String getString() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return slideProperty.nodeProperty.getValue().toString();
	}

	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
	public void setType(int type){
		this.type=type;
	}

}
