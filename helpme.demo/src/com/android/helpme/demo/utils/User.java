package com.android.helpme.demo.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.helpme.demo.interfaces.PositionInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.SimpleSelectionStrategy;
import com.google.android.maps.GeoPoint;



/**
 * 
 * @author Andreas Wieland
 *
 */
public class User implements UserInterface {
	public static final String NAME = "Name";
	public static final String HELFER = "Helfer";
	public static final String POSITION = "Position";
	public static final String PICTURE = "Picture";
	public static final String ID = "id";
	public static final String MESSAGE = "Message";
	public static final String GENDER = "Gender";
	public static final String AGE = "Age";

	private String name;
	private String id;
	private Boolean helfer;
	private Position position;
	private String pic; //TODO
	private Integer age;
	private String gender;

	public User(String id,String name, Boolean helfer,String pic,Integer age, String gender) {
		this.name = name;
		this.helfer = helfer;
		this.id = id;
		this.pic = pic;
		this.age = age;
		this.gender = gender;
	}

	public User(JSONObject object) {
		this.name = (String) object.get(NAME);
		this.helfer = (Boolean) object.get(HELFER);
		this.id = (String) object.get(ID);
		this.pic = (String) object.get(PICTURE);
		this.age = new Integer(object.get(AGE).toString());
		this.gender  = ((String)object.get(GENDER));
		if (object.get(POSITION) != null) {
			this.position = new Position(object);
		}
	}


	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserInterface#isHelper()
	 */
	@Override
	public Boolean isHelper() {
		return helfer;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getPosition()
	 */
	@Override
	public Position getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#setPosition(com.android.helpme.demo.utils.position.Position)
	 */
	@Override
	public void setPosition(Position position) {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getAge()
	 */
	@Override
	public int getAge() {
		return age;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getHandyNr()
	 */
	public String getGender() {
		return gender;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String string = new String();
		if (helfer) {
			string += "Helfer";
		}else {
			string += "Hilfe Suchender";
		}
		string += (" : "+ name);
		return string;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getJsonObject()
	 */
	@Override
	public JSONObject getJsonObject() {
		JSONObject object = new JSONObject();
		object.put(NAME, name);
		object.put(HELFER, helfer);
		object.put(POSITION, position.getJSON());
		object.put(ID, id);
		object.put(PICTURE, pic);
		object.put(AGE, age);
		object.put(GENDER, gender);
		return object;
	}
	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getGeoPoint()
	 */
	@Override
	public GeoPoint getGeoPoint() {
		if (getPosition() != null) {
			return position.getGeoPoint();
		}else
			return null;

	}
	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#updatePosition(com.android.helpme.demo.utils.position.Position)
	 */
	@Override
	public void updatePosition(Position position) {
		if (this.position == null) {
			this.position = position;
		}else if (SimpleSelectionStrategy.isPositionRelevant(getPosition(), position)) {
			this.position = position;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getPicture()
	 */
	@Override
	public String getPicture() {
		return this.pic;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getDistanceTo(com.android.helpme.demo.utils.UserInterface)
	 */
	@Override
	public double getDistanceTo(UserInterface userInterface) {
		if (this.position != null) {
			return this.position.calculateSphereDistance(userInterface.getPosition());
		}else
			return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserInterface#setPicture(int)
	 */
	@Override
	public void setPicture(String pic) {
		this.pic = pic;
	}
	
	@Override
	public UserInterface getUser(JSONObject object) {
		return new User(object);
	}
}
