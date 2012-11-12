/**
 * 
 */
package com.android.helpme.demo.overlay;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class MapItemnizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> items;
	private Context context;

	public MapItemnizedOverlay(Drawable drawable) {
		super(boundCenterBottom(drawable));
		items = new ArrayList<OverlayItem>();
		populate();
	}

	public MapItemnizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		items = new ArrayList<OverlayItem>();
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}
	
	public ArrayList<OverlayItem> getItems() {
		return items;
	}
	
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		return false;
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = items.get(index);
		if (item != null) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
			return true;
		}
		return false;
	}

	public void removeItem(OverlayItem item) {
		items.remove(item);
	}
}
