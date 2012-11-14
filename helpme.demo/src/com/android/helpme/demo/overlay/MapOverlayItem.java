/**
 * 
 */
package com.android.helpme.demo.overlay;

import org.json.simple.JSONObject;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class MapOverlayItem extends OverlayItem {

	private JSONObject jsonObject;
	private Drawable[] drawable;
	/**
	 * @param point
	 * @param title
	 * @param snippet
	 */
	public MapOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		jsonObject = null;
		drawable = null;
	}

	public MapOverlayItem(GeoPoint point, String title, String snippet, JSONObject jsonObject, Drawable[] drawable) {
		super(point, title, snippet);
		this.jsonObject = jsonObject;
		this.drawable = drawable;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public Drawable[] getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable[] drawable) {
		this.drawable = drawable;
	}
}
