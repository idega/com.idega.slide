/*
 * $Id: IWSlideConstants.java,v 1.1 2005/01/07 18:47:43 gummi Exp $
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


/**
 * 
 *  Last modified: $Date: 2005/01/07 18:47:43 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IWSlideConstants {
	
	
	public static final String ROLENAME_USERS = "users";
	public static final String ROLENAME_ROOT = "root";
	
	public static final String PATH_USERS = "/users";
	public static final String PATH_GROUPS = "/groups";
	public static final String PATH_ROLES = "/roles";
	
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
	
}
