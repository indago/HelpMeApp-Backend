package com.android.helpme.demo.interfaces;

import android.location.LocationListener;

public interface PositionManagerInterface extends LocationListener {

	public  Runnable startLocationTracking();

	public  Runnable stopLocationTracking();

	public  boolean isStarted();
}