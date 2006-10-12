package com.idega.slide.business;

import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.event.EventCollection;

public class IWContentEvent  {

	private AbstractEventMethod method;
	private ContentEvent contentEvent;
	
	public IWContentEvent(EventCollection.Event event) {
		this.contentEvent = (ContentEvent)event.getEvent();
		this.method = event.getMethod();
	}

	public AbstractEventMethod getMethod() {
		return method;
	}

	public ContentEvent getContentEvent() {
		return contentEvent;
	}

	public void setContentEvent(ContentEvent contentEvent) {
		this.contentEvent = contentEvent;
	}

	public void setMethod(AbstractEventMethod method) {
		this.method = method;
	}

}