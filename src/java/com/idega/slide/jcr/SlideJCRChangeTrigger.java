package com.idega.slide.jcr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.event.EventCollection;
import org.apache.slide.event.EventCollectionListener;
import org.apache.slide.event.VetoException;


public class SlideJCRChangeTrigger implements EventCollectionListener {

	public void collected(EventCollection events) {
		List collectedEvents = events.getCollection();
		List jcrEvents = new ArrayList();
		for (Iterator i = collectedEvents.iterator(); i.hasNext();) {
			EventCollection.Event event = (EventCollection.Event) i.next();
			AbstractEventMethod method = event.getMethod();
			if(ContentEvent.REMOVE.equals(method) || ContentEvent.CREATE.equals(method) || ContentEvent.STORE.equals(method) ){
				//ContentEvent contentEvent = (ContentEvent)event.getEvent();
				//IWContentEvent iwContentEvent = new IWContentEvent(event);

				SlideJCREvent jcrEvent = new SlideJCREvent(event);
				jcrEvents.add(jcrEvent);
							
			}
		}
		
		//Repository repository = getRepository();
		Session session = getSession();
		
		ObservationManager observationManager;
		try {
			if(session!=null){
				observationManager = session.getWorkspace().getObservationManager();
	
				EventListenerIterator iterator = observationManager.getRegisteredEventListeners();
				while(iterator.hasNext()){
					EventListener listener = iterator.nextEventListener();
					EventIterator eventiterator = new IteratorHelper(jcrEvents);
					listener.onEvent(eventiterator);
				}
			}
		} catch (UnsupportedRepositoryOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	private Repository getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	public void vetoableCollected(EventCollection collection)
			throws VetoException {
		// TODO Auto-generated method stub
		
	}

}
