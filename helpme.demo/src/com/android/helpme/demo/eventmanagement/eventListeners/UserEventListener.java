/**
 * 
 */
package com.android.helpme.demo.eventmanagement.eventListeners;

import java.util.EventListener;

import com.android.helpme.demo.eventmanagement.events.UserEvent;

/**
 * @author Andreas Wieland
 *
 */
public interface UserEventListener extends EventListener {
	void getUserEvent(UserEvent userEvent);
}
