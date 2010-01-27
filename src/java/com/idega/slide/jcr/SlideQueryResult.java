package com.idega.slide.jcr;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
/**
 * <p>
 * Implementation for the JCR Search mechanism against Slide - Not finished
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideQueryResult implements QueryResult {

	@SuppressWarnings("unused")
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
