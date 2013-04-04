/**
 * 
 */
package com.android.helpme.demo.eventmanagement.eventListeners;

import java.util.EventListener;

import com.android.helpme.demo.eventmanagement.events.PositionEvent;

/**
 * @author Andreas Wieland
 *
 */
public interface PositionEventListener extends EventListener {
	void getPositionEvent(PositionEvent positionEvent);
}
