package com.idega.slide.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
/**
 * <p>
 * Wrapper for the specific jcr:content node property value against Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
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

	@Override
	public Binary getBinary() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getDecimal() throws ValueFormatException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}
}
