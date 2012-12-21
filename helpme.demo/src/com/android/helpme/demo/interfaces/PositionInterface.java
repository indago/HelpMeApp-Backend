package com.android.helpme.demo.interfaces;

import org.jdom2.Element;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public interface PositionInterface {

	public double getSpeed();

	public double getDirection();

	public double getPrecision();

	public long getMeasureDateTime();

	public double getLongitude();

	public double getLatitude();
	
	public GeoPoint getGeoPoint();

	public double calculateSphereDistance(PositionInterface other);

	public Element getElement();
	
	public Element getElementAs(String string);
}