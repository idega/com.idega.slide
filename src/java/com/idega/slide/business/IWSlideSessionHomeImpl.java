package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class IWSlideSessionHomeImpl extends IBOHomeImpl implements IWSlideSessionHome {

	public Class getBeanInterfaceClass() {
		return IWSlideSession.class;
	}

	public IWSlideSession create() throws CreateException {
		return (IWSlideSession) super.createIBO();
	}
}