package com.idega.slide.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.observation.EventJournal;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

/**
 * <p>
 * Imlpementation of the JCR Observation manager for Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideObservationManager implements ObservationManager {

	//SlideWorkspace slideWorkspace;
	SlideRepository slideRepository;
	List<EventListener> listeners= new ArrayList<EventListener>();

	public SlideObservationManager(SlideRepository slideRepository) {
		this.slideRepository=slideRepository;
	}

	@Override
	public void addEventListener(EventListener listener, int eventTypes,
			String absPath, boolean isDeep, String[] uuid,
			String[] nodeTypeName, boolean noLocal) throws RepositoryException {
		listeners.add(listener);
	}

	@Override
	public EventListenerIterator getRegisteredEventListeners()
			throws RepositoryException {
		return new IteratorHelper<EventListener>(listeners);
	}

	@Override
	public void removeEventListener(EventListener listener)
			throws RepositoryException {
		listeners.remove(listener);
	}

	@Override
	public void setUserData(String userData) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public EventJournal getEventJournal() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventJournal getEventJournal(int eventTypes, String absPath,
			boolean isDeep, String[] uuid, String[] nodeTypeName)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
