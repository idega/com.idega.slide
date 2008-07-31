package com.idega.slide.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.slide.search.Search;
import org.apache.slide.webdav.util.ComputedPropertyProvider;

public class SlideQueryManager implements QueryManager {

	private SlideWorkspace slideWorkspace;
	private Search searchHelper;
	
	public SlideQueryManager(SlideWorkspace slideWorkspace) {
		this.slideWorkspace=slideWorkspace;
		searchHelper = slideWorkspace.getSlideSession().getSlideRepository().getNamespace().getSearchHelper();
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
