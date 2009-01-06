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

/**
 * <p>
 * Implementation for the JCR Search mechanism against Slide - Not finished
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class SlideQuery implements Query {

	private SlideQueryManager slideQueryManager;

	public SlideQuery(SlideQueryManager slideQueryManager) {
		this.setSlideQueryManager(slideQueryManager);
	}

	public QueryResult execute() throws RepositoryException {
		return new SlideQueryResult(this);
	}

	public String getLanguage() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getStatement() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public String getStoredQueryPath() throws ItemNotFoundException,
			RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public Node storeAsNode(String arg0) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException,
			UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public void setSlideQueryManager(SlideQueryManager slideQueryManager) {
		this.slideQueryManager = slideQueryManager;
	}

	public SlideQueryManager getSlideQueryManager() {
		return slideQueryManager;
	}

}
