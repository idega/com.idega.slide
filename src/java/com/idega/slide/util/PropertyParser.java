/*
 * $Id: PropertyParser.java,v 1.3 2007/02/04 20:42:22 valdas Exp $
 * Created on 15.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Class for parsing and encoding properties in slide.
 * 
 *  Last modified: $Date: 2007/02/04 20:42:22 $ by $Author: valdas $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.3 $
 */
public class PropertyParser {
	/**
	 * @param namespace The property namespace, default is "DAV:" if namespace is null
	 * @param propertySet Set of properties, usually a path from the application server root including context.
	 * @return
	 */
	public static String encodePropertyString(String namespace, Set propertySet) {
		String pNamespace = (namespace==null)?"DAV:":namespace;
		String newGroupMemberSet = "";
		for (Iterator iter = propertySet.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			newGroupMemberSet += "<D:href xmlns:D=\""+pNamespace+"\">"+path+"</D:href>";
		}
		return newGroupMemberSet;
	}

	/**
	 * Parses property of the format "<D:href xmlns:D="namespace">property</D:href>" and returns
	 * set of properties.
	 * 
	 * @param namespace The property namespace, default is "DAV:" if namespace is null
	 * @return Set of property values, usually a path from the application server root including context.
	 * @throws RemoteException
	 */
	public static Set parsePropertyString(String namespace, String propertyString) {
		String pNamespace = (namespace==null)?"DAV:":namespace;
		//Skips first token because it is what is before the first <D:href...., usually ""
		boolean skipFirst = false;
		String[] tokens = propertyString.split("<D:href xmlns:D=\""+pNamespace+"\">");
		for (int i = 0; i < tokens.length; i++) {
			int closeTagIndex = tokens[i].indexOf("</D:href>");
			if(closeTagIndex >-1){
				tokens[i] = tokens[i].substring(0,closeTagIndex);
			} else if (i==0){
				skipFirst = true;
			}
				
		}
		Set propertySet = new LinkedHashSet();
		//Skips first token because it is what is before the first <D:href...., usually ""
		for (int i = ((skipFirst)?1:0); i < tokens.length; i++) {
			propertySet.add(tokens[i]);
		}
		return propertySet;
	}
}
