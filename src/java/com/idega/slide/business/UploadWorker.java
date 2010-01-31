package com.idega.slide.business;

import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadWorker implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(UploadWorker.class.getName());
	
	private IWSlideServiceBean slideService;
	
	private String uploadId = UUID.randomUUID().toString();
	
	private String uploadPath;
	private String fileName;
	private String contentType;
	
	private InputStream stream;
	
	private boolean closeStream;
	
	private boolean result;
	
	public UploadWorker(IWSlideServiceBean slideService, String uploadPath, String fileName, String contentType, InputStream stream, boolean closeStream) {
		this.slideService = slideService;
		
		this.uploadPath = uploadPath;
		this.fileName = fileName;
		this.contentType = contentType;
		this.stream = stream;
		this.closeStream = closeStream;
	}
	
	public void run() {
		waitInAQueue();
		upload();
	}
	
	boolean getUploadResult() {
		return result;
	}
	
	private void waitInAQueue() {
		while (slideService.isBusyUploader(uploadPath, uploadId)) {
			try {
				LOGGER.info("Worker " + this + " is waiting in a queue...");
				Thread.sleep(50);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Upload worker was interupted while waiting in an unpload queue!", e);
			}
		}
	}

	private void upload() {
		try {
			uploadPath = slideService.createFoldersAndPreparedUploadPath(uploadPath, true);
			if (uploadPath == null) {
				return;
			}
	
			IWSimpleSlideService simpleSlideService = slideService.getSimpleSlideService();
			if (simpleSlideService == null) {
				return;
			}
			
			result = simpleSlideService.upload(stream, uploadPath, fileName, contentType, null, closeStream);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Error uploading '" + uploadPath + fileName + "' using Slide API. Will try to upload using common API", t);
		} finally {
			slideService.removeFromUploadFromQueue(uploadPath, uploadId);
		}
	}
	
	@Override
	public String toString() {
		return "Upload worker: id: ".concat(uploadId).concat(", upload path: ").concat(uploadPath).concat(", file name: ").concat(fileName);
	}
}