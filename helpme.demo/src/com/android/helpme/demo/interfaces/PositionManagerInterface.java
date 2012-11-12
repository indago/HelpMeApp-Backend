package com.android.helpme.demo.interfaces;

import com.android.helpme.demo.utils.position.Position;

import android.location.LocationListener;

public interface PositionManagerInterface extends LocationListener {

	public  Runnable startLocationTracking();

	public  Runnable stopLocationTracking();

	public  boolean isStarted();
	
	public Position getLastPosition();
}