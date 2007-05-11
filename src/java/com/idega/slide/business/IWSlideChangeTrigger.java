/*
 * $Id: IWSlideChangeTrigger.java,v 1.5 2007/05/11 11:21:55 eiki Exp $ Created on Mar 24,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.slide.business;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.apache.slide.event.AbstractEventMethod;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.event.EventCollection;
import org.apache.slide.event.EventCollectionListener;
import org.apache.slide.event.VetoException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWMainApplication;

/**
 * Listens for any change to the slide filesystem and notifies
 * IWSlideChangeListener classes. Useful for decaching stuff and more...
 * 
 * Last modified: $Date: 2007/05/11 11:21:55 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class IWSlideChangeTrigger implements EventCollectionListener {

	private IWSlideService service;

	public IWSlideChangeTrigger() {
		// empty
	}

	public void vetoableCollected(EventCollection events) throws VetoException {
		// Throw a vetoexception if you want to stop the change,add,remove....
		/*
		 * ContentEvent[] changedEvents =
		 * EventCollectionFilter.getChangedContents(events);
		 * 
		 * for (int i = 0; i < changedEvents.length; i++) { ContentEvent event =
		 * changedEvents[i]; System.out.println("VETO changed: "+event); }
		 * 
		 * changedEvents = EventCollectionFilter.getCreatedContents(events);
		 * 
		 * for (int i = 0; i < changedEvents.length; i++) { ContentEvent event =
		 * changedEvents[i]; System.out.println("VETO created: "+event); }
		 * 
		 * changedEvents = EventCollectionFilter.getRemovedContents(events);
		 * 
		 * for (int i = 0; i < changedEvents.length; i++) { ContentEvent event =
		 * changedEvents[i]; System.out.println("VETO removed: "+event); }
		 */
	}

	public void collected(EventCollection events) {
		this.service = getIWSlideService();
		try {
			IWSlideChangeListener[] listeners = this.service.getIWSlideChangeListeners();
			if (listeners != null && listeners.length>0) {
				// notify on any type of content change
				List collectedEvents = events.getCollection();
				for (Iterator i = collectedEvents.iterator(); i.hasNext();) {
					EventCollection.Event event = (EventCollection.Event) i.next();
					AbstractEventMethod method = event.getMethod();
					if(ContentEvent.REMOVE.equals(method) || ContentEvent.CREATE.equals(method) || ContentEvent.STORE.equals(method) ){
						ContentEvent contentEvent = (ContentEvent)event.getEvent();
						IWContentEvent iwContentEvent = new IWContentEvent(event);
						
						for (int j = 0; j < listeners.length; j++) {
							IWSlideChangeListener listener = listeners[j];
							listener.onSlideChange(iwContentEvent);
						}					
					}
				}
			}
			// OR WE COULD DO IT FOR EACH TYPE OF CHANGE
			// ContentEvent[] changedEvents =
			// EventCollectionFilter.getChangedContents(events);
			// for (int i = 0; i < changedEvents.length; i++) {
			// ContentEvent event = changedEvents[i];
			// System.out.println("Collected changed: " + event);
			// }
			// changedEvents = EventCollectionFilter.getCreatedContents(events);
			// for (int i = 0; i < changedEvents.length; i++) {
			// ContentEvent event = changedEvents[i];
			// System.out.println("Collected created: " + event);
			// }
			// changedEvents = EventCollectionFilter.getRemovedContents(events);
			// for (int i = 0; i < changedEvents.length; i++) {
			// ContentEvent event = changedEvents[i];
			// System.out.println("Collected removed: " + event);
			// }
		}
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected IWSlideService getIWSlideService() {
		try {
			if (this.service == null) {
				this.service = (IWSlideService) IBOLookup.getServiceInstance(
						IWMainApplication.getDefaultIWApplicationContext(), IWSlideService.class);
			}
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return this.service;
	}
}
