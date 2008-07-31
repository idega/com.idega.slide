package com.idega.slide.jcr;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

public class SlideQuery implements Query {

	private SlideQueryManager slideQueryManager;

	public SlideQuery(SlideQueryManager slideQueryManager) {
		this.slideQueryManager=slideQueryManager;
	}

	public QueryResult execute() throws RepositoryException {
		// TODO Auto-generated method stub
		return new SlideQueryResult(this);
	}

	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStatement() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStoredQueryPath() throws ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node storeAsNode(String arg0) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException,
			UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
