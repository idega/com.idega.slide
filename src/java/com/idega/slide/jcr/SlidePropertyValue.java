package com.idega.slide.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;

/**
 * <p>
 * Main implementation for the JCR node property object value in Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlidePropertyValue implements Value {

	
    protected static final SimpleDateFormat format;
    protected static final SimpleDateFormat formats[];
    protected static final SimpleDateFormat creationDateFormat;

    static 
    {
        format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        formats = (new SimpleDateFormat[] {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        });
        creationDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }
    
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
		Object value = slideProperty.nodeProperty.getValue();
		if(value instanceof Boolean){
			return (Boolean)value;
		}
		else{
			try{
				return Boolean.parseBoolean(value.toString());
			}
			catch(Exception e){
				throw new ValueFormatException(e);
			}
			
		}
		//throw new ValueFormatException("Unable to convert property to boolean for property: "+slideProperty.getName());
	}

	public Calendar getDate() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		Object value = slideProperty.nodeProperty.getValue();
        if(value == null)
            return null;
        if(value instanceof Calendar)
            return (Calendar)value;
        String creationDateValue = value.toString();
        Date result = null;
        for(int i = 0; result == null && i < formats.length; i++){
            try
            {
                synchronized(formats[i])
                {
                    result = formats[i].parse(creationDateValue);
                }
            }
            catch(ParseException e) { }
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        return cal;
		//throw new ValueFormatException("Unknown format of property: "+slideProperty.getName());
	}

	public double getDouble() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		Object value = slideProperty.nodeProperty.getValue();
		if(value==null){
			return 0;
		}
		else if(value instanceof Double){
			return (Double)value;
		}
		else{
			try{
				return Double.parseDouble(value.toString());
			}
			catch(Exception e){
				throw new ValueFormatException(e);
			}
			
		}
	}

	public long getLong() throws ValueFormatException, IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		Object value = slideProperty.nodeProperty.getValue();
		if(value==null){
			return 0;
		}
		else if(value instanceof Long){
			return (Long)value;
		}
		else{
			try{
				return Long.parseLong(getString());
			}
			catch(NumberFormatException nfe){
				throw new ValueFormatException(nfe);
			}
		}
	}

	public InputStream getStream() throws IllegalStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return (InputStream)slideProperty.nodeProperty.getValue();
	}

	public String getString() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		// TODO Auto-generated method stub
		Object value = slideProperty.nodeProperty.getValue();
		return value.toString();
	}

	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
	public void setType(int type){
		this.type=type;
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
