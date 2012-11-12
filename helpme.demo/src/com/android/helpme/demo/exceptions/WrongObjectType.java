package com.android.helpme.demo.exceptions;
/**
 * 
 * @author Andreas Wieland
 *
 */
public class WrongObjectType extends Exception {

	public WrongObjectType() {
		super("Encountered wrong ObjectType");
	}

	public WrongObjectType(String detailMessage) {
		super(detailMessage);
	}
	public WrongObjectType(String encounteredType, String expectedType){
		super("Wrong ObjectType encountered:" + encounteredType+" expected " +expectedType);
	}
	public WrongObjectType(Object encounteredObject, Class<?> expectedObject){
		super("Wrong ObjectType encountered:" + encounteredObject.getClass().getSimpleName()+" expected " +expectedObject.getSimpleName());
	}
	public WrongObjectType(String where, Object encounteredObject, Class<?> expectedObject){
		super("Wrong ObjectType encountered in "+where +" : " +encounteredObject.getClass().getSimpleName() +" expected " +expectedObject.getSimpleName());
	}
	public WrongObjectType(Object where, Object encounteredObject, Class<?> expectedObject){
		super("Wrong ObjectType encountered in "+where.getClass().getSimpleName() +" : " +encounteredObject.getClass().getSimpleName() +" expected " +expectedObject.getSimpleName());
	}
}
