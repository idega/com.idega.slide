package com.idega.slide.business;

import java.io.InputStream;

import com.idega.user.data.User;

/**
* Proxy for Simple Slide API's methods. Check {@link IWSimpleSlideServiceImp} for implementation.
* 
* @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
* @version $Revision: 1.1 $
*
* Last modified: $Date: 2009/05/08 08:08:46 $ by: $Author: valdas $
*/
public interface IWSimpleSlideService {

	public boolean checkExistance(String pathToFile) throws Exception;
	
	public InputStream getInputStream(String pathToFile);
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user) throws Exception;
	
	public boolean upload(InputStream stream, String uploadPath, String fileName, String contentType, User user, boolean closeStream) throws Exception;
	
}
