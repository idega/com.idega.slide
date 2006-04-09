/*
 * $Id: AccessControlList.java,v 1.5 2006/04/09 11:44:15 laddi Exp $
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
 *  Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.5 $
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
	
	boolean tmp = false;
	
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
		this.aceList = new ArrayList();
		this.acesForStandardPrincipals = new ArrayList();
		this.acesForRoles = new ArrayList();
		this.acesForUsers = new ArrayList();
		this.acesForGroups = new ArrayList();
		this.acesForOthers = new ArrayList();
	}
	
	public void setAces(Ace[] aces){
		clearLists();
//		System.out.println("setAces(...) starts");
		if(aces != null){
			for (int i = 0; i < aces.length; i++) {
				Ace ace = aces[i];
				addAce(ace);
			}
		}
//		System.out.println("setAces(...) ends");
	}
	
	public void add(AccessControlEntry entry){
//		tmp=true;
		addAceToList(this.aceList,entry);
//		tmp=false;
		
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
	private void addAceToList(List theList, AccessControlEntry entry) {
		int index = -1;
		String entryPrincipal = entry.getPrincipal();
		boolean entryIsNegative = entry.isNegative();
		boolean listContainedAnalogousEntry = false;
		for (ListIterator iter = theList.listIterator(); iter.hasNext();) {
			AccessControlEntry ace = (AccessControlEntry) iter.next();
			int lIndex = iter.nextIndex()-1;
			if(entryPrincipal.equals(ace.getPrincipal())){
				if(entryIsNegative==ace.isNegative()){  //swap entries
//					if(tmp){System.out.println("Removing: "+ace.toString()+" and adding "+entry.toString());}
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
//			if(tmp){	System.out.println("Adding "+((addBefore)?"before":"after")+" sister item ("+index+")(size:"+theList.size()+") : "+entry.toString());}	
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
		List l = new ArrayList();
		String rootRoleSuffix = "/".concat(IWSlideConstants.ROLENAME_ROOT);
		boolean containsPositiveRootACE = false;
		
		Collections.sort(this.aceList, new AcessControlEntryComparator());  // Groups roles, groups, users and standard principals together 
		
		for (Iterator iter = this.aceList.iterator(); iter.hasNext();) {
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
		return this.aceList;
	}
	
	public List getAccessControlEntriesForStandardPrincipals(){
		return this.acesForStandardPrincipals;
	}
	
	public List getAccessControlEntriesForRoles(){
		return this.acesForRoles;
	}
	
	public List getAccessControlEntriesForUsers(){
		return this.acesForUsers;
	}
	
	public List getAccessControlEntriesForGroups(){
		return this.acesForGroups;
	}
	
	public List getAccessControlEntriesForOthers(){
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
	 * When this is true, as default, the method #getAces() returnes array of Aces that is guaranteed not to 
	 * denie the root role any privileges when used to store ACL.
	 * 
	 * @param guaranteeThatRootHasAllPrivileges The guaranteeThatRootHasAllPrivileges to set. Default is true.
	 */
	public void setGuaranteedThatRootHasAllPrivileges(boolean guaranteeThatRootHasAllPrivileges) {
		this.guaranteeThatRootHasAllPrivileges = guaranteeThatRootHasAllPrivileges;
	}
}
