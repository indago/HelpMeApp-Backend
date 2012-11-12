/*
 * Copyright (C) 2011-2012 AlarmApp.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.helpme.demo.utils.position;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

import com.android.helpme.demo.interfaces.PositionInterface;
import com.android.helpme.demo.utils.User;
import com.google.android.maps.GeoPoint;

import android.R.dimen;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class Position implements Serializable, PositionInterface {
	public static final String PRECISION = "precision";
	public static final String DIRECTION = "direction";
	public static final String SPEED = "speed";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String DATE = "date";
	private double longitude;
	private double latitude;
	private double speed;
	private double direction;
	private double precision;
	private long date;

	// private String operationId;
	
	public Position(Location location){
		this.longitude =  location.getLongitude();
		this.latitude = location.getLatitude();
		this.speed = location.getSpeed();
		this.direction = location.getBearing();
		this.precision = location.getAccuracy();
		this.date = location.getTime();
	}

	public Position(double lon, double lat, float speed, float direction, float precision, long date) {
		// this.operationId = OperationId;
		this.longitude = lon;
		this.latitude = lat;
		this.speed = speed;
		this.direction = direction;
		this.precision = precision;
		this.date = date;
	}

	public Position(JSONObject object) {
		JSONObject position = (JSONObject) object.get(User.POSITION);
		if (position != null) {
			this.longitude = (Double)position.get(LONGITUDE);
			this.latitude = (Double)position.get(LATITUDE);
			this.speed = (Double)position.get(SPEED);
			this.direction = (Double)position.get(DIRECTION);
			this.precision = (Double)position.get(PRECISION);
			this.date = (Long)position.get(DATE);
		}else {
			this.longitude = (Double)object.get(LONGITUDE);
			this.latitude = (Double)object.get(LATITUDE);
			this.speed = (Double)object.get(SPEED);
			this.direction = (Double)object.get(DIRECTION);
			this.precision = (Double)object.get(PRECISION);
			this.date = (Long)object.get(DATE);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getSpeed()
	 */
	@Override
	public double getSpeed() {
		return speed;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getDirection()
	 */
	@Override
	public double getDirection() {
		return direction;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getPrecision()
	 */
	@Override
	public double getPrecision() {
		return precision;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getMeasureDateTime()
	 */
	@Override
	public long getMeasureDateTime() {
		return date;
	}
	
	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getLongitude()
	 */
	@Override
	public double getLongitude() {
		return longitude;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getLatitude()
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}
	
	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#calculateSphereDistance(com.android.helpme.demo.utils.position.PositionInterface)
	 */
	@Override
	public double calculateSphereDistance(PositionInterface other) {

		double earthRadius = 3958.75;
		double dLat = Math.toRadians(this.latitude - other.getLatitude());
		double dLng = Math.toRadians(this.longitude - other.getLongitude());
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(this.latitude))
				* Math.cos(Math.toRadians(other.getLatitude()))
				* Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return  (dist * meterConversion);
	}
	
	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.position.PositionInterface#getJSON()
	 */
	@Override
	public JSONObject getJSON(){
		JSONObject object = new JSONObject();
		object.put(LONGITUDE, this.longitude);
		object.put(LATITUDE, this.latitude);
		object.put(SPEED, this.speed);
		object.put(DIRECTION, this.direction);
		object.put(PRECISION, this.precision);
		object.put(DATE, this.date);
		return object;
	}
	
	@Override
	public String toString() {
		String string = new String();
		string += LONGITUDE +" : " + this.longitude + "\n";
		string += LATITUDE +" : "+ this.latitude + "\n";
		string += SPEED +" : "+ this.speed +"\n";
		string += DIRECTION +" : "+ this.direction +"\n";
		string += PRECISION +" : "+ this.precision +"\n";
		string += DATE +" : "+DateFormat.getDateInstance(DateFormat.FULL).format(new Date(date)) +"\n";
		
		return string;
	}
	
	@Override
	public GeoPoint getGeoPoint() {
		int latitude = (int)(getLatitude() * 1e6);
		int longitude = (int)(getLongitude() * 1e6);
		GeoPoint point = new GeoPoint(latitude,longitude);
		return point;
	}
}
