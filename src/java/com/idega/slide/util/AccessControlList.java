/*
 * $Id: AccessControlList.java,v 1.1 2005/01/07 18:55:05 gummi Exp $
 * Created on 28.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.webdav.lib.Ace;


/**
 * 
 *  Last modified: $Date: 2005/01/07 18:55:05 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class AccessControlList {
	
	protected List aceList;
	protected String serverURI;
	protected String resourcePath;
	
	protected List acesForStandardPrincipals;
	protected List acesForRoles;
	protected List acesForUsers;
	protected List acesForGroups;
	protected List acesForOthers;
	
	public AccessControlList(String serverURI, String resourcePath){
		this.serverURI = serverURI;
		this.resourcePath = resourcePath;
		initializeLists();
	}
	
	private void initializeLists(){
		clearLists();
	}
	
	private void clearLists(){
		aceList = new ArrayList();
		acesForStandardPrincipals = new ArrayList();
		acesForRoles = new ArrayList();
		acesForUsers = new ArrayList();
		acesForGroups = new ArrayList();
		acesForOthers = new ArrayList();
	}
	
	public void setAces(Ace[] aces){
		clearLists();
		if(aces != null){
			for (int i = 0; i < aces.length; i++) {
				Ace ace = aces[i];
				addAce(ace);
			}
		}
	}
	
	public void add(AccessControlEntry entry){
		switch (entry.getPrincipalType()) {
			case AccessControlEntry.PRINCIPAL_TYPE_ROLE:
				aceList.add(entry);
				acesForRoles.add(entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_STANDARD:
				aceList.add(entry);
				acesForStandardPrincipals.add(entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_GROUP:
				aceList.add(entry);
				acesForGroups.add(entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_USER:
				aceList.add(entry);
				acesForUsers.add(entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_OTHER:
			default:
				aceList.add(entry);
				acesForOthers.add(entry);
				break;
		}
	}
	
	/**
	 * @param ace
	 */
	private void addAce(Ace ace) {
		int type;
		String principal = ace.getPrincipal();
		if(IWSlideConstants.ALL_STANDARD_SUBJECT_URIS.contains(principal)){
			type = AccessControlEntry.PRINCIPAL_TYPE_STANDARD;
			AccessControlEntry acentry = new AccessControlEntry(ace,type);
			aceList.add(acentry);
			acesForStandardPrincipals.add(acentry);
		} else {
			int index = principal.indexOf(serverURI);
			if(index > -1){
				principal = principal.substring(index+serverURI.length());
				if(principal.startsWith(IWSlideConstants.PATH_ROLES)){
					type = AccessControlEntry.PRINCIPAL_TYPE_ROLE;
					AccessControlEntry acentry = new AccessControlEntry(ace,type);
					aceList.add(acentry);
					acesForRoles.add(acentry);
				} else if(principal.startsWith(IWSlideConstants.PATH_USERS)){
					type = AccessControlEntry.PRINCIPAL_TYPE_USER;
					AccessControlEntry acentry = new AccessControlEntry(ace,type);
					aceList.add(acentry);
					acesForUsers.add(acentry);
				} else if(principal.startsWith(IWSlideConstants.PATH_GROUPS)){
					type = AccessControlEntry.PRINCIPAL_TYPE_GROUP;
					AccessControlEntry acentry = new AccessControlEntry(ace,type);
					aceList.add(acentry);
					acesForGroups.add(acentry);
				} else {
					type = AccessControlEntry.PRINCIPAL_TYPE_OTHER;
					AccessControlEntry acentry = new AccessControlEntry(ace,type);
					aceList.add(acentry);
					acesForOthers.add(acentry);
				}
			} else {
				type = AccessControlEntry.PRINCIPAL_TYPE_OTHER;
				AccessControlEntry acentry = new AccessControlEntry(ace,type);
				aceList.add(acentry);
				acesForOthers.add(acentry);
			}
		}
	}
	
	public String getResourcePath(){
		return resourcePath;
	}

	public Ace[] getAces(){
		List l = new ArrayList();
		for (Iterator iter = aceList.iterator(); iter.hasNext();) {
			AccessControlEntry entry = (AccessControlEntry) iter.next();
			if(entry.hasPrivileges()){
				l.add(entry.getWrappedAce());
			}
		}
		return (Ace[])l.toArray(new Ace[l.size()]);
	}
	
	public List getAccessControlEntries(){
		return aceList;
	}
	
	public List getAccessControlEntriesForStandardPrincipals(){
		return acesForStandardPrincipals;
	}
	
	public List getAccessControlEntriesForRoles(){
		return acesForRoles;
	}
	
	public List getAccessControlEntriesForUsers(){
		return acesForUsers;
	}
	
	public List getAccessControlEntriesForGroups(){
		return acesForGroups;
	}
	
	public List getAccessControlEntriesForOthers(){
		return acesForOthers;
	}
	
	
}
