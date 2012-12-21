/**
 * 
 */
package com.android.helpme.demo.overlay;

import org.jdom2.Element;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class HistoryOverlayItem extends OverlayItem {
	private Element element;

	/**
	 * @param geoPoint
	 * @param title
	 * @param text
	 */
	public HistoryOverlayItem(GeoPoint geoPoint, String title, String text) {
		super(geoPoint, title,text);
		element = null;
	}
	
	public HistoryOverlayItem(GeoPoint geoPoint, String title, String text, Element element) {
		super(geoPoint, title, text);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
	
	

}
