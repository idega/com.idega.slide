/*
 * $Id: IWSlideChangeEventClient.java,v 1.3 2007/05/11 11:21:55 eiki Exp $
 * Created on May 9, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.event;

import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideTokenImpl;
import org.apache.slide.common.Uri;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.store.ExtendedStore;
import org.apache.slide.store.Store;
import org.apache.slide.util.logger.Logger;

import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.impl.EventClient;
import com.idega.slide.business.IWContentEvent;
import com.idega.slide.business.IWSlideChangeListener;


/**
 * 
 *  Last modified: $Date: 2007/05/11 11:21:55 $ by $Author: eiki $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class IWSlideChangeEventClient extends EventClient implements IWSlideChangeListener {
	
	private static final String LOG_CHANNEL = IWSlideChangeEventClient.class.getName();
	
	private static final String URI ="URI";
	
	private String storeKey = null;
	private String removeKey = null;
	private String createKey = null;
	
	public IWSlideChangeEventClient() {
		storeKey = ContentEvent.STORE.getName();
		removeKey = ContentEvent.REMOVE.getName();
		createKey = ContentEvent.CREATE.getName();
		initialize(IWSlideChangeEventClient.class);
	}
	
	public void onSlideChange(IWContentEvent event) {
		if (isNothingToDo()) return;
		String methodName = event.getMethod().getName();
		String uri = event.getContentEvent().getUri();
		fireEvent(methodName, URI, uri);
	}
	
	
	public void handleEvent(MethodCallEvent methodCallEvent) {
		if (isEventCompatible(methodCallEvent)) {
			contentChanged(methodCallEvent);
			structureChanged(methodCallEvent);
		}
	}

	public void contentChanged(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, storeKey)){
			String uri = methodCallEvent.get(URI);
			removeObject(uri, false);
		}
	}
	
	public void structureChanged(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, removeKey) || isMethod(methodCallEvent,createKey)){
			String uri = methodCallEvent.get(URI);
			removeObject(uri, true);
		}
	}
			
	private void removeObject(String uri, boolean getParent) {
		NamespaceAccessToken nat = Domain.accessNamespace(new SecurityToken(this), Domain.getDefaultNamespace());
		try {
			nat.begin();
			Uri theUri = nat.getUri(new SlideTokenImpl(new CredentialsToken("")), uri);
			// get the parent
			theUri = (getParent) ? theUri.getParentUri() : theUri;
			Store store = theUri.getStore();
			if (store instanceof ExtendedStore) {
				Domain.log("Resetting cache for " + theUri, LOG_CHANNEL, Logger.INFO);
				((ExtendedStore) store).removeObjectFromCache(theUri);
			}
			nat.commit();
		} 
		catch(Exception e) {
			if (Domain.isEnabled(LOG_CHANNEL, Logger.ERROR)) {
				Domain.log("Error clearing cache: " + e + ". See stderr for stacktrace.", LOG_CHANNEL, Logger.ERROR);
				e.printStackTrace();
			}
		}
	}

	

}
