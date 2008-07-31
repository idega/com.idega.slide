package com.idega.slide.jcr;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;

import org.xml.sax.ContentHandler;

public class SlideWorkspace implements Workspace {

	private SlideSession slideSession;
	private QueryManager queryManager;
	private ObservationManager observationManager;

	public SlideWorkspace(SlideSession slideSession) {
		this.slideSession=slideSession;
	}

	public void clone(String arg0, String arg1, String arg2, boolean arg3)
			throws NoSuchWorkspaceException, ConstraintViolationException,
			VersionException, AccessDeniedException, PathNotFoundException,
			ItemExistsException, LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void copy(String arg0, String arg1)
			throws ConstraintViolationException, VersionException,
			AccessDeniedException, PathNotFoundException, ItemExistsException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void copy(String arg0, String arg1, String arg2)
			throws NoSuchWorkspaceException, ConstraintViolationException,
			VersionException, AccessDeniedException, PathNotFoundException,
			ItemExistsException, LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public String[] getAccessibleWorkspaceNames() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentHandler getImportContentHandler(String arg0, int arg1)
			throws PathNotFoundException, ConstraintViolationException,
			VersionException, LockException, AccessDeniedException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NamespaceRegistry getNamespaceRegistry() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeTypeManager getNodeTypeManager() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public ObservationManager getObservationManager()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		if(observationManager==null){
			observationManager = new SlideObservationManager(this);
		}
		return observationManager;
	}

	public QueryManager getQueryManager() throws RepositoryException {
		if(queryManager==null){
			queryManager=new SlideQueryManager(this);
		}
		return queryManager;
	}

	public Session getSession() {
		return getSlideSession();
	}

	public SlideSession getSlideSession() {
		return slideSession;
	}

	public void setSlideSession(SlideSession slideSession) {
		this.slideSession = slideSession;
	}

	public void importXML(String arg0, InputStream arg1, int arg2)
			throws IOException, PathNotFoundException, ItemExistsException,
			ConstraintViolationException, InvalidSerializedDataException,
			LockException, AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void move(String arg0, String arg1)
			throws ConstraintViolationException, VersionException,
			AccessDeniedException, PathNotFoundException, ItemExistsException,
			LockException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void restore(Version[] arg0, boolean arg1)
			throws ItemExistsException,
			UnsupportedRepositoryOperationException, VersionException,
			LockException, InvalidItemStateException, RepositoryException {
		// TODO Auto-generated method stub

	}

}
