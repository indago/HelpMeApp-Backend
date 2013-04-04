/**
 * 
 */
package com.android.helpme.demo.eventmanagement.events;

import java.util.EventObject;

import com.android.helpme.demo.utils.position.Position;

/**
 * @author Andreas Wieland
 *
 */
public class PositionEvent extends EventObject {	
	private Position position;

	/**
	 * @param source
	 * @param position
	 */
	public PositionEvent(Object source, Position position){
		super(source);
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

}
