/*
 * $Id: PropertyParser.java,v 1.1 2004/12/15 16:35:47 gummi Exp $
 * Created on 15.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Class for parsing and encoding properties in slide.
 * 
 *  Last modified: $Date: 2004/12/15 16:35:47 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
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
	public static Set parsePropertyString(String namespace, String propertyString, boolean propertyAlwaysIncludesSlash) {
		String pNamespace = (namespace==null)?"DAV:":namespace;
		String[] tokens = propertyString.split("<D:href xmlns:D=\""+pNamespace+"\">");
		for (int i = 0; i < tokens.length; i++) {
			int closeTagIndex = tokens[i].indexOf("</D:href>");
			if(closeTagIndex >-1){
				tokens[i] = tokens[i].substring(0,closeTagIndex);
//				System.out.println("\t"+tokens[i]);
			}
		}
		Set propertySet = new LinkedHashSet();
		for (int i = 0; i < tokens.length; i++) {
			if(!propertyAlwaysIncludesSlash || tokens[i].indexOf("/")!=-1){
				propertySet.add(tokens[i]);
			}
		}
		return propertySet;
	}
}
