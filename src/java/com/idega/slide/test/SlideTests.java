package com.idega.slide.test;

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

@Service("slideTestsExecutor")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SlideTests extends DefaultSpringBean implements ApplicationListener {
	
	private static final Logger LOGGER = Logger.getLogger(SlideTests.class.getName());

	private Random random = new Random();
	
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof IWMainSlideStartedEvent) {
			IWMainSlideStartedEvent slideStartedEvent = (IWMainSlideStartedEvent) event;
			if (slideStartedEvent.getIWMA().getSettings().getBoolean("test_mass_uploads", Boolean.FALSE)) {
				executeConcurentUploads(50);
			}
		}
	}
	
	public boolean executeConcurentUploads(int threads) {
		final IWSlideService slide = getServiceInstance(IWSlideService.class);
		
		String mainDir = "/files/public/concurent_uploads/";
		String subDir1 = mainDir.concat("t1/");
		final List<String> uploadPaths = Arrays.asList(mainDir, subDir1, mainDir.concat("t2/"), subDir1.concat("sub/"));		
		final String fileName = "File_";
		for (int i = 0; i < threads; i++) {
			final int threadNumber = i+1;
			Thread worker = new Thread(new Runnable() {
				public void run() {
					String path = getRandomPath(uploadPaths);
					String name = fileName.concat(String.valueOf(threadNumber)).concat(".txt");
					try {
						InputStream stream = StringHandler.getStreamFromString("File " + threadNumber + " with no content in folder: " + path);
						boolean result = slide.uploadFile(path, name, "text/plain", stream);
						LOGGER.info("Uploaded file: " + path + name + ": " + result);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error while uploading file: " + path + name, e);
					}
				}
			});
			worker.start();
			LOGGER.info("Thread " + threadNumber + " has started: " + worker);
		}
		
		return Boolean.TRUE;
	}

	private String getRandomPath(List<String> paths) {
		return paths.get(random.nextInt(paths.size()));
	}
}