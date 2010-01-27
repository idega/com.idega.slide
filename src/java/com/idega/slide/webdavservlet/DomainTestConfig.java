/**
 * $Id: DomainTestConfig.java,v 1.1 2009/01/06 15:17:20 tryggvil Exp $
 * Created in 2009 by tryggvil
 *
 * Copyright (C) 2000-2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.webdavservlet;


/**
 * <p>
 * Overrided DomainConfig to use in maven unit test
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class DomainTestConfig extends DomainConfig {
	
	//public static final String SPRING_BEAN_IDENTIFIER="slideTestDomainConfig";
	
	public DomainTestConfig(){
		//Overrided to use the ./target folder as the repository base
		setBasePath("./target");
		setJndiLookupEnabled(false);
		setIwAppPropertyLookupEnabled(false);
	}

}
