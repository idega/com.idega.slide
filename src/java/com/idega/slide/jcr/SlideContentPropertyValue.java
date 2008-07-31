package com.idega.slide.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

public class SlideContentPropertyValue implements Value {

	private SlideContentProperty slideContentProperty;

	public SlideContentPropertyValue(SlideContentProperty slideContentProperty) {
		this.slideContentProperty=slideContentProperty;
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
		//byte[] bytes = getSlideContentProperty().getSlideContentNode().getRevisionContent().getContentBytes();
		//return new ByteArrayInputStream(bytes);
		InputStream stream;
		try {
			stream = getSlideContentProperty().getSlideContentNode().getRevisionContent().streamContent();
			return stream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getString() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getType() {
		return PropertyType.BINARY;
	}

	public void setStream(InputStream value) {
		
		/*SlideNode slideNode = this.getSlideContentProperty().getSlideContentNode().getSlideNode();
		if(!slideNode.isNew()){
			slideNode.incrementRevisionNumber();
		}*/
		getSlideContentProperty().getSlideContentNode().getRevisionContent().setContent(value);
	}

	
	public SlideContentProperty getSlideContentProperty(){
		return this.slideContentProperty;
	}
}
