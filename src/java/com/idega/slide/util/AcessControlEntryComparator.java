/*
 * $Id: AcessControlEntryComparator.java,v 1.1 2005/03/10 23:45:00 gummi Exp $
 * Created on 10.3.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.Comparator;


/**
 * 
 *  Last modified: $Date: 2005/03/10 23:45:00 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class AcessControlEntryComparator implements Comparator {

	/**
	 * 
	 */
	public AcessControlEntryComparator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		int i1 = getPrincipalTypeOrderValue((AccessControlEntry)o1);
		int i2 = getPrincipalTypeOrderValue((AccessControlEntry)o2);
		if(i1==i2){
			return 0;
		} else if(i1>i2){
			return 1;
		} else {
			return -1;
		}
	}
	
	
	public int getPrincipalTypeOrderValue(AccessControlEntry ace){
		int order = 0;
		switch (ace.getPrincipalType()) {
			case AccessControlEntry.PRINCIPAL_TYPE_STANDARD:
				order=-1;
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_ROLE:
				order=-2;
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_GROUP:
				order=-3;
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_USER:
				order=-4;
				break;
			case AccessControlEntry.PRINCIPAL_TYPE_OTHER: //such as owner
				order=-5;
				break;
		}
		return order;
	}
	
}
