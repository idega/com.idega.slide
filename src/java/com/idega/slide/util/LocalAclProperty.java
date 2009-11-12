package com.idega.slide.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.slide.security.NodePermission;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.properties.AclProperty;

import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class LocalAclProperty extends AclProperty {
	
	private Collection<NodePermission> nodePermissions;
	
	public LocalAclProperty(Enumeration<NodePermission> nodePermissions) {
		this(Collections.list(nodePermissions));
	}
	
	public LocalAclProperty(Collection<NodePermission> nodePermissions) {
		super(null, null);
		
		this.nodePermissions = nodePermissions;
	}
	
	@Override
	public Ace[] getAces() {
		if (ListUtil.isEmpty(nodePermissions)) {
			return null;
		}
		
		List<Ace> aces = new ArrayList<Ace>();
		for (NodePermission permission: nodePermissions) {
			String subjectUri = permission.getSubjectUri();
			if (!StringUtil.isEmpty(subjectUri) && !IWSlideConstants.ALL_STANDARD_SUBJECT_URIS.contains(subjectUri)) {
				subjectUri = CoreConstants.WEBDAV_SERVLET_URI.concat(subjectUri);
			}
			Ace ace = new Ace(subjectUri);
			
			ace.setInheritable(permission.isInheritable());
			String inheritedFrom = permission.getInheritedFrom();
			ace.setInheritedFrom(inheritedFrom);
			ace.setInherited(!StringUtil.isEmpty(inheritedFrom));
			ace.setNegative(permission.isNegative());
			ace.setProtected(permission.isProtected());
			ace.addPrivilege(new Privilege(IWSlideConstants.DAV_NAME_SPACE, permission.getActionUri(), null));
			
			aces.add(ace);
		}
		
		return ArrayUtil.convertListToArray(aces);
	}
	
}