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
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainSlideStartedEvent;
import com.idega.slide.SlideConstants;
import com.idega.slide.business.IWSlideService;
import com.idega.util.CoreConstants;

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
		final IWBundle bundle = getBundle(SlideConstants.BUNDLE_IDENTIFIER);
		
		String mainDir = "/files/public/concurent_uploads/";
		String subDir1 = mainDir.concat("t1/");
		final List<String> uploadPaths = Arrays.asList(mainDir, subDir1, mainDir.concat("t2/"), subDir1.concat("sub/"), "/files/cms/", "/files/dropbox/",
				"/files/bpm/", "/files/users/");	
		final List<String> testFiles = Arrays.asList("/resources/images/test.jpg", "/resources/test.pdf");				
		final String fileName = "File_";
		for (int i = 0; i < threads; i++) {
			final int threadNumber = i+1;
			Thread worker = new Thread(new Runnable() {
				public void run() {
					long start = System.currentTimeMillis();
					
					String path = getRandomValue(uploadPaths);
					String file = getRandomValue(testFiles);
					String name = fileName.concat(String.valueOf(threadNumber)).concat(CoreConstants.UNDER)
						.concat(file.substring(file.lastIndexOf(CoreConstants.SLASH) + 1));
					try {
						InputStream stream = bundle.getResourceInputStream(file);
						boolean result = slide.uploadFile(path, name, null, stream);
						LOGGER.info("Uploaded file: " + path + name + ": " + result);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error while uploading file: " + path + name, e);
					} finally {
						long end = System.currentTimeMillis();
						LOGGER.info("Took time to upload ".concat(path).concat(name).concat(": ").concat(String.valueOf(end-start)).concat(" ms"));
					}
				}
			});
			worker.start();
			LOGGER.info("Thread " + threadNumber + " has started: " + worker);
		}
		
		return Boolean.TRUE;
	}

	private String getRandomValue(List<String> values) {
		return values.get(random.nextInt(values.size()));
	}
}