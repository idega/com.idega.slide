package com.idega.slide.jcr;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;

/**
 * <p>
 * Event to map against the JCR Observation mechanism - not finished
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideJCREvent implements Event {

	String path;
	int type=0;
	String userID;
	private ContentEvent contentEvent;
	private AbstractEventMethod method;
	
	public SlideJCREvent(org.apache.slide.event.EventCollection.Event event) {
		this.contentEvent = (ContentEvent)event.getEvent();
		this.method = event.getMethod();
		setPath(this.contentEvent.getUri());
		this.setUserID(this.method.getId());
		setType();
	}

	private void setType() {
		if(ContentEvent.REMOVE.equals(method)){
			setType(NODE_REMOVED);
		}
		else if(ContentEvent.CREATE.equals(method)){
			setType(NODE_ADDED);
		}
	}

	public void setPath(String path){
		this.path=path;
	}
	
	public String getPath() throws RepositoryException {
		// TODO Auto-generated method stub
		return path;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
	public void setType(int type){
		this.type=type;
	}

	public String getUserID() {
		// TODO Auto-generated method stub
		return userID;
	}
	
	public void setUserID(String userid){
		this.userID=userid;
	}

	@Override
	public String getIdentifier() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getInfo() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserData() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDate() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

}
