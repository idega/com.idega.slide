/*
 * $Id: AccessControlEntry.java,v 1.1 2005/01/07 18:55:05 gummi Exp $
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
 *  Last modified: $Date: 2005/01/07 18:55:05 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
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
	}
	
	public int getPrincipalType(){
		return principalType;
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
	
	
	
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#addPrivilege(org.apache.webdav.lib.Privilege)
	 */
	public void addPrivilege(Privilege privilege) {
		ace.addPrivilege(privilege);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#clearPrivileges()
	 */
	public void clearPrivileges() {
		ace.clearPrivileges();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#enumeratePrivileges()
	 */
	public Enumeration enumeratePrivileges() {
		return ace.enumeratePrivileges();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return ace.equals(obj);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getInheritedFrom()
	 */
	public String getInheritedFrom() {
		return ace.getInheritedFrom();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getPrincipal()
	 */
	public String getPrincipal() {
		return ace.getPrincipal();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#getProperty()
	 */
	public PropertyName getProperty() {
		return ace.getProperty();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#hashCode()
	 */
	public int hashCode() {
		return ace.hashCode();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isInherited()
	 */
	public boolean isInherited() {
		return ace.isInherited();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isNegative()
	 */
	public boolean isNegative() {
		return ace.isNegative();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#isProtected()
	 */
	public boolean isProtected() {
		return ace.isProtected();
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#removePrivilege(org.apache.webdav.lib.Privilege)
	 */
	public boolean removePrivilege(Privilege privilege) {
		return ace.removePrivilege(privilege);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setInherited(boolean)
	 */
	public void setInherited(boolean inherited) {
		ace.setInherited(inherited);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setInheritedFrom(java.lang.String)
	 */
	public void setInheritedFrom(String inheritedFrom) {
		ace.setInheritedFrom(inheritedFrom);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setNegative(boolean)
	 */
	public void setNegative(boolean negative) {
		ace.setNegative(negative);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setPrincipal(java.lang.String)
	 */
	public void setPrincipal(String principal) {
		ace.setPrincipal(principal);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setProperty(org.apache.webdav.lib.PropertyName)
	 */
	public void setProperty(PropertyName property) {
		ace.setProperty(property);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#setProtected(boolean)
	 */
	public void setProtected(boolean protectedAce) {
		ace.setProtected(protectedAce);
	}
	/* (non-Javadoc)
	 * @see org.apache.webdav.lib.Ace#toString()
	 */
	public String toString() {
		return ace.toString();
	}
	
	public boolean hasPrivileges(){
		return enumeratePrivileges().hasMoreElements();
	}
	
	public Ace getWrappedAce(){
		return ace;
	}
}