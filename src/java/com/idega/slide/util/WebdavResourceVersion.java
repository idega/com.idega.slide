/*
 * $Id: WebdavResourceVersion.java,v 1.8 2006/04/09 11:44:15 laddi Exp $ Created on Dec
 * 19, 2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.slide.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Map;
import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.properties.CheckedinProperty;
import org.apache.webdav.lib.properties.CheckedoutProperty;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.user.data.User;

/**
 * 
 * Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * A little wrapper for version information
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson </a>
 * @version $Revision: 1.8 $
 */
public class WebdavResourceVersion implements Comparable,Serializable{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -9196111629242509312L;

	private BaseProperty versionName;

	private BaseProperty creatorDisplayName;

	private BaseProperty lastModified;

	private BaseProperty contentLength;

	//private BaseProperty successorSet;
	
	private CheckedinProperty checkedIn;

	private CheckedoutProperty checkedOut;

	private BaseProperty comment;
	
	private BaseProperty creationDate;
	
	protected WebdavResourceVersion(Map propTable) {
		this.versionName = (BaseProperty) propTable.get(VersionHelper.PROPERTY_VERSION_NAME);
		this.creatorDisplayName = (BaseProperty) propTable.get(VersionHelper.PROPERTY_CREATOR_DISPLAY_NAME);
		this.lastModified = (BaseProperty) propTable.get(VersionHelper.PROPERTY_LAST_MODIFIED);
		this.contentLength = (BaseProperty) propTable.get(VersionHelper.PROPERTY_CONTENT_LENGTH);
		//successorSet = (BaseProperty) propTable.get(VersionHelper.PROPERTY_SUCCESSOR_SET);
		this.checkedIn = (CheckedinProperty) propTable.get(VersionHelper.PROPERTY_CHECKED_IN);
		this.checkedOut = (CheckedoutProperty) propTable.get(VersionHelper.PROPERTY_CHECKED_OUT);
		this.comment = (BaseProperty) propTable.get(VersionHelper.PROPERTY_COMMENT);
		this.creationDate = (BaseProperty) propTable.get(VersionHelper.PROPERTY_CREATION_DATE);
	}

	public String toString() {
		if (this.versionName != null) {
			return this.versionName.getPropertyAsString();
		}
		return null;
	}

	/**
	 * @return Returns the checkedIn.
	 */
	public String getCheckedIn() {
		return this.checkedIn.getPropertyAsString();
	}

	/**
	 * @return Returns the checkedOut.
	 */
	public String getCheckedOut() {
		return this.checkedOut.getPropertyAsString();
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return this.comment.getPropertyAsString();
	}

	/**
	 * @return Returns the contentLength.
	 */
	public String getContentLength() {
		return this.contentLength.getPropertyAsString();
	}

	/**
	 * @return Returns the creatorDisplayName.
	 */
	public String getCreatorDisplayName() {
		String userName = this.creatorDisplayName.getPropertyAsString();
		String name = null;
		
		LoginTable login =  LoginDBHandler.getUserLoginByUserName(userName);
		if(login!=null){
			User user = login.getUser();
			name = user.getName();
		}
		
		if(name!=null && !userName.equals(name)){
			return name+" ("+userName+")";
		}
		else{
			return userName;
		}
	}

	/**
	 * @return Returns the lastModified.
	 */
	public String getLastModified() {
		return this.lastModified.getPropertyAsString();
	}

	/**
	 * @return Returns the successorSet.
	 */
//	public BaseProperty getSuccessorSet() {
//		return successorSet;
//	}

	/**
	 * @return Returns the versionName.
	 */
	public String getVersionName() {
		return toString();
	}
	
	/**
	 * @return Returns the versionName.
	 */
	public String getURL() {
		return this.versionName.getOwningURL();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object version) {
		String otherVersionToString = version.toString();
		String thisVersionToString = this.toString();
		
		int otherIndexOfDot = otherVersionToString.indexOf(".");
		int thisIndexOfDot = thisVersionToString.indexOf(".");
		if(otherIndexOfDot!=-1 && thisIndexOfDot!=-1){
			//Cut of the first dot, to make comparision handle the cases 1.9 and 1.10 e.g.
			String otherVersionToStringBeforeDot = otherVersionToString.substring(0,otherIndexOfDot);
			String thisVersionToStringBeforeDot = thisVersionToString.substring(0,thisIndexOfDot);
			
			String otherVersionToStringAfterDot = otherVersionToString.substring(otherIndexOfDot+1,otherVersionToString.length());
			String thisVersionToStringAfterDot = thisVersionToString.substring(thisIndexOfDot+1,thisVersionToString.length());
			try{
				int intOtherVersionBeforeDot = Integer.parseInt(otherVersionToStringBeforeDot);
				int intThisVersionBeforeDot = Integer.parseInt(thisVersionToStringBeforeDot);
				
				int intOtherVersionAfterDot = Integer.parseInt(otherVersionToStringAfterDot);
				int intThisVersionAfterDot = Integer.parseInt(thisVersionToStringAfterDot);
				
				if(intOtherVersionBeforeDot==intThisVersionBeforeDot){
					if(intOtherVersionAfterDot==intThisVersionAfterDot){
						return 0;
					}
					else if(intOtherVersionAfterDot>intThisVersionAfterDot){
						return -1;
					}
					else if(intOtherVersionAfterDot<intThisVersionAfterDot){
						return 1;
					}
				}
				else if(intOtherVersionBeforeDot>intThisVersionBeforeDot){
					return -1;
				}
				else if(intOtherVersionBeforeDot<intThisVersionBeforeDot){
					return 1;
				}
				
			}
			catch(NumberFormatException nfe){
				nfe.printStackTrace();
			}
		
		}
		
		int theReturn = Collator.getInstance().compare(thisVersionToString,otherVersionToString);
		return theReturn;
	}
	
	public String getCreationDate() {
		return this.creationDate.getPropertyAsString();
	}
	
}