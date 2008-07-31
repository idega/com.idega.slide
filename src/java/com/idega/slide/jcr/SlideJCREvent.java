package com.idega.slide.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;

import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;

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

}
