package com.idega.slide.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.slide.search.Search;
/**
 * <p>
 * Implementation for the JCR Search mechanism against Slide - Not finished
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SlideQueryManager implements QueryManager {

	private SlideRepository slideRepository;
	private Search searchHelper;
	
	public SlideQueryManager(SlideRepository slideRepository) {
		this.slideRepository=slideRepository;
		searchHelper = slideRepository.getNamespace().getSearchHelper();
	}

	public Query createQuery(String arg0, String arg1)
			throws InvalidQueryException, RepositoryException {
		// TODO Auto-generated method stub
		
		/*
        searchQuery = searchHelper.createSearchQuery
        (grammarNamespace, queryElement, slideToken, maxDepth,
         new ComputedPropertyProvider(token, slideToken, 
               getSlideContextPath(), getConfig()),
         req.getRequestURI ());
    
    	requestedProperties = searchQuery.requestedProperties ();
		*/
		
		/*
		 * result = searchHelper.search (slideToken, searchQuery);
		 * 
		 */
		
		return new SlideQuery(this);
	}

	public Query getQuery(Node arg0) throws InvalidQueryException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getSupportedQueryLanguages() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
