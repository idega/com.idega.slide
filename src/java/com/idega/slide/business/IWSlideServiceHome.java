package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface IWSlideServiceHome extends IBOHome {
	public IWSlideService create() throws CreateException, RemoteException;
}