/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.slide.util;

import java.lang.reflect.Field;
import java.util.Timer;
import org.apache.slide.common.Domain;
import org.apache.slide.webdav.event.NotificationTrigger;


/**
 * @author thomas
 *
 *	As long as the org.apache.slide project does not provide methods for 
 *	unloading dirty reflection methods are used to reset some classes that cause
 *	problems during reloading idegaweb application.
 *
 * !!! Be very careful with this class !!!
 * !!! Use this class only when stopping idegaweb application !!!
 *
 */
public class DirtyUnloader {
	
	public void unload()	{
		unloadNotificationTrigger();
		unloadDomain();
	}
	
	/**
	 * The NotificationTrigger and its Timer prevents tomcat from stopping.
	 *
	 */
	private void unloadNotificationTrigger() {
		// first cancel Timer
		cancelAndUnloadTimerOfNotificationTrigger();
		// destroy static instance
		unloadInstanceOfNotificationTrigger();
	}
	
	/** The Domain, once initialized, can never initialized again. 
	 * There are two static variables that are checked:
	 * namespaces and domain.
	 * By setting these static variables to null 
	 * the Domain can be initialized again, that happens
	 * when reloading the idegaweb application.
	 * 
	 *  see the method isInitialized() of the Domain class:
	 *      
     * Tests if the domain has been initialized before.
     *
     * @return boolean True if the domain has already been initialized
     *
     *  	public static boolean isInitialized() {
     *
     *			return ((domain != null) || (namespaces != null));
     *		}
     *
	 *
	 */
	private void unloadDomain() {
		unloadEmbeddedDomainOfDomain();
		unloadNameSpacesOfDomain();
	}
	
	
	private void cancelAndUnloadTimerOfNotificationTrigger() {
		try {
			Field timerField = NotificationTrigger.class.getDeclaredField("timer");
			timerField.setAccessible(true);
			Object object = timerField.get(null);
			Timer timer = (Timer) object;
			timer.cancel();
			timerField.set(null, null);
		}
		catch (Exception ex) {
			System.err.println("[DirtyUnloader] Could not unload timer of NotificationTrigger");
		}
	}

    
	private void unloadInstanceOfNotificationTrigger() {
		try {
			Field instanceField = NotificationTrigger.class.getDeclaredField("notificationTrigger");
			instanceField.setAccessible(true);
			instanceField.set(null, null);
		}
		catch (Exception ex) {
			System.err.println("[DirtyUnloader] Could not unload instance of NotificationTrigger");
		}
	}

	private void unloadEmbeddedDomainOfDomain() {
		try {
			Field embeddedDomainField = Domain.class.getDeclaredField("domain");
			embeddedDomainField.setAccessible(true);
			embeddedDomainField.set(null, null);
		}
		catch (Exception ex) {
			System.out.println("[DirtyUnloader] Could not unload embeddedDomain of Domain");
		}
	}
    
	private void unloadNameSpacesOfDomain() {
		try {
			Field namespacesField = Domain.class.getDeclaredField("namespaces");
			namespacesField.setAccessible(true);
			namespacesField.set(null, null);
		}
		catch (Exception ex) {
			System.out.println("[DirtyUnloader] Could not unload namespaces of Domain");
		}
	}
    

}
