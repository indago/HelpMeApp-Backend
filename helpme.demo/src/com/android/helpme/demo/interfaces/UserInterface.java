package com.android.helpme.demo.interfaces;

import org.jdom2.Element;

import android.content.SharedPreferences;

import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
import com.google.android.maps.GeoPoint;

public interface UserInterface {

	public static final String NAME = "Name";
	public static final String HELFER = "Helfer";
	public static final String POSITION = "Position";
	public static final String PICTURE = "Picture";
	public static final String ID = "id";
	public static final String MESSAGE = "Message";
	public static final String GENDER = "Gender";
	public static final String AGE = "Age";
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
	public Element getElement(String name);
	
	public GeoPoint getGeoPoint();
	
	public void updatePosition(Position position);
	
	public double getDistanceTo(UserInterface userInterface);
}