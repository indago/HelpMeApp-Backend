/**
 * 
 */
package com.android.helpme.demo.overlay;

import org.json.simple.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class HistoryOverlayItem extends OverlayItem {
	private JSONObject jsonObject;

	/**
	 * @param geoPoint
	 * @param title
	 * @param text
	 */
	public HistoryOverlayItem(GeoPoint geoPoint, String title, String text) {
		super(geoPoint, title,text);
		jsonObject = null;
	}
	
	public HistoryOverlayItem(GeoPoint geoPoint, String title, String text, JSONObject jsonObject) {
		super(geoPoint, title, text);
		this.jsonObject = jsonObject;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	
	

}
