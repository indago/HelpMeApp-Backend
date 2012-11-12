package com.android.helpme.demo.exceptions;
/**
 * 
 * @author Andreas Wieland
 *
 */
public class UnkownMessageType extends Exception {
	 public UnkownMessageType(String text) {
		super(text);
	}
	public UnkownMessageType() {
		super("recieved Message of unknwon type");
	}
	public UnkownMessageType(Object where) {
		super("recieved Message of unknown type from: " +where.getClass().getSimpleName());
	}
	public UnkownMessageType(Class<?> from){
		super("recieved Message of unknown type from: " +from.getSimpleName());
	}
	 
}
