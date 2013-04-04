/**
 * 
 */
package com.android.helpme.demo.eventmanagement.events;

import java.util.EventObject;

import com.android.helpme.demo.utils.User;

/**
 * @author Andreas Wieland
 *
 */
public class UserEvent extends EventObject {

	User user;
	/**
	 * @param source
	 */
	public UserEvent(Object source, User user) {
		super(source);
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
