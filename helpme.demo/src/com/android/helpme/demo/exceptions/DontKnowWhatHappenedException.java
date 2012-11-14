/**
 * 
 */
package com.android.helpme.demo.exceptions;

/**
 * @author Andreas Wieland
 *
 */
public class DontKnowWhatHappenedException extends Exception {

	/**
	 * 
	 */
	public DontKnowWhatHappenedException() {
		super("Dont know what happend but something went terrible wrong and probably here should stand something else");
	}
	
	public DontKnowWhatHappenedException(String e){
		super("Dont know what happend but something went terrible wrong and probably here should stand something else\nmaybe this helps?\n" +e);
	}
}
