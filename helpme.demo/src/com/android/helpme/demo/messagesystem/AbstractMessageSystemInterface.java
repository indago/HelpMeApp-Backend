package com.android.helpme.demo.messagesystem;

public interface AbstractMessageSystemInterface {
	/**
	 * sets the LogTag for each class its for debuging on android
	 * @return
	 */
	public abstract String getLogTag();

	public abstract AbstractMessageSystemInterface getManager();

}