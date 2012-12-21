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

import org.jdom2.DocType;
import org.jdom2.Element;

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
	private Double longitude;
	private Double latitude;
	private Double speed;
	private Double direction;
	private Double precision;
	private Long date;

	// private String operationId;

	public Position(Location location){
		this.longitude =  location.getLongitude();
		this.latitude = location.getLatitude();
		this.speed = (double) location.getSpeed();
		this.direction = (double) location.getBearing();
		this.precision = (double) location.getAccuracy();
		this.date = location.getTime();
	}

	public Position(double lon, double lat, float speed, float direction, float precision, long date) {
		// this.operationId = OperationId;
		this.longitude = lon;
		this.latitude = lat;
		this.speed = (double) speed;
		this.direction = (double) direction;
		this.precision = (double) precision;
		this.date = date;
	}

	public Position(Element object) {
		Element position = object.getChild(User.POSITION);
		if (position != null) {
			this.longitude = new Double(position.getAttributeValue(LONGITUDE));
			this.latitude = new Double(position.getAttributeValue(LATITUDE));
			this.speed = new Double (position.getAttributeValue(SPEED));
			this.direction = new Double (position.getAttributeValue(DIRECTION));
			this.precision = new Double (position.getAttributeValue(PRECISION));
			this.date = new Long(position.getAttributeValue(DATE));
		}else {
			this.longitude = new Double( object.getAttributeValue(LONGITUDE));
			this.latitude = new Double( object.getAttributeValue(LATITUDE));
			this.speed = new Double( object.getAttributeValue(SPEED));
			this.direction = new Double( object.getAttributeValue(DIRECTION));
			this.precision = new Double( object.getAttributeValue(PRECISION));
			this.date = new Long (object.getAttributeValue(DATE));
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

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.PositionInterface#getElement()
	 */
	@Override
	public Element getElement(){
		return getElementAs(User.POSITION);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.PositionInterface#getElementAs(java.lang.String)
	 */
	@Override
	public Element getElementAs(String string) {
		Element object = new Element(string);

		object.setAttribute(LONGITUDE, this.longitude.toString());
		object.setAttribute(LATITUDE, this.latitude.toString());
		object.setAttribute(SPEED, this.speed.toString());
		object.setAttribute(DIRECTION, this.direction.toString());
		object.setAttribute(PRECISION, this.precision.toString());
		object.setAttribute(DATE, this.date.toString());
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
