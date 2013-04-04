package com.android.helpme.demo.interfaces;

import com.android.helpme.demo.eventmanagement.eventListeners.PositionEventListener;
import com.android.helpme.demo.interfaces.ManagerInterfaces.ManagerInterface;
import com.android.helpme.demo.utils.position.Position;

import android.location.LocationListener;

public interface PositionManagerInterface extends LocationListener,ManagerInterface {

	public  void startLocationTracking();

	public  void stopLocationTracking();

	public  boolean isStarted();
	
	public Position getLastPosition();
	
	public void addPositionEventListener(PositionEventListener positionEventListener);
	public void removePositionEventListener(PositionEventListener positionEventListener);
}