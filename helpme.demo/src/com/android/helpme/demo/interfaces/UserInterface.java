package com.android.helpme.demo.interfaces;

import org.jdom2.Element;

import android.content.SharedPreferences;

import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
import com.google.android.maps.GeoPoint;

public interface UserInterface {
	
	public UserInterface getUser(Element object);
	
	public String getId();

	public String getName();

	public Boolean isHelper();

	public Position getPosition();
	
	public String getPicture();
	
	public void setPicture(String pic);
	
	public int getAge();
	
	public String getGender();

	public void setPosition(Position position);

	public Element getElement();
	
	public GeoPoint getGeoPoint();
	
	public void updatePosition(Position position);
	
	public double getDistanceTo(UserInterface userInterface);
}