/**
 * 
 */
package com.android.helpme.demo.interfaces.ManagerInterfaces;

import com.android.helpme.demo.eventmanagement.eventListeners.DataEventListener;
import com.android.helpme.demo.eventmanagement.events.DataEvent;
import com.android.helpme.demo.utils.User;

/**
 * @author Andreas Wieland
 *
 */
public interface RecievedDataManagerInterface extends DataEventListener,ManagerInterface {
	public void handleIncomingUserAsHelper(User incomingUser);
	public void handleIncomingUserAsHelpee(User incomingUser);
}
