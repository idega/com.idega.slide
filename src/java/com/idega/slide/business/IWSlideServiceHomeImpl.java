package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class IWSlideServiceHomeImpl extends IBOHomeImpl implements
		IWSlideServiceHome {
	public Class getBeanInterfaceClass() {
		return IWSlideService.class;
	}

	public IWSlideService create() throws CreateException {
		return (IWSlideService) super.createIBO();
	}
}