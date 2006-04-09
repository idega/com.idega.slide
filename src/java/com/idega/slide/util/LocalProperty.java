/*
 * $Id: LocalProperty.java,v 1.2 2006/04/09 11:44:15 laddi Exp $
 * Created on 21.1.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.util;

import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.ResponseEntity;
import org.w3c.dom.Element;

public class LocalProperty implements Property{
		String name;
		String localName;
		String namespaceURI;
		Element element;
		ResponseEntity response;
		String propertyAsString;
		
		/**
		 * @param response2
		 */
		public LocalProperty(ResponseEntity response2) {
			setResponse(response2);
		}

		/**
		 * @return Returns the element.
		 */
		public Element getElement() {
			if(this.element==null){
				throw new UnsupportedOperationException("Method getElement() not implemented");
			}
			return this.element;
		}
		
		/**
		 * @param element The element to set.
		 */
		public void setElement(Element element) {

			this.element = element;
		}
		
		/**
		 * @return Returns the localName.
		 */
		public String getLocalName() {
			return this.localName;
		}
		
		/**
		 * @param localName The localName to set.
		 */
		public void setLocalName(String localName) {
			this.localName = localName;
		}
		
		/**
		 * @return Returns the name.
		 */
		public String getName() {
			//return name;
			return getNamespaceURI()+getLocalName();
		}
		
		/**
		 * @param name The name to set.
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * @return Returns the namespaceURI.
		 */
		public String getNamespaceURI() {
			return this.namespaceURI;
		}
		
		/**
		 * @param namespaceURI The namespaceURI to set.
		 */
		public void setNamespaceURI(String namespaceURI) {
			this.namespaceURI = namespaceURI;
		}
		
		/**
		 * @return Returns the response.
		 */
		public ResponseEntity getResponse() {
			return this.response;
		}
		
		/**
		 * @param response The response to set.
		 */
		public void setResponse(ResponseEntity response) {
			this.response = response;
		}

		/* (non-Javadoc)
		 * @see org.apache.webdav.lib.Property#getPropertyAsString()
		 */
		public String getPropertyAsString() {
			return this.propertyAsString;
		}
		
		public void setPropertyAsString(String value){
			this.propertyAsString=value;
		}

		/* (non-Javadoc)
		 * @see org.apache.webdav.lib.Property#getStatusCode()
		 */
		public int getStatusCode() {
			return getResponse().getStatusCode();
		}

		/* (non-Javadoc)
		 * @see org.apache.webdav.lib.Property#getOwningURL()
		 */
		public String getOwningURL() {
			return getResponse().getHref();
		}
	
}