/*
 * $Id: AccessControlEntry.java,v 1.3 2006/04/09 11:44:15 laddi Exp $
 * Created on 28.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.Enumeration;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.PropertyName;


/**
 * 
 *  Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.3 $
 */
public class AccessControlEntry extends Ace {

	public static final int PRINCIPAL_TYPE_OTHER = -1;
	public static final int PRINCIPAL_TYPE_STANDARD = 0;
	public static final int PRINCIPAL_TYPE_ROLE = 2;
	public static final int PRINCIPAL_TYPE_USER = 3;
	public static final int PRINCIPAL_TYPE_GROUP = 4;
	
	private Ace ace;
	private int principalType;

	/**
	 * @param principal
	 * @param negative
	 * @param protectedAce
	 * @param inherited
	 * @param inheritedFrom
	 */
	public AccessControlEntry(String principal, boolean negative, boolean protectedAce, boolean inherited, String inheritedFrom, int principalType){
		//Would use the default constructor if available.
		super("");
		this.ace = new Ace(principal,negative,protectedAce,inherited,inheritedFrom);
		this.principalType = principalType;
	}
	
	public AccessControlEntry(Ace ace, int principalType) {
		//Would use the default constructor if available.
		super("");
		this.ace = ace;
		this.principalType = principalType;
//		System.out.print("creating AcessControlEntry for ace: "+ace+" type:"+principalType);
//		Enumeration e = ace.enumeratePrivileges();
//		while (e.hasMoreElements()) {
//			Privilege p = (Privilege) e.nextElement();
//			System.out.print(", "+p.getName());
//		}
//		System.out.println();
	}
	
	public int getPrincipalType(){
		return this.principalType;
	}
	
	public boolean principalIsRole(){
		return getPrincipalType() == PRINCIPAL_TYPE_ROLE;
	}
	
	public boolean principalIsUser(){
		return getPrincipalType() == PRINCIPAL_TYPE_USER;
	}
	
	public boolean principalIsGroup(){
		return getPrincipalType() == PRINCIPAL_TYPE_GROUP;
	}
	
	public boolean containsPrivilege(Privilege privilege){
		Enumeration privileges = enumeratePrivileges();
		while (privileges.hasMoreElements()) {
			Privilege element = (Privilege) privileges.nextElement();
			if(element.equals(privilege)){
				return true;
			}
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#addPrivilege(org.apache.webdav.lib.Privilege)
	 */
	public void addPrivilege(Privilege privilege) {
		this.ace.addPrivilege(privilege);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#clearPrivileges()
	 */
	public void clearPrivileges() {
		this.ace.clearPrivileges();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#enumeratePrivileges()
	 */
	public Enumeration enumeratePrivileges() {
		return this.ace.enumeratePrivileges();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return this.ace.equals(obj);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getInheritedFrom()
	 */
	public String getInheritedFrom() {
		return this.ace.getInheritedFrom();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getPrincipal()
	 */
	public String getPrincipal() {
		return this.ace.getPrincipal();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getProperty()
	 */
	public PropertyName getProperty() {
		return this.ace.getProperty();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#hashCode()
	 */
	public int hashCode() {
		return this.ace.hashCode();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isInherited()
	 */
	public boolean isInherited() {
		return this.ace.isInherited();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isNegative()
	 */
	public boolean isNegative() {
		return this.ace.isNegative();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isProtected()
	 */
	public boolean isProtected() {
		return this.ace.isProtected();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#removePrivilege(org.apache.webdav.lib.Privilege)
	 */
	public boolean removePrivilege(Privilege privilege) {
		return this.ace.removePrivilege(privilege);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setInherited(boolean)
	 */
	public void setInherited(boolean inherited) {
		this.ace.setInherited(inherited);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setInheritedFrom(java.lang.String)
	 */
	public void setInheritedFrom(String inheritedFrom) {
		this.ace.setInheritedFrom(inheritedFrom);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setNegative(boolean)
	 */
	public void setNegative(boolean negative) {
		this.ace.setNegative(negative);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setPrincipal(java.lang.String)
	 */
	public void setPrincipal(String principal) {
		this.ace.setPrincipal(principal);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setProperty(org.apache.webdav.lib.PropertyName)
	 */
	public void setProperty(PropertyName property) {
		this.ace.setProperty(property);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setProtected(boolean)
	 */
	public void setProtected(boolean protectedAce) {
		this.ace.setProtected(protectedAce);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#toString()
	 */
	public String toString() {
		return this.ace.toString();
	}
	
	public boolean hasPrivileges(){
		return enumeratePrivileges().hasMoreElements();
	}
	
	public Ace getWrappedAce(){
		return this.ace;
	}
}