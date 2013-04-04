/**
 * 
 */
package com.android.helpme.demo.eventmanagement.events;

import java.util.EventObject;

import com.android.helpme.demo.messagesystem.InAppMessage;

/**
 * @author Andreas Wieland
 *
 */
public class DataEvent extends EventObject {
	InAppMessage inAppMessage;

	/**
	 * @param source
	 */
	public DataEvent(Object source, InAppMessage inAppMessage) {
		super(source);
		this.inAppMessage = inAppMessage;
	}

	public InAppMessage getInAppMessage() {
		return inAppMessage;
	}

	public void setInAppMessage(InAppMessage inAppMessage) {
		this.inAppMessage = inAppMessage;
	}

}
