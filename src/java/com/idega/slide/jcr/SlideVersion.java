package com.idega.slide.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
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

/**
 * <p>
 * JCR Version implementation for Slide - NOT FINISHED
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideVersion implements Version {

	SlideVersionHistory slideVersionHistory;
	
	
	public VersionHistory getContainingHistory() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Calendar getCreated() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Version[] getPredecessors() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Version[] getSuccessors() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public Node addNode(String relPath) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node addNode(String relPath, String primaryNodeTypeName)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canAddMixin(String mixinName)
			throws NoSuchNodeTypeException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public void cancelMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public Version checkin() throws VersionException,
			UnsupportedRepositoryOperationException, InvalidItemStateException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkout() throws UnsupportedRepositoryOperationException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void doneMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public Version getBaseVersion()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCorrespondingNodePath(String workspaceName)
			throws ItemNotFoundException, NoSuchWorkspaceException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeDefinition getDefinition() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIndex() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Lock getLock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getNode(String relPath) throws PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeIterator getNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeIterator getNodes(String namePattern) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Item getPrimaryItem() throws ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeType getPrimaryNodeType() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyIterator getProperties() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyIterator getProperties(String namePattern)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property getProperty(String relPath) throws PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyIterator getReferences() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUUID() throws UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public VersionHistory getVersionHistory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNode(String relPath) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasProperties() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasProperty(String relPath) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean holdsLock() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCheckedOut() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocked() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNodeType(String nodeTypeName) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public Lock lock(boolean isDeep, boolean isSessionScoped)
			throws UnsupportedRepositoryOperationException, LockException,
			AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeIterator merge(String srcWorkspace, boolean bestEffort)
			throws NoSuchWorkspaceException, AccessDeniedException,
			MergeException, LockException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void orderBefore(String srcChildRelPath, String destChildRelPath)
			throws UnsupportedRepositoryOperationException, VersionException,
			ConstraintViolationException, ItemNotFoundException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void removeMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void restore(String versionName, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void restore(Version version, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void restore(Version version, String relPath, boolean removeExisting)
			throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void restoreByLabel(String versionLabel, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public Property setProperty(String name, Value value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, Value[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, String[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, InputStream value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, boolean value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, double value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, long value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, Calendar value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, Node value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, Value value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, Value[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, String[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property setProperty(String name, String value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void unlock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void update(String srcWorkspaceName)
			throws NoSuchWorkspaceException, AccessDeniedException,
			LockException, InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void accept(ItemVisitor visitor) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDepth() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSame(Item otherItem) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Property setProperty(String name, Binary value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, BigDecimal value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getNodes(String[] nameGlobs) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getProperties(String[] nameGlobs)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getReferences(String name)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getWeakReferences() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getWeakReferences(String name)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrimaryType(String nodeTypeName)
			throws NoSuchNodeTypeException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeIterator getSharedSet() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSharedSet() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeShare() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void followLifecycleTransition(String transition)
			throws UnsupportedRepositoryOperationException,
			InvalidLifecycleTransitionException, RepositoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getAllowedLifecycleTransistions()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getLinearSuccessor() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getLinearPredecessor() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getFrozenNode() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
