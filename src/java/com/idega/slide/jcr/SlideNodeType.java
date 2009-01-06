package com.idega.slide.jcr;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
/**
 * <p>
 * Main implementation for the JCR node type in Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideNodeType implements NodeType {

	SlideNode node;
	
	public SlideNodeType(SlideNode slideNode){
		this.node=slideNode;
	}
	
	
	public boolean canAddChildNode(String childNodeName) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canAddChildNode(String childNodeName, String nodeTypeName) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canRemoveItem(String itemName) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canSetProperty(String propertyName, Value value) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canSetProperty(String propertyName, Value[] values) {
		// TODO Auto-generated method stub
		return true;
	}

	public NodeDefinition[] getChildNodeDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeDefinition[] getDeclaredChildNodeDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyDefinition[] getDeclaredPropertyDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeType[] getDeclaredSupertypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.node.type;
	}

	public String getPrimaryItemName() {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyDefinition[] getPropertyDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeType[] getSupertypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasOrderableChildNodes() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMixin() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNodeType(String nodeTypeName) {
		// TODO Auto-generated method stub
		return this.node.type.equals(nodeTypeName);
	}

}
