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
		
		if (lock != null) {
			synchronized (lock) {
				lock.unlock();
			}
		}
	}
	
	public boolean isQueueEmpty() {
		synchronized (uploadsQueue) {
			return uploadsQueue.size() == 0;
		}
	}
	
	public boolean isFirsInAQueue(String uploadId) {
		synchronized (uploadsQueue) {
			if (uploadsQueue.size() == 0) {
				return false;
			}
			
			if (uploadId.equals(uploadsQueue.get(0))) {
				if (lock == null) {
					lock = new ReentrantLock();
				}
			
				synchronized (lock) {
					if (!lock.isLocked()) {
						lock.lock();
						return true;
					}
				}
			}
		}
		return false;
	}
}