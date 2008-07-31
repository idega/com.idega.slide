package com.idega.slide.jcr;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

public class SlideQueryResult implements QueryResult {

	private SlideQuery slideQuery;

	public SlideQueryResult(SlideQuery slideQuery) {
		this.slideQuery=slideQuery;
	}

	public String[] getColumnNames() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeIterator getNodes() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowIterator getRows() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
