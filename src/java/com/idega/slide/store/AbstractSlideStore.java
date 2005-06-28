/*
 * $Id: AbstractSlideStore.java,v 1.2 2005/06/28 12:09:52 tryggvil Exp $
 * Created on Jun 27, 2005 in project com.idega.slide
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.store;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.common.Namespace;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.Scope;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceConnectionFailedException;
import org.apache.slide.common.ServiceDisconnectionFailedException;
import org.apache.slide.common.ServiceInitializationFailedException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.common.ServiceResetFailedException;
import org.apache.slide.common.Uri;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionAlreadyExistException;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.content.RevisionNotFoundException;
import org.apache.slide.lock.LockTokenNotFoundException;
import org.apache.slide.lock.NodeLock;
import org.apache.slide.search.basic.IBasicExpressionFactory;
import org.apache.slide.search.basic.IBasicExpressionFactoryProvider;
import org.apache.slide.security.NodePermission;
import org.apache.slide.store.ContentStore;
import org.apache.slide.store.LockStore;
import org.apache.slide.store.NodeStore;
import org.apache.slide.store.RevisionDescriptorStore;
import org.apache.slide.store.RevisionDescriptorsStore;
import org.apache.slide.store.SecurityStore;
import org.apache.slide.store.SequenceStore;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.util.logger.Logger;


/**
 * <p>
 * Simple wrapper class around the standard Slide Store interfaces
 * </p>
 *  Last modified: $Date: 2005/06/28 12:09:52 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class AbstractSlideStore 
	implements Service,NodeStore,ContentStore,LockStore,RevisionDescriptorsStore,RevisionDescriptorStore,
	SecurityStore,SequenceStore,IBasicExpressionFactoryProvider{
	
	//private Hashtable _parameters = new Hashtable();
	private Service service;
	private NodeStore nodeStore;
	private ContentStore contentStore;
	private LockStore lockStore;
	private RevisionDescriptorsStore revisionDescriptorsStore;
	private RevisionDescriptorStore revisionDescriptorStore;
	private SecurityStore securityStore;
	private SequenceStore sequenceStore;
	private IBasicExpressionFactoryProvider ibasicExpressionFactoryProvider;
	
	/**
	 * 
	 */
	public AbstractSlideStore() {
	}

	
	/**
	 * @return Returns the contentStore.
	 */
	protected ContentStore getContentStore() {
		if(contentStore==null){
			throw new RuntimeException(this.getClass().getName()+": ContentStore instance it not set");
		}
		return contentStore;
	}

	
	/**
	 * @param contentStore The contentStore to set.
	 */
	protected void setContentStore(ContentStore contentStore) {
		this.contentStore = contentStore;
	}

	
	/**
	 * @return Returns the lockStore.
	 */
	protected LockStore getLockStore() {
		if(lockStore==null){
			throw new RuntimeException(this.getClass().getName()+": LockStore instance it not set");
		}
		return lockStore;
	}

	
	/**
	 * @param lockStore The lockStore to set.
	 */
	protected void setLockStore(LockStore lockStore) {
		this.lockStore = lockStore;
	}

	
	/**
	 * @return Returns the nodeStore.
	 */
	protected NodeStore getNodeStore() {
		if(nodeStore==null){
			throw new RuntimeException(this.getClass().getName()+": NodeStore instance it not set");
		}
		return nodeStore;
	}

	
	/**
	 * @param nodeStore The nodeStore to set.
	 */
	protected void setNodeStore(NodeStore nodeStore) {
		this.nodeStore = nodeStore;
	}

	
	/**
	 * @return Returns the revisionDescriptorsStore.
	 */
	protected RevisionDescriptorsStore getRevisionDescriptorsStore() {
		if(revisionDescriptorsStore==null){
			throw new RuntimeException(this.getClass().getName()+": RevisionDescriptorsStore instance it not set");
		}
		return revisionDescriptorsStore;
	}

	
	/**
	 * @param revisionDescriptorsStore The revisionDescriptorsStore to set.
	 */
	protected void setRevisionDescriptorsStore(RevisionDescriptorsStore revisionDescriptorsStore) {
		this.revisionDescriptorsStore = revisionDescriptorsStore;
	}

	
	/**
	 * @return Returns the revisionDescriptorStore.
	 */
	protected RevisionDescriptorStore getRevisionDescriptorStore() {
		if(revisionDescriptorStore==null){
			throw new RuntimeException(this.getClass().getName()+": RevisionDescriptorStore instance it not set");
		}
		return revisionDescriptorStore;
	}

	
	/**
	 * @param revisionDescriptorStore The revisionDescriptorStore to set.
	 */
	protected void setRevisionDescriptorStore(RevisionDescriptorStore revisionDescriptorStore) {
		this.revisionDescriptorStore = revisionDescriptorStore;
	}

	
	/**
	 * @return Returns the service.
	 */
	protected Service getService() {
		if(service==null){
			throw new RuntimeException(this.getClass().getName()+": Service instance it not set");
		}
		return service;
	}

	
	/**
	 * @param service The service to set.
	 */
	protected void setService(Service service) {
		this.service = service;
	}

	
	/**
	 * @return Returns the ibasicExpressionFactoryProvider.
	 */
	protected IBasicExpressionFactoryProvider getIbasicExpressionFactoryProvider() {
		if(ibasicExpressionFactoryProvider==null){
			throw new RuntimeException(this.getClass().getName()+": IBasicExpressionFactoryProvider instance it not set");
		}
		return ibasicExpressionFactoryProvider;
	}


	
	/**
	 * @param ibasicExpressionFactoryProvider The ibasicExpressionFactoryProvider to set.
	 */
	protected void setIbasicExpressionFactoryProvider(IBasicExpressionFactoryProvider ibasicExpressionFactoryProvider) {
		this.ibasicExpressionFactoryProvider = ibasicExpressionFactoryProvider;
	}


	
	/**
	 * @return Returns the securityStore.
	 */
	protected SecurityStore getSecurityStore() {
		if(securityStore==null){
			throw new RuntimeException(this.getClass().getName()+": SecurityStore instance it not set");
		}
		return securityStore;
	}


	
	/**
	 * @param securityStore The securityStore to set.
	 */
	protected void setSecurityStore(SecurityStore securityStore) {
		this.securityStore = securityStore;
	}


	
	/**
	 * @return Returns the sequenceStore.
	 */
	protected SequenceStore getSequenceStore() {
		if(sequenceStore==null){
			throw new RuntimeException(this.getClass().getName()+": SequenceyStore instance it not set");
		}
		return sequenceStore;
	}


	
	/**
	 * @param sequenceStore The sequenceStore to set.
	 */
	protected void setSequenceStore(SequenceStore sequenceStore) {
		this.sequenceStore = sequenceStore;
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.ContentStore#createRevisionContent(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor, org.apache.slide.content.NodeRevisionContent)
	 */
	public void createRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor, NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionAlreadyExistException {
		contentStore.createRevisionContent(uri, revisionDescriptor, revisionContent);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.ContentStore#removeRevisionContent(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor)
	 */
	public void removeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException {
		contentStore.removeRevisionContent(uri, revisionDescriptor);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.ContentStore#retrieveRevisionContent(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor)
	 */
	public NodeRevisionContent retrieveRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException, RevisionNotFoundException {
		return contentStore.retrieveRevisionContent(uri, revisionDescriptor);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.ContentStore#storeRevisionContent(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor, org.apache.slide.content.NodeRevisionContent)
	 */
	public void storeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor, NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionNotFoundException {
		contentStore.storeRevisionContent(uri, revisionDescriptor, revisionContent);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.LockStore#enumerateLocks(org.apache.slide.common.Uri)
	 */
	public Enumeration enumerateLocks(Uri uri) throws ServiceAccessException {
		return lockStore.enumerateLocks(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.LockStore#killLock(org.apache.slide.common.Uri, org.apache.slide.lock.NodeLock)
	 */
	public void killLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
		lockStore.killLock(uri, lock);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.LockStore#putLock(org.apache.slide.common.Uri, org.apache.slide.lock.NodeLock)
	 */
	public void putLock(Uri uri, NodeLock lock) throws ServiceAccessException {
		lockStore.putLock(uri, lock);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.LockStore#removeLock(org.apache.slide.common.Uri, org.apache.slide.lock.NodeLock)
	 */
	public void removeLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
		lockStore.removeLock(uri, lock);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.LockStore#renewLock(org.apache.slide.common.Uri, org.apache.slide.lock.NodeLock)
	 */
	public void renewLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
		lockStore.renewLock(uri, lock);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.NodeStore#createObject(org.apache.slide.common.Uri, org.apache.slide.structure.ObjectNode)
	 */
	public void createObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectAlreadyExistsException {
		getNodeStore().createObject(uri, object);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.NodeStore#removeObject(org.apache.slide.common.Uri, org.apache.slide.structure.ObjectNode)
	 */
	public void removeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
		getNodeStore().removeObject(uri, object);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.NodeStore#retrieveObject(org.apache.slide.common.Uri)
	 */
	public ObjectNode retrieveObject(Uri uri) throws ServiceAccessException, ObjectNotFoundException {
		return getNodeStore().retrieveObject(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.NodeStore#storeObject(org.apache.slide.common.Uri, org.apache.slide.structure.ObjectNode)
	 */
	public void storeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
		getNodeStore().storeObject(uri, object);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorsStore#createRevisionDescriptors(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptors)
	 */
	public void createRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors) throws ServiceAccessException {
		getRevisionDescriptorsStore().createRevisionDescriptors(uri, revisionDescriptors);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorsStore#removeRevisionDescriptors(org.apache.slide.common.Uri)
	 */
	public void removeRevisionDescriptors(Uri uri) throws ServiceAccessException {
		getRevisionDescriptorsStore().removeRevisionDescriptors(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorsStore#retrieveRevisionDescriptors(org.apache.slide.common.Uri)
	 */
	public NodeRevisionDescriptors retrieveRevisionDescriptors(Uri uri) throws ServiceAccessException, RevisionDescriptorNotFoundException {
		return getRevisionDescriptorsStore().retrieveRevisionDescriptors(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorsStore#storeRevisionDescriptors(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptors)
	 */
	public void storeRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors) throws ServiceAccessException, RevisionDescriptorNotFoundException {
		getRevisionDescriptorsStore().storeRevisionDescriptors(uri, revisionDescriptors);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorStore#createRevisionDescriptor(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor)
	 */
	public void createRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException {
		getRevisionDescriptorStore().createRevisionDescriptor(uri, revisionDescriptor);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorStore#retrieveRevisionDescriptor(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionNumber)
	 */
	public NodeRevisionDescriptor retrieveRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber) throws ServiceAccessException, RevisionDescriptorNotFoundException {
		return getRevisionDescriptorStore().retrieveRevisionDescriptor(uri, revisionNumber);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorStore#storeRevisionDescriptor(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionDescriptor)
	 */
	public void storeRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException, RevisionDescriptorNotFoundException {
		getRevisionDescriptorStore().storeRevisionDescriptor(uri, revisionDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.apache.slide.store.RevisionDescriptorStore#removeRevisionDescriptor(org.apache.slide.common.Uri, org.apache.slide.content.NodeRevisionNumber)
	 */
	public void removeRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber) throws ServiceAccessException {
		getRevisionDescriptorStore().removeRevisionDescriptor(uri,revisionNumber);
	}

	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#cacheResults()
	 */
	public boolean cacheResults() {
		return getService().cacheResults();
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid, boolean)
	 */
	public void commit(Xid arg0, boolean arg1) throws XAException {
		getService().commit(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#connect()
	 */
	public void connect() throws ServiceConnectionFailedException {
		getService().connect();
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#connect(org.apache.slide.authenticate.CredentialsToken)
	 */
	public void connect(CredentialsToken crdtoken) throws ServiceConnectionFailedException {
		getService().connect(crdtoken);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#connectIfNeeded()
	 */
	public boolean connectIfNeeded() throws ServiceConnectionFailedException, ServiceAccessException {
		return getService().connectIfNeeded();
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#connectIfNeeded(org.apache.slide.authenticate.CredentialsToken)
	 */
	public boolean connectIfNeeded(CredentialsToken crdtoken) throws ServiceConnectionFailedException, ServiceAccessException {
		return getService().connectIfNeeded(crdtoken);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#disconnect()
	 */
	public void disconnect() throws ServiceDisconnectionFailedException {
		getService().disconnect();
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid, int)
	 */
	public void end(Xid arg0, int arg1) throws XAException {
		getService().end(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
	 */
	public void forget(Xid arg0) throws XAException {
		getService().forget(arg0);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#getLogger()
	 */
	public Logger getLogger() {
		return getService().getLogger();
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
	 */
	public int getTransactionTimeout() throws XAException {
		return getService().getTransactionTimeout();
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#initialize(org.apache.slide.common.NamespaceAccessToken)
	 */
	public void initialize(NamespaceAccessToken token) throws ServiceInitializationFailedException {
		getService().initialize(token);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#isConnected()
	 */
	public boolean isConnected() throws ServiceAccessException {
		return getService().isConnected();
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
	 */
	public boolean isSameRM(XAResource arg0) throws XAException {
		return getService().isSameRM(arg0);
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
	 */
	public int prepare(Xid arg0) throws XAException {
		return getService().prepare(arg0);
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#recover(int)
	 */
	public Xid[] recover(int arg0) throws XAException {
		return getService().recover(arg0);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#reset()
	 */
	public void reset() throws ServiceResetFailedException {
		getService().reset();
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
	 */
	public void rollback(Xid arg0) throws XAException {
		getService().rollback(arg0);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#setNamespace(org.apache.slide.common.Namespace)
	 */
	public void setNamespace(Namespace namespace) {
		getService().setNamespace(namespace);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#setParameters(java.util.Hashtable)
	 */
	public void setParameters(Hashtable parameters) throws ServiceParameterErrorException, ServiceParameterMissingException {
		getService().setParameters(parameters);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.common.Service#setScope(org.apache.slide.common.Scope)
	 */
	public void setScope(Scope scope) {
		getService().setScope(scope);
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
	 */
	public boolean setTransactionTimeout(int arg0) throws XAException {
		return getService().setTransactionTimeout(arg0);
	}


	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid, int)
	 */
	public void start(Xid arg0, int arg1) throws XAException {
		getService().start(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.search.basic.IBasicExpressionFactoryProvider#getBasicExpressionFactory()
	 */
	public IBasicExpressionFactory getBasicExpressionFactory() {
		return getIbasicExpressionFactoryProvider().getBasicExpressionFactory();
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SecurityStore#grantPermission(org.apache.slide.common.Uri, org.apache.slide.security.NodePermission)
	 */
	public void grantPermission(Uri uri, NodePermission permission) throws ServiceAccessException {
		getSecurityStore().grantPermission(uri, permission);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SecurityStore#revokePermission(org.apache.slide.common.Uri, org.apache.slide.security.NodePermission)
	 */
	public void revokePermission(Uri uri, NodePermission permission) throws ServiceAccessException {
		getSecurityStore().revokePermission(uri, permission);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SecurityStore#revokePermissions(org.apache.slide.common.Uri)
	 */
	public void revokePermissions(Uri uri) throws ServiceAccessException {
		getSecurityStore().revokePermissions(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SecurityStore#enumeratePermissions(org.apache.slide.common.Uri)
	 */
	public Enumeration enumeratePermissions(Uri uri) throws ServiceAccessException {
		return getSecurityStore().enumeratePermissions(uri);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SequenceStore#createSequence(java.lang.String)
	 */
	public boolean createSequence(String sequenceName) throws ServiceAccessException {
		return getSequenceStore().createSequence(sequenceName);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SequenceStore#isSequenceSupported()
	 */
	public boolean isSequenceSupported() {
		return getSequenceStore().isSequenceSupported();
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SequenceStore#nextSequenceValue(java.lang.String)
	 */
	public long nextSequenceValue(String sequenceName) throws ServiceAccessException {
		return getSequenceStore().nextSequenceValue(sequenceName);
	}


	/* (non-Javadoc)
	 * @see org.apache.slide.store.SequenceStore#sequenceExists(java.lang.String)
	 */
	public boolean sequenceExists(String sequenceName) throws ServiceAccessException {
		return getSequenceStore().sequenceExists(sequenceName);
	}
	
}
