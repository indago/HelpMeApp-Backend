/**
 * 
 */
package com.android.helpme.demo.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.android.helpme.demo.eventmanagement.eventListeners.PositionEventListener;
import com.android.helpme.demo.eventmanagement.events.PositionEvent;
import com.android.helpme.demo.interfaces.PositionInterface;
import com.android.helpme.demo.interfaces.PositionManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.SimpleSelectionStrategy;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author Andreas Wieland
 * 
 */
public class PositionManager implements PositionManagerInterface {
	private static final String LOGTAG = PositionManager.class.getSimpleName();
	//	private InAppMessage message;
	private static PositionManager manager;
	private LocationManager locationManager;
	private Set<PositionEventListener> listeners; 
	private Location lastLocation;
	private boolean started;
	private Handler handler;
	private String generatedId;

	/**
	 * 
	 * @param context
	 */
	private PositionManager(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		lastLocation = null;
		started = false;
		handler = new Handler();
		listeners = new HashSet<PositionEventListener>();
	}

	/**
	 * 
	 * @return {@link PositionManager} if set else null
	 */
	public static PositionManagerInterface getInstance() {
		return manager;
	}
	
	/**
	 * creates new {@link PositionManager}
	 * @param context
	 * @return {@link PositionManager}
	 */
	public static PositionManager getInstance(Context context) {
		if (manager == null) {
			manager = new PositionManager(context);
		}
		return manager;
	}

	public String getLogTag() {
		return LOGTAG;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!SimpleSelectionStrategy.isPositionRelevant(location)) {
			return;
		}
		//		if (lastLocation != null && !SimpleSelectionStrategy.isPositionRelevant(lastLocation, location)) {
		//			return;
		//		}
		lastLocation = location;
		Position position = new Position(location);
		Log.i(getLogTag(), "new Location arrived");
		notifyListeners(new PositionEvent(manager, position));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#startLocationTracking()
	 */
	@Override
	public void startLocationTracking() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (started) {
					return;
				}
				//				if (Looper.myLooper() == null) {
				//					Looper.prepare();
				//				}
				//				;

				lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (lastLocation != null && SimpleSelectionStrategy.isPositionRelevant(lastLocation)) {
					notifyListeners(new PositionEvent(manager, new Position(lastLocation)));
				}
			}
		});


		handler.post(new Runnable() {

			@Override
			public void run() {
				Log.i(LOGTAG, "requesting Location");
				if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					//							Criteria crit = new Criteria();
					//							crit.setPowerRequirement(Criteria.POWER_LOW);
					//							crit.setAccuracy(Criteria.ACCURACY_COARSE);
					//							String provider = locationManager.getBestProvider(crit, false);
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, manager);
					started = true;
				}
				if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					//							Criteria crit2 = new Criteria();
					//							crit2.setAccuracy(Criteria.ACCURACY_FINE);
					//							provider2 = locationManager.getBestProvider(crit2, false);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, manager);
					started = true;
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#stopLocationTracking()
	 */
	@Override
	public void stopLocationTracking() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				locationManager.removeUpdates(manager);
				started = false;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#isStarted()
	 */
	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public Position getLastPosition() {
		return new Position(lastLocation);
	}

	public void addPositionEventListener(PositionEventListener positionEventListener){
		listeners.add(positionEventListener);
	}

	public void removePositionEventListener(PositionEventListener positionEventListener){
		listeners.remove(positionEventListener);
	}

	private void notifyListeners(PositionEvent positionEvent){
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
			PositionEventListener eventListener = (PositionEventListener) iterator.next();
			eventListener.getPositionEvent(positionEvent);
		}
	}
}
