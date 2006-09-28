package com.idega.slide.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface IWSlideSessionHome extends IBOHome {
	public IWSlideSession create() throws CreateException, RemoteException;
}