package com.idega.slide.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.idega.slide.webdavservlet.WebdavExtendedServlet;

public class WorkerInfo {

	private List<String> queue = new ArrayList<String>();
	private ReentrantLock lock;
	
	public void addToQueue(String workId) {
		synchronized (queue) {
			if (!queue.contains(workId)) {
				queue.add(workId);
			}
		}
	}
	
	public void removeFromQueue(String workId) {
		synchronized (queue) {
			queue.remove(workId);
		}
		
		unlock();
	}
	
	public boolean isQueueEmpty() {
		synchronized (queue) {
			if (queue.size() == 0) {
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
	
	public boolean isFirstInAQueue(String workId) {
		synchronized (queue) {
			if (queue.size() == 0) {
				return Boolean.FALSE;
			}
			
			if (workId.equals(queue.get(0)) && !isActive()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public synchronized boolean isActive() {
		if (WebdavExtendedServlet.isLocked()) {
			return Boolean.TRUE;
		}
		
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
		return "Queue: " + queue + ", lock: " + lock;
	}
}