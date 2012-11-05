package com.android.helpme.demo.interfaces;

import org.json.simple.JSONObject;

import android.location.Location;

public interface PositionInterface {

	public double getSpeed();

	public double getDirection();

	public double getPrecision();

	public long getMeasureDateTime();

	public double getLongitude();

	public double getLatitude();

	public double calculateSphereDistance(PositionInterface other);

	public JSONObject getJSON();
}