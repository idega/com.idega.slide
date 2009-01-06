package com.idega.slide.jcr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import org.apache.slide.content.NodeRevisionContent;
/**
 * <p>
 * Wrapper for the specific jcr:content node against Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class SlideContentNode implements Node{

	SlideNode fileNode;
	//private NodeRevisionContent revisionContent;
	private SlideContentProperty contentProperty;
	String name="jcr:content";

	public SlideContentNode(SlideNode slideNode,
			NodeRevisionContent revisionContent) {
		this.fileNode=slideNode;
		this.setRevisionContent(revisionContent);
		this.contentProperty=new SlideContentProperty(this);
	}

	public void addMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public Node addNode(String relPath) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Node addNode(String relPath, String primaryNodeTypeName)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean canAddMixin(String mixinName)
			throws NoSuchNodeTypeException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void cancelMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public Version checkin() throws VersionException,
			UnsupportedRepositoryOperationException, InvalidItemStateException,
			LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void checkout() throws UnsupportedRepositoryOperationException,
			LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void doneMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public Version getBaseVersion()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getCorrespondingNodePath(String workspaceName)
			throws ItemNotFoundException, NoSuchWorkspaceException,
			AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeDefinition getDefinition() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public int getIndex() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Lock getLock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Node getNode(String relPath) throws PathNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeIterator getNodes() throws RepositoryException {
		return new IteratorHelper(new ArrayList());
	}

	public NodeIterator getNodes(String namePattern) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Item getPrimaryItem() throws ItemNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeType getPrimaryNodeType() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public PropertyIterator getProperties() throws RepositoryException {
		ArrayList list = new ArrayList();
		if(this.contentProperty!=null){
			list.add(this.contentProperty);
		}
		return new IteratorHelper(list);
	}

	public PropertyIterator getProperties(String namePattern)
			throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property getProperty(String relPath) throws PathNotFoundException,
			RepositoryException {
		if(relPath.equals(SlideNode.PROPERTY_NAME_DATA)){
			return this.contentProperty;
		}
		else{
			throw new PathNotFoundException("Property "+relPath+" not found");
		}
	}

	public PropertyIterator getReferences() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getUUID() throws UnsupportedRepositoryOperationException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public VersionHistory getVersionHistory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasNode(String relPath) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasNodes() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasProperties() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasProperty(String relPath) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean holdsLock() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isCheckedOut() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isLocked() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isNodeType(String nodeTypeName) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Lock lock(boolean isDeep, boolean isSessionScoped)
			throws UnsupportedRepositoryOperationException, LockException,
			AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeIterator merge(String srcWorkspace, boolean bestEffort)
			throws NoSuchWorkspaceException, AccessDeniedException,
			MergeException, LockException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void orderBefore(String srcChildRelPath, String destChildRelPath)
			throws UnsupportedRepositoryOperationException, VersionException,
			ConstraintViolationException, ItemNotFoundException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void removeMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void restore(String versionName, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void restore(Version version, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void restore(Version version, String relPath, boolean removeExisting)
			throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void restoreByLabel(String versionLabel, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public Property setProperty(String name, Value value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, Value[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, String[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return this.fileNode.setProperty(name, value);
	}

	public Property setProperty(String name, InputStream value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		if(name.equals(SlideNode.PROPERTY_NAME_DATA)){
			if(contentProperty==null){
				contentProperty=new SlideContentProperty(this);
			}
			contentProperty.setValue(value);
			return contentProperty;
		}
		else{
			throw new UnsupportedOperationException("Property: "+SlideNode.PROPERTY_NAME_DATA+" only supported");
		}
	}

	public Property setProperty(String name, boolean value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, double value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, long value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, Calendar value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, Node value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, Value value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, Value[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, String[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property setProperty(String name, String value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void unlock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void update(String srcWorkspaceName)
			throws NoSuchWorkspaceException, AccessDeniedException,
			LockException, InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void accept(ItemVisitor visitor) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public int getDepth() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getName() throws RepositoryException {
		return this.name;
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getPath() throws RepositoryException {
		return this.fileNode.getPath()+"/"+getName();
	}

	public Session getSession() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isModified() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isNew() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isNode() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isSame(Item otherItem) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
		
	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		

		this.fileNode.save();
	}

	public void setRevisionContent(NodeRevisionContent revisionContent) {
		this.fileNode.revisionContent = revisionContent;
	}

	public NodeRevisionContent getRevisionContent() {
		return this.fileNode.revisionContent;
	}
	
	public SlideNode getSlideNode(){
		return this.fileNode;
	}

}
