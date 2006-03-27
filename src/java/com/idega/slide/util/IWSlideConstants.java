/*
 * $Id: IWSlideConstants.java,v 1.6 2006/03/27 14:54:50 eiki Exp $
 * Created on 27.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.slide.structure.SubjectNode;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.properties.CheckedinProperty;
import org.apache.webdav.lib.properties.CheckedoutProperty;


/**
 * 
 *  Last modified: $Date: 2006/03/27 14:54:50 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.6 $
 */
public class IWSlideConstants {
	
	
	public static final String ROLENAME_USERS = "users";
	public static final String ROLENAME_ROOT = "root";
	
	public static final String PATH_USERS = "/users";
	public static final String PATH_GROUPS = "/groups";
	public static final String PATH_ROLES = "/roles";
	public static final String PATH_ACTIONS = "/actions";
	
	public static final String SUBJECT_URI_ALL = SubjectNode.ALL_URI;
	public static final String SUBJECT_URI_AUTHENTICATED = SubjectNode.AUTHENTICATED_URI;
	public static final String SUBJECT_URI_OWNER = SubjectNode.OWNER_URI;
	public static final String SUBJECT_URI_SELF = SubjectNode.SELF_URI;
	public static final String SUBJECT_URI_UNAUTHENTICATED = SubjectNode.UNAUTHENTICATED_URI;
	
	public static List ALL_STANDARD_SUBJECT_URIS = new ArrayList();
	
	static{
		ALL_STANDARD_SUBJECT_URIS.add(SUBJECT_URI_ALL);
		ALL_STANDARD_SUBJECT_URIS.add(SUBJECT_URI_AUTHENTICATED);
		ALL_STANDARD_SUBJECT_URIS.add(SUBJECT_URI_OWNER);
		ALL_STANDARD_SUBJECT_URIS.add(SUBJECT_URI_SELF);
		ALL_STANDARD_SUBJECT_URIS.add(SUBJECT_URI_UNAUTHENTICATED);
	}
	
	public static final Privilege PRIVILEGE_ALL = Privilege.ALL;
	public static final Privilege PRIVILEGE_READ = Privilege.READ;
	public static final Privilege PRIVILEGE_READ_ACL = Privilege.READ_ACL;
	public static final Privilege PRIVILEGE_WRITE = Privilege.WRITE;
	public static final Privilege PRIVILEGE_WRITE_ACL = Privilege.WRITE_ACL;
	
	
	
	public static final String PROPERTYNAME_DISPLAY_NAME = "displayname";
	public static final String PROPERTYNAME_PREDECESSOR_SET = "predecessor-set";
	public static final String PROPERTYNAME_SUCCESSOR_SET = "successor-set";
	public static final String PROPERTYNAME_VERSION_NAME = "version-name";
	public static final String PROPERTYNAME_VERSION_TREE = "version-tree";
	public static final String PROPERTYNAME_LATEST_ACTIVITY_VERSION = "latest-activity-version";
	public static final String PROPERTYNAME_CREATOR_DISPLAY_NAME = "creator-displayname";
	public static final String PROPERTYNAME_CHECKED_OUT_SET = "checkedout-set";
	public static final String PROPERTYNAME_CHECKED_OUT = CheckedoutProperty.TAG_NAME;
	public static final String PROPERTYNAME_CHECKED_IN = CheckedinProperty.TAG_NAME;
	public static final String PROPERTYNAME_LOCK_DISCOVERY = "lockdiscovery";
	public static final String PROPERTYNAME_RESOURCE_TYPE = "resourcetype";
	public static final String PROPERTYNAME_CONTENT_TYPE = WebdavResource.GETCONTENTTYPE;
	public static final String PROPERTYNAME_CONTENT_LENGTH = WebdavResource.GETCONTENTLENGTH;
	public static final String PROPERTYNAME_LAST_MODIFIED = WebdavResource.GETLASTMODIFIED;
	public static final String PROPERTYNAME_CREATION_DATE = WebdavResource.CREATIONDATE;
	public static final String PROPERTYNAME_COMMENT = "comment";
	//NON-STANDARD
	//content item
	public static final String PROPERTYNAME_CATEGORY = "categories";
	//content item
	//imagepropertyextractor
    public static final String PROPERTYNAME_HEIGHT = "height";
    public static final String PROPERTYNAME_WIDTH = "width";
    public static final String PROPERTYNAME_BITS_PER_PIXEL = "bits-per-pixel";
    public static final String PROPERTYNAME_DPI = "dpi";
    /**
     * Property for width and height combined to minimize propFind calls. the format is widthxheight e.g. 100x200
     */
	public static final String PROPERTYNAME_WIDTH_AND_HEIGHT_PROPERTY = "widthXheight";
    //imagepropertyextractor
	
	
	public static final PropertyName PROPERTY_DISPLAY_NAME = new PropertyName("DAV:", PROPERTYNAME_DISPLAY_NAME);
	public static final PropertyName PROPERTY_PREDECESSOR_SET = new PropertyName("DAV:", PROPERTYNAME_PREDECESSOR_SET);
	public static final PropertyName PROPERTY_SUCCESSOR_SET = new PropertyName("DAV:", PROPERTYNAME_SUCCESSOR_SET);
	public static final PropertyName PROPERTY_VERSION_NAME = new PropertyName("DAV:", PROPERTYNAME_VERSION_NAME);
	public static final PropertyName PROPERTY_VERSION_TREE = new PropertyName("DAV:", PROPERTYNAME_VERSION_NAME);
	public static final PropertyName PROPERTY_LATEST_ACTIVITY_VERSION = new PropertyName("DAV:", PROPERTYNAME_LATEST_ACTIVITY_VERSION);
	public static final PropertyName PROPERTY_CREATOR_DISPLAY_NAME = new PropertyName("DAV:", PROPERTYNAME_CREATOR_DISPLAY_NAME);
	public static final PropertyName PROPERTY_CHECKED_OUT_SET = new PropertyName("DAV:", PROPERTYNAME_CHECKED_OUT_SET);
	public static final PropertyName PROPERTY_CHECKED_OUT = new PropertyName("DAV:", PROPERTYNAME_CHECKED_OUT);
	public static final PropertyName PROPERTY_CHECKED_IN = new PropertyName("DAV:", PROPERTYNAME_CHECKED_IN);
	public static final PropertyName PROPERTY_LOCK_DISCOVERY = new PropertyName("DAV:", PROPERTYNAME_LOCK_DISCOVERY);
	public static final PropertyName PROPERTY_RESOURCE_TYPE = new PropertyName("DAV:", PROPERTYNAME_RESOURCE_TYPE);
	public static final PropertyName PROPERTY_CONTENT_TYPE = new PropertyName("DAV:", PROPERTYNAME_CONTENT_TYPE);
	public static final PropertyName PROPERTY_CONTENT_LENGTH = new PropertyName("DAV:", PROPERTYNAME_CONTENT_LENGTH);
	public static final PropertyName PROPERTY_LAST_MODIFIED = new PropertyName("DAV:", PROPERTYNAME_LAST_MODIFIED);
	public static final PropertyName PROPERTY_CREATION_DATE = new PropertyName("DAV:", PROPERTYNAME_CREATION_DATE);
	public static final PropertyName PROPERTY_COMMENT = new PropertyName("DAV:", PROPERTYNAME_COMMENT);
	//NON-STANDARD
	//content item
	public static final PropertyName PROPERTY_CATEGORY = new PropertyName("DAV:", PROPERTYNAME_CATEGORY);
	//content item
	//imagepropertyextractor
    public static final PropertyName PROPERTY_HEIGHT =  new PropertyName("DAV:", PROPERTYNAME_HEIGHT);
    public static final PropertyName PROPERTY_WIDTH = new PropertyName("DAV:", PROPERTYNAME_WIDTH);
    public static final PropertyName PROPERTY_BITS_PER_PIXEL = new PropertyName("DAV:", PROPERTYNAME_BITS_PER_PIXEL);
    public static final PropertyName PROPERTY_DPI = new PropertyName("DAV:", PROPERTYNAME_DPI);
    public static final PropertyName PROPERTY_WIDTH_AND_HEIGHT = new PropertyName("DAV:",PROPERTYNAME_WIDTH_AND_HEIGHT_PROPERTY);
    //imagepropertyextractor

    
}
