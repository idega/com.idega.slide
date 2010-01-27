package com.idega.slide.jcr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.content.Content;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionAlreadyExistException;
import org.apache.slide.content.RevisionContentNotFoundException;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.content.RevisionNotFoundException;
import org.apache.slide.event.VetoException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.structure.LinkedObjectNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectHasChildrenException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.Structure;
import org.apache.slide.structure.SubjectNode;

import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;

/**
 * <p>
 * Main implementation for the JCR node object in Slide
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class SlideNode implements Node {

	
	private static final String SLASH = "/";
	public static String PRIMARY_NODETYPE_FOLDER="nt:folder";
	public static String PRIMARY_NODETYPE_FILE="nt:file";
	public static String PRIMARY_NODETYPE_UNSTRUCTURED="nt:unstructured";
	
	public static String NODE_NAME_CONTENT="jcr:content";
	
	public static String PROPERTY_NAME_DATA="jcr:data";
	public static String PROPERTY_NAME_PRIMARYTYPE="jcr:primaryType";
	
	private String path;
	SlideSession slideSession;

	NodeRevisionNumber lastRevision = null;
	NodeRevisionDescriptor revisionDescriptor;
	NodeRevisionContent revisionContent;
	NodeRevisionDescriptors revisions;
	String type=PRIMARY_NODETYPE_UNSTRUCTURED;

	List<Property> properties = new ArrayList<Property>();
	List<Node> nodes = new ArrayList<Node>();
	private ObjectNode objectNode;
	private NodeType primaryNodeType;
	private Structure structure;
	private SlideToken token;
	private Content content;
	private String name;
	boolean isNew=false;
	private SlideVersionHistory slideVersionHistory;
	
	public static int LOGLEVEL_INFO=0;
	public static int LOGLEVEL_DEBUG=1;
	
	public static int LOGLEVEL=LOGLEVEL_INFO;
	

	
	/*public SlideNode(SlideSession slideSession, ObjectNode objectNode) {
		//this(slideSession,path,create,null);
		this.slideSession=slideSession;
		this.objectNode=objectNode;
	}*/
	
	public SlideNode(SlideSession slideSession, String absolutePath, boolean create) throws PathNotFoundException, ItemExistsException {
		this(slideSession,absolutePath,create,null);
	}
	
	public SlideNode(SlideSession slideSession, String absolutePath, boolean create, String type) throws PathNotFoundException, ItemExistsException {
		this.slideSession=slideSession;
		this.path = absolutePath;
		if(type!=null){
			this.type=type;
		}
		
		initialize();
		
		if(create){
			create(path,type);
		}
		else{
			load(path);
		}
		
	}

	private void populateDefaultProperties(List<Property> properties) {
		Property typeProperty = new SlideTransientProperty(this,PROPERTY_NAME_PRIMARYTYPE,this.type);
		properties.add(typeProperty);
		
	}

	private void initialize() {
		structure = this.getSlideSession().getSlideRepository().getStructure();
		token = this.getSlideSession().getToken();
		content = this.getSlideSession().getSlideRepository()
				.getContent();
		
		this.name=path.substring(path.lastIndexOf(SLASH)+1);

	}

	private void create(String nodePath,String type) throws ItemExistsException{
		try{
			//try {
			//	objectNode = structure.retrieve(token, nodePath);
			//	revisions = content.retrieve(token, nodePath);
			//	lastRevision = revisions.getLatestRevision();
			//} catch (ObjectNotFoundException e) {
				SubjectNode subject = new SubjectNode();
				// Create object
				try {
					structure.create(token, subject, nodePath);
					objectNode = structure.retrieve(token, nodePath);
				} catch (ObjectAlreadyExistsException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					throw new ItemExistsException(e1);
				}
			//}
			

			if (lastRevision == null) {
				lastRevision = new NodeRevisionNumber();
			} else {
				lastRevision = new NodeRevisionNumber(lastRevision, false);
			}
			
				
	
				// Node revision descriptor
				IWTimestamp now = IWTimestamp.RightNow();
				revisionDescriptor = new NodeRevisionDescriptor(lastRevision,
						NodeRevisionDescriptors.MAIN_BRANCH, new Vector(),
						new Hashtable());
				
				if(type.equals(PRIMARY_NODETYPE_FILE)){
					revisionDescriptor.setResourceType(CoreConstants.EMPTY);
				}
				
				/*
				 * revisionDescriptor.setResourceType(CoreConstants.EMPTY);
				 */ revisionDescriptor.setSource(CoreConstants.EMPTY);
				 revisionDescriptor.setContentLanguage(Locale.ENGLISH.getLanguage());
				 revisionDescriptor.setLastModified(now.getDate());
				 revisionDescriptor.setETag(computeEtag(path,
				 revisionDescriptor));
				 revisionDescriptor.setCreationDate(now.getDate());
				 
		
				if(this.type.equals(PRIMARY_NODETYPE_FOLDER)){
					//this.setProperty(Property, "<collection/>");
					this.revisionDescriptor.setResourceType("<collection/>");
				}
				isNew=true;
				
				// Create content
				//revisionContent = new NodeRevisionContent();
				// revisionContent.setContent(stream);
	
				// Important to create NodeRevisionDescriptors separately to be
				// able to tell it to use versioning
				//if (lastRevision.toString().equals("1.0")) {
				//	content.create(token, nodePath, true);
	
				//}
				//content.create(token, nodePath, revisionDescriptor,
				//		revisionContent);
				
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.apache.slide.security.AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LinkedObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String computeEtag(String uri, NodeRevisionDescriptor nrd) {
		StringBuffer result = new StringBuffer(String.valueOf(System.currentTimeMillis())).append(CoreConstants.UNDER).append(uri.hashCode()).append(CoreConstants.UNDER);
		result.append(nrd.getLastModified()).append(CoreConstants.UNDER).append(nrd.getContentLength());
		return DigestUtils.md5Hex(result.toString());
	}

	private void load(String nodePath) throws PathNotFoundException{

		try {
			if(nodePath.endsWith("css")){
				//boolean test=true;
			}
			try {
				objectNode = structure.retrieve(token, nodePath);
				revisions = content.retrieve(token,nodePath);
				lastRevision = revisions.getLatestRevision();
				try {
					this.revisionDescriptor = content.retrieve(token, revisions, lastRevision);
				} catch (RevisionDescriptorNotFoundException e) {
					// TODO Auto-generated catch block
					if(LOGLEVEL==LOGLEVEL_DEBUG){
						e.printStackTrace();
					}
					//IWTimestamp now = IWTimestamp.RightNow();
					revisionDescriptor = new NodeRevisionDescriptor(lastRevision,
							NodeRevisionDescriptors.MAIN_BRANCH, new Vector(),
							new Hashtable());
				}
			} catch (ObjectNotFoundException e) {
				/*SubjectNode subject = new SubjectNode();
				// Create object
				try {
					structure.create(token, subject, nodePath);
				} catch (ObjectAlreadyExistsException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				throw new PathNotFoundException(nodePath);
			}
				
			String resourceType = revisionDescriptor.getResourceType();
			
			if(resourceType.contains("<collection/>")){
				this.type=PRIMARY_NODETYPE_FOLDER;
			}
			else{
				this.type=PRIMARY_NODETYPE_FILE;
			}
			

		} catch (org.apache.slide.security.AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LinkedObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Node getContentNode() {
			revisionContent = getRevisionContent();
			SlideContentNode contentNode = new SlideContentNode(this,revisionContent);
			return contentNode;
	}

	public void addMixin(String arg0) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public Node addNode(String path) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		//return new SlideNode(this.getSlideSession(),getPath()+path,true);
		return addNode(path,"nt:unstructured");
	}

	public Node addNode(String path, String type) throws ItemExistsException,
			PathNotFoundException, NoSuchNodeTypeException, LockException,
			VersionException, ConstraintViolationException, RepositoryException {
		if(this.type.equals(PRIMARY_NODETYPE_FILE)){
			if(path.equals(NODE_NAME_CONTENT)){
				return getContentNode();
			}
		}
		String myPath = getPath();
		String subPath = parseChildNodePath(path, myPath);
		Node node = new SlideNode(this.getSlideSession(),subPath,true,type);
		return node;
	}

	public boolean canAddMixin(String arg0) throws NoSuchNodeTypeException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void cancelMerge(Version arg0) throws VersionException,
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

	public void doneMerge(Version arg0) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public Version getBaseVersion()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getCorrespondingNodePath(String arg0)
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

	public Node getNode(String path) throws PathNotFoundException,
			RepositoryException {
		//Specific handling for a slide content (file) node
		if(this.type.equals(PRIMARY_NODETYPE_FILE)){
			if(path.equals(NODE_NAME_CONTENT)){
				if(this.revisions!=null){
					Enumeration revisionNumbers = this.revisions.enumerateRevisionNumbers();
					System.out.println("RevisionNumbers for "+getName()+": ");
					while(revisionNumbers.hasMoreElements()){
						NodeRevisionNumber revisionNumber = (NodeRevisionNumber) revisionNumbers.nextElement();
						System.out.println(revisionNumber.toString()+", ");
					}
				}
				return getContentNode();
			}
		}
		String thisPath = getPath();
		String nodePath=null;
		nodePath = parseChildNodePath(path, thisPath);
		return new SlideNode(this.getSlideSession(),nodePath,false);
	}

	private String parseChildNodePath(String childPath, String thisPath) {
		String nodePath;
		if(childPath.startsWith(SLASH)){
			//When we have a / starting on the childurl then we are most likely the root node
			if(thisPath.equals(SLASH)){
				nodePath = childPath;
			}
			//else if(thisPath.endsWith("/")){
			//	nodePath = thisPath+path;
			//}
			else{
				nodePath = thisPath+SLASH+childPath;
			}
		}
		else{
			if(thisPath.endsWith(SLASH)){
				nodePath = thisPath+childPath;
			}
			else{
				nodePath = thisPath+SLASH+childPath;
			}
		}
		return nodePath;
	}

	public NodeIterator getNodes() throws RepositoryException {
		List children = loadChildren();
		return new IteratorHelper(children);
	}

	private List loadChildren() {
		if(this.nodes.isEmpty()){
			Vector children = this.objectNode.getChildren();
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				
				String child =  (String) iterator.next();
				String path = child;
				SlideNode childNode;
				try {
					childNode = new SlideNode(this.getSlideSession(),path,false);
					this.nodes.add(childNode);
				} catch (PathNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ItemExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if(this.type.equals(PRIMARY_NODETYPE_FILE)){
				this.nodes.add(getContentNode());
			}
		}
		return this.nodes;
	}

	public NodeIterator getNodes(String arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Item getPrimaryItem() throws ItemNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeType getPrimaryNodeType() throws RepositoryException {
		if(primaryNodeType==null){
			primaryNodeType= new SlideNodeType(this);
		}
		return primaryNodeType;
	}

	public PropertyIterator getProperties() throws RepositoryException {
		if(this.getName().endsWith(".css")){
			//boolean test=true;
		}
		List properties = loadProperties();
		return new IteratorHelper(properties);
	}

	private List loadProperties() {
		if(this.properties.isEmpty()){
				Enumeration enumeration = this.revisionDescriptor.enumerateProperties();
				while (enumeration.hasMoreElements()) {
					NodeProperty nodeProp = (NodeProperty) enumeration.nextElement();
					SlideProperty jcrProperty = new SlideProperty(this,nodeProp);
					properties.add(jcrProperty);
				}
				populateDefaultProperties(properties);
			}
		return this.properties;
	}
	
	public PropertyIterator getProperties(String arg0)
			throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Property getProperty(String name) throws PathNotFoundException,RepositoryException{
		return getProperty(name,false);
	}
	
	public Property getProperty(String name,boolean addProperty) throws PathNotFoundException,
			RepositoryException {
		PropertyIterator iter = getProperties();
		while(iter.hasNext()){
			Property prop = iter.nextProperty();
			if(prop.getName().equals(name)){
				return prop;
			}
		}
		if(addProperty){
			
			//NodeProperty nodeProperty = new NodeProperty(name,null);
			
			SlideProperty newProperty = new SlideProperty(this,name);
			//newProperty.setNew(true);
			this.properties.add(newProperty);
			return newProperty;
		}
		else{
			throw new PathNotFoundException("Property: "+name+" not found at node with path"+getPath());
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
		if(slideVersionHistory==null){
			slideVersionHistory=new SlideVersionHistory(this);
		}
		return slideVersionHistory;
	}

	public boolean hasNode(String arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasNodes() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasProperties() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean hasProperty(String arg0) throws RepositoryException {
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

	public boolean isNodeType(String arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Lock lock(boolean arg0, boolean arg1)
			throws UnsupportedRepositoryOperationException, LockException,
			AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public NodeIterator merge(String arg0, boolean arg1)
			throws NoSuchWorkspaceException, AccessDeniedException,
			MergeException, LockException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void orderBefore(String arg0, String arg1)
			throws UnsupportedRepositoryOperationException, VersionException,
			ConstraintViolationException, ItemNotFoundException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void removeMixin(String arg0) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void restore(String arg0, boolean arg1) throws VersionException,
			ItemExistsException, UnsupportedRepositoryOperationException,
			LockException, InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void restore(Version arg0, boolean arg1) throws VersionException,
			ItemExistsException, UnsupportedRepositoryOperationException,
			LockException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void restore(Version arg0, String arg1, boolean arg2)
			throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void restoreByLabel(String arg0, boolean arg1)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public Property setProperty(String propertyName, Value value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, Value[] value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, String[] value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, InputStream value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, boolean value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, double value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, long value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, Calendar value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, Node value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		return property;
	}

	public Property setProperty(String propertyName, Value value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		((SlideProperty)property).getSlidePropertyValue().setType(type);
		return property;
	}

	public Property setProperty(String propertyName, Value[] value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		((SlideProperty)property).getSlidePropertyValue().setType(type);
		return property;
	}

	public Property setProperty(String propertyName, String[] value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		((SlideProperty)property).getSlidePropertyValue().setType(type);
		return property;
	}

	public Property setProperty(String propertyName, String value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property property = getProperty(propertyName,true);
		property.setValue(value);
		((SlideProperty)property).getSlidePropertyValue().setType(type);
		return property;
	}

	public void unlock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void update(String arg0) throws NoSuchWorkspaceException,
			AccessDeniedException, LockException, InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void accept(ItemVisitor arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public Item getAncestor(int arg0) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public int getDepth() throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getName() throws RepositoryException {
		return name;
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getPath() throws RepositoryException {
		return path;
	}

	public Session getSession() throws RepositoryException {
		return getSlideSession();
	}

	public boolean isModified() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isNew() {
		return this.isNew;
	}

	public boolean isNode() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isSame(Item arg0) throws RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void refresh(boolean arg0) throws InvalidItemStateException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");

	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		
		try {
			structure.remove(token, this.objectNode);
		} catch (ObjectNotFoundException e) {
			throw new RepositoryException(e);
		} catch (org.apache.slide.security.AccessDeniedException e) {
			throw new RepositoryException(e);
		} catch (LinkedObjectNotFoundException e) {
			throw new RepositoryException(e);
		} catch (ObjectLockedException e) {
			throw new RepositoryException(e);
		} catch (ServiceAccessException e) {
			throw new RepositoryException(e);
		} catch (VetoException e) {
			throw new RepositoryException(e);
		} catch (ObjectHasChildrenException e) {
			throw new RepositoryException(e);
		}

		try {
			content.remove(token, getPath(), revisionDescriptor);
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			if(LOGLEVEL==LOGLEVEL_DEBUG){
				e.printStackTrace();
			}
		} catch (org.apache.slide.security.AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LinkedObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RevisionDescriptorNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		
		//if(this.isNew()){
			
			try {
					String path = getPath();
					// Important to create NodeRevisionDescriptors separately to be
					// able to tell it to use versioning
					//if(lastRevision!=null){
						if(isNew()){
							if (lastRevision.toString().equals("1.0")) {
									content.create(token, path, true);	
							}
						}
					//}
					if(isContentNode()){
						//if(!isNew()){
						//	incrementRevisionNumber();
						//}
						//NodeRevisionContent content = getRevisionContent();
						NodeRevisionContent cnt = getRevisionContent();
						if(isNew()){
							content.create(token, path, revisionDescriptor, cnt);
						}
						else{
							incrementRevisionNumber();
							content.create(token, path, revisionDescriptor, cnt);
							//content.store(token, path, revisionDescriptor, cnt);
						}
					}
					else{
						content.create(token, path, revisionDescriptor, null);
					}
				
			} catch (ObjectNotFoundException e) {
				// TODO Auto-generated catch block
				if(LOGLEVEL==LOGLEVEL_DEBUG){
					e.printStackTrace();
				}
			} catch (org.apache.slide.security.AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LinkedObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectLockedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RevisionAlreadyExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		//TODO: Look at this, now commit is only done in Session.save().
		
		/*
			this.getSlideSession().getSlideRepository().getNamespace().commit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	protected boolean isContentNode() throws RepositoryException {
		NodeType myType = this.getPrimaryNodeType();
		if(myType!=null){
			if(myType.getName().equals(PRIMARY_NODETYPE_FOLDER)){
				return false;
			}
			else if(myType.getName().equals(PRIMARY_NODETYPE_UNSTRUCTURED)){
				return false;
			}
		}
		return true;
	}

	private NodeRevisionContent getRevisionContent() {
		if(this.revisionContent==null){
			try {
				revisionContent = content.retrieve(token, getPath(), revisionDescriptor);
			} catch (ObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.apache.slide.security.AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RevisionNotFoundException e) {
				if(!this.isNew()){
					if(LOGLEVEL==LOGLEVEL_DEBUG){
						e.printStackTrace();
					}
				}
				revisionContent = new NodeRevisionContent();
			} catch (LinkedObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RevisionContentNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				revisionContent = new NodeRevisionContent();
			} catch (ObjectLockedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return revisionContent;
	}

	public void incrementRevisionNumber() {
		//String strUri = getPath();
		//Uri objectUri = this.getSlideSession().getSlideRepository().getNamespace().getUri(token, strUri);
		
			lastRevision = new NodeRevisionNumber(lastRevision, false);
			// Node revision descriptor
			//IWTimestamp now = IWTimestamp.RightNow();
			NodeRevisionDescriptor newRevisionDescriptor = new NodeRevisionDescriptor(lastRevision,
					NodeRevisionDescriptors.MAIN_BRANCH, new Vector(),
					new Hashtable());
			
			Enumeration properties = revisionDescriptor.enumerateProperties();
			while(properties.hasMoreElements()){
				NodeProperty property = (NodeProperty) properties.nextElement();
				newRevisionDescriptor.setProperty(property);
			}
			newRevisionDescriptor.setResourceType(revisionDescriptor.getResourceType());
			revisionDescriptor=newRevisionDescriptor;
	}

	public SlideSession getSlideSession() {
		return slideSession;
	}

	public void setSlideSession(SlideSession slideSession) {
		this.slideSession = slideSession;
	}

	public String toString(){
		try {
			return "SlideNode: "+getPath()+" "+getPrimaryNodeType().getName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.toString();
	}
}
