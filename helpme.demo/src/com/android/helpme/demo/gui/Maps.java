/**
 * 
 */
package com.android.helpme.demo.gui;

import java.util.List;

import com.android.helpme.demo.R;
import com.android.helpme.demo.R.drawable;
import com.android.helpme.demo.R.id;
import com.android.helpme.demo.R.layout;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @author Andreas Wieland
 *
 */
public class Maps extends MapActivity implements DrawManager{
	private List<Overlay> mapOverlays;
	private MyItemnizedOverlay overlay;
	private MapController mapController;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		handler = new Handler();
		
		mapView.setBuiltInZoomControls(true);

		mapOverlays= mapView.getOverlays();
		
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);

		overlay = new MyItemnizedOverlay(drawable, this);

		mapController = mapView.getController();
		mapOverlays.add(overlay);

		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.MAP, this);
		handler.post(addMarker(UserManager.getInstance().getThisUser()));
	}

	private Runnable addMarker(final UserInterface userInterface){
		return new Runnable() {
			@Override
			public void run() {
				mapController.animateTo(userInterface.getGeoPoint());
				while (mapController.zoomIn()) {
				}
				mapController.zoomOut();
				mapController.setCenter(userInterface.getGeoPoint());
				OverlayItem overlayitem;
				if (userInterface.getHelfer()) {
					 overlayitem = new OverlayItem(userInterface.getGeoPoint(), userInterface.getName(),"ein Helfer");
				}else{
					overlayitem = new OverlayItem(userInterface.getGeoPoint(), userInterface.getName(),"Sie");
				}
				
				overlay.addOverlay(overlayitem);
			}
		};
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void drawThis(Object object) {
		if (object instanceof User) {
			User user = (User) object;
			handler.post(addMarker(user));
		}

	}

}