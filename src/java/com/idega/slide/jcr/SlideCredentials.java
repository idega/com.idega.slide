/**
 * $Id: SlideCredentials.java,v 1.1 2009/01/06 15:17:20 tryggvil Exp $
 * Created in 2009 by tryggvil
 *
 * Copyright (C) 2000-2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.slide.jcr;

import javax.jcr.Credentials;

import org.apache.slide.common.SlideToken;

/**
 * <p>
 * TODO tryggvil Describe Type SlideCredentials
 * </p>
 *  Last modified: $Date: 2009/01/06 15:17:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class SlideCredentials implements Credentials {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4164141294202290185L;
	private SlideToken slideToken;
	
	public SlideCredentials(SlideToken token){
		this.setSlideToken(slideToken);
	}

	public void setSlideToken(SlideToken slideToken) {
		this.slideToken = slideToken;
	}

	public SlideToken getSlideToken() {
		return slideToken;
	}
	
}
