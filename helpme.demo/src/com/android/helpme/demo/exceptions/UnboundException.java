/**
 * 
 */
package com.android.helpme.demo.exceptions;

import com.android.helpme.demo.manager.NetworkManager;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;

/**
 * @author Andreas Wieland
 *
 */
public class UnboundException extends Exception {

	/**
	 * 
	 */
	public UnboundException() {
		super(NetworkManager.LOGTAG +" is not bound to " +RabbitMQService.LOGTAG);
	}
}
