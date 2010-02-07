package com.idega.slide.upload;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

	private Random random;
	
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof IWMainSlideStartedEvent) {
			IWMainSlideStartedEvent slideStartedEvent = (IWMainSlideStartedEvent) event;
			if (slideStartedEvent.getIWMA().getSettings().getBoolean("test_mass_uploads", Boolean.FALSE)) {
				LOGGER = LOGGER == null ? Logger.getLogger(UploadTest.class.getName()) : LOGGER;
				random = random == null ? new Random() : random;
				
				executeUploads();
			}
		}
	}
	
	public void executeUploads() {
		final IWSlideService slide = getServiceInstance(IWSlideService.class);
		
		String mainDir = "/files/public/concurent_uploads/";
		String subDir1 = mainDir.concat("t1/");
		final List<String> uploadPaths = Arrays.asList(mainDir, subDir1, mainDir.concat("t2/"), subDir1.concat("sub/"));		
		final String fileName = "File_";
		for (int i = 0; i < 50; i++) {
			final int fileNumber = i+1;
			Thread worker = new Thread(new Runnable() {
				public void run() {
					String path = getRandomPath(uploadPaths);
					String name = fileName.concat(String.valueOf(fileNumber)).concat(".txt");
					try {
						InputStream stream = StringHandler.getStreamFromString("File " + fileNumber + " with no content in folder: " + path);
						boolean result = slide.uploadFile(path, name, "text/plain", stream);
						LOGGER.info("Uploaded file: " + path + name + ": " + result);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error while uploading file: " + path + name, e);
					}
				}
			});
			worker.start();
		}
	}

	private String getRandomPath(List<String> paths) {
		return paths.get(random.nextInt(paths.size()));
	}
}