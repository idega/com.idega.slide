package com.idega.slide.business;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.util.CoreConstants;

public abstract class RepositoryWorker implements Runnable {

	private IWSlideServiceBean repositoryService;
	private IWSimpleSlideServiceImp simpleRepositoryService;
	
	private String workId = UUID.randomUUID().toString();
	private String repositoryPath;
	
	protected Boolean result;
	
	public RepositoryWorker(IWSlideServiceBean repositoryService, String repositoryPath) {
		this.repositoryService = repositoryService;
		
		if (this instanceof DeleteWorker) {
			int lastSlash = repositoryPath.lastIndexOf(CoreConstants.SLASH);
			if (lastSlash != 0) {
				String fileName = repositoryPath.substring(lastSlash + 1);
				if (fileName.indexOf(CoreConstants.DOT) != -1) {
					repositoryPath = repositoryPath.substring(0, repositoryPath.indexOf(fileName));
				}
			}
		}
		this.repositoryPath = repositoryPath;
	}
	
	public RepositoryWorker(IWSlideServiceBean repositoryService, IWSimpleSlideServiceImp simpleRepositoryService, String repositoryPath) {
		this(repositoryService, repositoryPath);
		
		this.simpleRepositoryService = simpleRepositoryService;
	}
	
	public void run() {
		waitInAQueue();
		doWork();
	}
	
	private void waitInAQueue() {
		while (getRepositoryService().isBusyWorker(getRepositoryPath(), getWorkId())) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				getLogger().log(Level.WARNING, "Worker was interupted while waiting in an unpload queue!", e);
			}
		}
	}

	protected abstract void doWork();
	
	boolean isWorkFinishedSuccessfully() {
		try {
			if (result == null) {
				getLogger().warning("There is still no results about status for: ".concat(getWorkId()).concat(", path: ").concat(getRepositoryPath()));
				result = Boolean.FALSE;
			}
			
			return result;
		} finally {
			getRepositoryService().removeFromQueue(getRepositoryPath(), getWorkId());
		}
	}
	
	protected IWSlideServiceBean getRepositoryService() {
		return repositoryService;
	}
	
	protected IWSimpleSlideServiceImp getSimpleRepositoryService() {
		return simpleRepositoryService;
	}
	
	protected String getRepositoryPath() {
		return repositoryPath;
	}
	
	String getWorkId() {
		return workId;
	}
	
	protected Logger getLogger() {
		return Logger.getLogger(this.getClass().getName());
	}
}