/*
 * $Id: AccessControlList.java,v 1.6 2009/05/19 13:19:27 valdas Exp $
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
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import org.apache.webdav.lib.Ace;

import com.idega.util.CoreConstants;


/**
 * 
 *  Last modified: $Date: 2009/05/19 13:19:27 $ by $Author: valdas $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.6 $
 */
public class AccessControlList {
	
	protected List<AccessControlEntry> aceList;
	protected String serverURI;
	protected String resourcePath;
	
	protected List<AccessControlEntry> acesForStandardPrincipals;
	protected List<AccessControlEntry> acesForRoles;
	protected List<AccessControlEntry> acesForUsers;
	protected List<AccessControlEntry> acesForGroups;
	protected List<AccessControlEntry> acesForOthers;
	
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
		this.aceList = new ArrayList<AccessControlEntry>();
		this.acesForStandardPrincipals = new ArrayList<AccessControlEntry>();
		this.acesForRoles = new ArrayList<AccessControlEntry>();
		this.acesForUsers = new ArrayList<AccessControlEntry>();
		this.acesForGroups = new ArrayList<AccessControlEntry>();
		this.acesForOthers = new ArrayList<AccessControlEntry>();
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
		addAceToList(this.aceList,entry);
		
		switch (entry.getPrincipalType()) {
			case AccessControlEntry.PRINCIPAL_TYPE_ROLE:
				addAceToList(this.acesForRoles,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_STANDARD:
				addAceToList(this.acesForStandardPrincipals,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_GROUP:
				addAceToList(this.acesForUsers,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_USER:
				addAceToList(this.acesForUsers,entry);
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_OTHER:
			default:
				addAceToList(this.acesForOthers,entry);
				break;
		}
	}
	
	/**
	 * This Method adds Ace to the list so that aces that have the same principal are added next to eachother
	 * and the ace that returns false for entry#isNegative() gets lower index than the other. If entry#isNegative()
	 * returns the same for both aces (and they have the same principal) the old one is swapped out for the new one.
	 * 
	 * @param entry
	 */
	private void addAceToList(List<AccessControlEntry> theList, AccessControlEntry entry) {
		int index = -1;
		String entryPrincipal = entry.getPrincipal();
		boolean entryIsNegative = entry.isNegative();
		boolean listContainedAnalogousEntry = false;
		for (ListIterator<AccessControlEntry> iter = theList.listIterator(); iter.hasNext();) {
			AccessControlEntry ace = iter.next();
			int lIndex = iter.nextIndex()-1;
			if(entryPrincipal.equals(ace.getPrincipal())){
				if(entryIsNegative==ace.isNegative()){  //swap entries
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

		boolean addBefore = !entryIsNegative;
		if(!listContainedAnalogousEntry){ //add next after it's sister item
			int addIndex = (index+((addBefore)?0:1));
			if(addIndex <= 0 || addIndex > theList.size()){
				theList.add(entry);
			} else {
				theList.add(addIndex,entry);  
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
			int index = principal.indexOf(this.serverURI);
			if(index > -1){
				principal = principal.substring(index+this.serverURI.length());
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
		return this.resourcePath;
	}

	public Ace[] getAces(){
		List<Ace> l = new ArrayList<Ace>();
		String rootRoleSuffix = CoreConstants.SLASH.concat(IWSlideConstants.ROLENAME_ROOT);
		boolean containsPositiveRootACE = false;
		
		Collections.sort(this.aceList, new AcessControlEntryComparator());  // Groups roles, groups, users and standard principals together 
		
		for (AccessControlEntry entry: this.aceList) {
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
			Logger.getLogger(this.getClass().getName()).warning("List " +this.aceList+ " does not contain positive ace for root role.");
		}
		return l.toArray(new Ace[l.size()]);
	}
	
	public List<AccessControlEntry> getAccessControlEntries(){
		return this.aceList;
	}
	
	public List<AccessControlEntry> getAccessControlEntriesForStandardPrincipals(){
		return this.acesForStandardPrincipals;
	}
	
	public List<AccessControlEntry> getAccessControlEntriesForRoles(){
		return this.acesForRoles;
	}
	
	public List<AccessControlEntry> getAccessControlEntriesForUsers(){
		return this.acesForUsers;
	}
	
	public List<AccessControlEntry> getAccessControlEntriesForGroups(){
		return this.acesForGroups;
	}
	
	public List<AccessControlEntry> getAccessControlEntriesForOthers(){
		return this.acesForOthers;
	}
	
	
	/** 
	 * @return Returns the guaranteeThatRootHasAllPrivileges. Default is true.
	 */
	public boolean isGuaranteedThatRootHasAllPrivileges() {
		return this.guaranteeThatRootHasAllPrivileges;
	}
	/**
	 * 
	 * When this is true, as default, the method #getAces() returns array of Aces that is guaranteed not to 
	 * deny the root role any privileges when used to store ACL.
	 * 
	 * @param guaranteeThatRootHasAllPrivileges The guaranteeThatRootHasAllPrivileges to set. Default is true.
	 */
	public void setGuaranteedThatRootHasAllPrivileges(boolean guaranteeThatRootHasAllPrivileges) {
		this.guaranteeThatRootHasAllPrivileges = guaranteeThatRootHasAllPrivileges;
	}
}
