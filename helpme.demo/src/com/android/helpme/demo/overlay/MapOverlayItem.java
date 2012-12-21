/**
 * 
 */
package com.android.helpme.demo.overlay;


import org.jdom2.Element;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class MapOverlayItem extends OverlayItem {

	private Element element;
	private Drawable[] drawable;
	/**
	 * @param point
	 * @param title
	 * @param snippet
	 */
	public MapOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		element = null;
		drawable = null;
	}

	public MapOverlayItem(GeoPoint point, String title, String snippet, Element element, Drawable[] drawable) {
		super(point, title, snippet);
		this.element = element;
		this.drawable = drawable;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public Drawable[] getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable[] drawable) {
		this.drawable = drawable;
	}
}
