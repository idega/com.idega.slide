package com.idega.slide.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

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
import javax.jcr.version.VersionIterator;

/**
 * <p>
 * JCR Version implementation for Slide - NOT FINISHED
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */

public class SlideVersionHistory implements VersionHistory {


	List<SlideVersion> versions;
	private SlideNode slideNode;

	public SlideVersionHistory(SlideNode slideNode) {
		this.slideNode=slideNode;
	}

	@Override
	public void addVersionLabel(String versionName, String label,
			boolean moveLabel) throws VersionException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public VersionIterator getAllVersions() throws RepositoryException {
		if(versions==null){
			versions=new ArrayList<SlideVersion>();
		}
		if(versions.isEmpty()){
			loadVersions(versions);
		}
		return new IteratorHelper<SlideVersion>(versions);
	}

	@SuppressWarnings("unused")
	private void loadVersions(List<SlideVersion> versions2) {
		Enumeration enumer = this.slideNode.revisions.enumerateRevisionNumbers();
		while(enumer.hasMoreElements()){
			String number = (String) enumer.nextElement();
			SlideVersion version = new SlideVersion();
		}
	}

	@Override
	public Version getRootVersion() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getVersion(String versionName) throws VersionException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getVersionByLabel(String label) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getVersionLabels() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getVersionLabels(Version version) throws VersionException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersionableUUID() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasVersionLabel(String label) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasVersionLabel(Version version, String label)
			throws VersionException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeVersion(String versionName)
			throws ReferentialIntegrityException, AccessDeniedException,
			UnsupportedRepositoryOperationException, VersionException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeVersionLabel(String label) throws VersionException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Node addNode(String relPath) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node addNode(String relPath, String primaryNodeTypeName)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canAddMixin(String mixinName)
			throws NoSuchNodeTypeException, RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Version checkin() throws VersionException,
			UnsupportedRepositoryOperationException, InvalidItemStateException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkout() throws UnsupportedRepositoryOperationException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doneMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Version getBaseVersion()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCorrespondingNodePath(String workspaceName)
			throws ItemNotFoundException, NoSuchWorkspaceException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeDefinition getDefinition() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIndex() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Lock getLock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNode(String relPath) throws PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getNodes(String namePattern) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item getPrimaryItem() throws ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeType getPrimaryNodeType() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getProperties() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getProperties(String namePattern)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getProperty(String relPath) throws PathNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIterator getReferences() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUUID() throws UnsupportedRepositoryOperationException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VersionHistory getVersionHistory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNode(String relPath) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasProperties() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasProperty(String relPath) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean holdsLock() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCheckedOut() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocked() throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNodeType(String nodeTypeName) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Lock lock(boolean isDeep, boolean isSessionScoped)
			throws UnsupportedRepositoryOperationException, LockException,
			AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator merge(String srcWorkspace, boolean bestEffort)
			throws NoSuchWorkspaceException, AccessDeniedException,
			MergeException, LockException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void orderBefore(String srcChildRelPath, String destChildRelPath)
			throws UnsupportedRepositoryOperationException, VersionException,
			ConstraintViolationException, ItemNotFoundException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(String versionName, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(Version version, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(Version version, String relPath, boolean removeExisting)
			throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreByLabel(String versionLabel, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Property setProperty(String name, Value value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, Value[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, String[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, InputStream value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, boolean value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, double value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, long value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, Calendar value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, Node value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, Value value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, Value[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, String[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property setProperty(String name, String value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unlock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(String srcWorkspaceName)
			throws NoSuchWorkspaceException, AccessDeniedException,
			LockException, InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void accept(ItemVisitor visitor) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDepth() throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSame(Item otherItem) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
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
	public String getVersionableIdentifier() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VersionIterator getAllLinearVersions() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getAllLinearFrozenNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getAllFrozenNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
