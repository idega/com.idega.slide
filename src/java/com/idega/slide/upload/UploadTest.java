package com.idega.slide.upload;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWMainSlideStartedEvent;
import com.idega.slide.business.IWSlideService;
import com.idega.util.StringHandler;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UploadTest extends DefaultSpringBean implements ApplicationListener {

	private Logger LOGGER;
	
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof IWMainSlideStartedEvent) {
			IWMainSlideStartedEvent slideStartedEvent = (IWMainSlideStartedEvent) event;
			if (slideStartedEvent.getIWMA().getSettings().getBoolean("test_mass_uploads", Boolean.FALSE)) {
				LOGGER = LOGGER == null ? Logger.getLogger(UploadTest.class.getName()) : LOGGER;
				final IWSlideService slide = getServiceInstance(slideStartedEvent.getIWMA().getIWApplicationContext(), IWSlideService.class);
				
				final String uploadPath = "/files/public/mass_uploads/";
				try {
					if (slide.getExistence(uploadPath)) {
						slide.deleteAsRootUser(uploadPath);
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error while deleting: " + uploadPath, e);
				}
				
				final String fileName = "File_";
				for (int i = 0; i < 100; i++) {
					final int fileNumber = i+1;
					Thread worker = new Thread(new Runnable() {
						public void run() {
							try {
								boolean result = slide.uploadFile(uploadPath, fileName + fileNumber + ".txt", "text/plain",
										StringHandler.getStreamFromString("File " + fileNumber + " with no content"));
								LOGGER.info("Uploaded file: " + fileNumber + ": " + result);
							} catch (Exception e) {
								LOGGER.log(Level.WARNING, "Error while uploading file: " + fileNumber, e);
							}
						}
					});
					worker.start();
				}
			}
		}
	}
}