package com.android.helpme.demo.interfaces;

public interface DrawManagerInterface {
	public enum DRAWMANAGER_TYPE {
		SEEKER, LIST, LOGIN, MAP, HELPERCOMMING, SWITCHER;
	}
	public void drawThis(Object object);

}
