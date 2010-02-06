package com.idega.slide.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UploadInfo {

	private List<String> uploadsQueue = new ArrayList<String>();
	private ReentrantLock lock;
	
	public void addToQueue(String uploadId) {
		synchronized (uploadsQueue) {
			if (!uploadsQueue.contains(uploadId)) {
				uploadsQueue.add(uploadId);
			}
		}
	}
	
	public void removeFromQueue(String uploadId) {
		synchronized (uploadsQueue) {
			uploadsQueue.remove(uploadId);
		}
		
		unlock();
	}
	
	public boolean isQueueEmpty() {
		synchronized (uploadsQueue) {
			if (uploadsQueue.size() == 0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	private void unlock() {
		if (lock != null) {
			synchronized (lock) {
				try {
					lock.unlock();
				} catch (IllegalMonitorStateException e) {}
			}
		}
	}
	
	public void lock() {
		if (lock == null) {
			lock = new ReentrantLock();
		}
	
		synchronized (lock) {
			if (!lock.isLocked()) {
				lock.lock();
			}
		}
	}
	
	public boolean isFirstInAQueue(String uploadId) {
		synchronized (uploadsQueue) {
			if (uploadsQueue.size() == 0) {
				return Boolean.FALSE;
			}
			
			if (uploadId.equals(uploadsQueue.get(0)) && !isActive()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public synchronized boolean isActive() {
		if (lock == null) {
			return Boolean.FALSE;
		}
		
		if (lock.isLocked()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	
	public boolean isLockedByCurrentThread() {
		if (lock == null) {
			return false;
		}
		
		synchronized (lock) {
			return lock.isHeldByCurrentThread();
		}
	}
	
	@Override
	public String toString() {
		return "Uploads queue: " + uploadsQueue + ", lock: " + lock;
	}
}