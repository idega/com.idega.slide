package com.idega.slide.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

public class SlideObservationManager implements ObservationManager {

	SlideWorkspace slideWorkspace;
	List<EventListener> listeners= new ArrayList<EventListener>();
	
	public SlideObservationManager(SlideWorkspace slideWorkspace) {
		this.slideWorkspace=slideWorkspace;
	}

	public void addEventListener(EventListener listener, int eventTypes,
			String absPath, boolean isDeep, String[] uuid,
			String[] nodeTypeName, boolean noLocal) throws RepositoryException {
		listeners.add(listener);
	}

	public EventListenerIterator getRegisteredEventListeners()
			throws RepositoryException {
		return new IteratorHelper(listeners);
	}

	public void removeEventListener(EventListener listener)
			throws RepositoryException {
		listeners.remove(listener);
	}

}
