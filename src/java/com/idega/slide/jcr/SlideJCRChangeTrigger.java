package com.idega.slide.jcr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
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

/**
 * <p>
 * Trigger to map against the JCR Observation mechanism - not finished
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideJCRChangeTrigger implements EventCollectionListener {

	private SlideRepository slideRepository;
	
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
		//Session session = getSession();
		SlideRepository slideRepo=getSlideRepository();
		
		ObservationManager observationManager;
		try {
			if(slideRepo!=null){
				observationManager = slideRepo.getDefaultObservationManager();
	
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

	/*private Session getSession() {
		// TODO Auto-generated method stub
		return getRepository().l;
	}

	private Repository getRepository() {
		// TODO Auto-generated method stub
		return null;
	}*/

	public void vetoableCollected(EventCollection collection)
			throws VetoException {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unused")
	private void setSlideRepository(SlideRepository slideRepository) {
		this.slideRepository = slideRepository;
	}

	private SlideRepository getSlideRepository() {
		return slideRepository;
	}

}
