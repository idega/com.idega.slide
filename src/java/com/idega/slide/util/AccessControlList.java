/*
 * $Id: AccessControlList.java,v 1.3 2005/03/10 23:45:00 gummi Exp $
 * Created on 28.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.webdav.lib.Ace;


/**
 * 
 *  Last modified: $Date: 2005/03/10 23:45:00 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.3 $
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
	
	protected boolean guaranteeThatRootHasAllPrivileges = true;
	
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
		addAceToList(aceList,entry);
		
		switch (entry.getPrincipalType()) {
			case AccessControlEntry.PRINCIPAL_TYPE_ROLE:
				addAceToList(acesForRoles,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_STANDARD:
				addAceToList(acesForStandardPrincipals,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_GROUP:
				addAceToList(acesForUsers,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_USER:
				addAceToList(acesForUsers,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_OTHER:
			default:
				addAceToList(acesForOthers,entry);
				break;
		}
	}
	
	/**
	 * @param entry
	 */
	private void addAceToList(List theList, AccessControlEntry entry) {
		int index = -1;
		String entryPrincipal = entry.getPrincipal();
		boolean entryIsNegative = entry.isNegative();
		boolean listContainedAnalogousEntry = false;
		for (ListIterator iter = theList.listIterator(); iter.hasNext();) {
			AccessControlEntry ace = (AccessControlEntry) iter.next();
			int lIndex = iter.nextIndex();
			if(entryPrincipal.equals(ace.getPrincipal())){
				if(entryIsNegative==ace.isNegative()){  //swap entries
					//System.out.println("Removing: "+ace.toString()+" and adding "+entry.toString());
					iter.remove();
					iter.add(entry);
					index = -1;
					listContainedAnalogousEntry = true;
					break;
				} else {  //keep index of ace with the same principal
					index = lIndex;
				}
			}
		}
		
		if(index > -1 || !listContainedAnalogousEntry) {
			int indexOfSecondLastItem = theList.size()-2;
			if(index < indexOfSecondLastItem && !listContainedAnalogousEntry){ //add next after it's sister item
				//System.out.println("Adding after sister item ("+index+") : "+entry.toString());			
				theList.add((index+1),entry);  
			} else{
				//System.out.println("Adding at end ("+index+") : "+entry.toString());	
				theList.add(entry);
			}
		}
	}

	/**
	 * @param ace
	 */
	private void addAce(Ace ace) {
		int type;
		String principal = ace.getPrincipal();
		AccessControlEntry acentry = null;
		if(IWSlideConstants.ALL_STANDARD_SUBJECT_URIS.contains(principal)){
			type = AccessControlEntry.PRINCIPAL_TYPE_STANDARD;
			acentry = new AccessControlEntry(ace,type);
		} else {
			int index = principal.indexOf(serverURI);
			if(index > -1){
				principal = principal.substring(index+serverURI.length());
				if(principal.startsWith(IWSlideConstants.PATH_ROLES)){
					type = AccessControlEntry.PRINCIPAL_TYPE_ROLE;
					acentry = new AccessControlEntry(ace,type);
				} else if(principal.startsWith(IWSlideConstants.PATH_USERS)){
					type = AccessControlEntry.PRINCIPAL_TYPE_USER;
					acentry = new AccessControlEntry(ace,type);
				} else if(principal.startsWith(IWSlideConstants.PATH_GROUPS)){
					type = AccessControlEntry.PRINCIPAL_TYPE_GROUP;
					acentry = new AccessControlEntry(ace,type);
				} else {
					type = AccessControlEntry.PRINCIPAL_TYPE_OTHER;
					acentry = new AccessControlEntry(ace,type);
				}
			} else {
				type = AccessControlEntry.PRINCIPAL_TYPE_OTHER;
				acentry = new AccessControlEntry(ace,type);
			}
		}
		if(acentry!=null){
			add(acentry);
		}
	}
	
	public String getResourcePath(){
		return resourcePath;
	}

	public Ace[] getAces(){
		List l = new ArrayList();
		String rootRoleSuffix = "/".concat(IWSlideConstants.ROLENAME_ROOT);
		boolean containsPositiveRootACE = false;
		
		Collections.sort(aceList, new AcessControlEntryComparator());  // Groups roles, groups, users and standard principals together 
		
		for (Iterator iter = aceList.iterator(); iter.hasNext();) {
			AccessControlEntry entry = (AccessControlEntry) iter.next();
			if(entry.hasPrivileges()){
				if(isGuaranteedThatRootHasAllPrivileges() && entry.getPrincipalType() == AccessControlEntry.PRINCIPAL_TYPE_ROLE && entry.getPrincipal().endsWith(rootRoleSuffix)){
					if(!entry.isNegative()){
						entry.setInherited(false);
						entry.setInheritedFrom(null);
						entry.clearPrivileges();
						entry.addPrivilege(IWSlideConstants.PRIVILEGE_ALL);
						containsPositiveRootACE = true;
						l.add(0,entry.getWrappedAce());
					}
				} else {
					l.add(entry.getWrappedAce());
				}
			}
		}
		if(!containsPositiveRootACE){
			System.err.println("[Warning]["+this.getClass().getName()+"]: list does not contain positive ace for root role.");
		}
		//System.out.println("Size of aceList is "+aceList.size());
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
	
	
	/** 
	 * @return Returns the guaranteeThatRootHasAllPrivileges. Default is true.
	 */
	public boolean isGuaranteedThatRootHasAllPrivileges() {
		return guaranteeThatRootHasAllPrivileges;
	}
	/**
	 * 
	 * When this is true, as default, the method #getAces() returnes array of Aces that is guaranteed not to 
	 * denie the root role any privileges when used to store ACL.
	 * 
	 * @param guaranteeThatRootHasAllPrivileges The guaranteeThatRootHasAllPrivileges to set. Default is true.
	 */
	public void setGuaranteedThatRootHasAllPrivileges(boolean guaranteeThatRootHasAllPrivileges) {
		this.guaranteeThatRootHasAllPrivileges = guaranteeThatRootHasAllPrivileges;
	}
}
