/**
 * 
 */
package com.android.helpme.demo.exceptions;

import com.android.helpme.demo.manager.RabbitMQManager;
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
		super(RabbitMQManager.LOGTAG +" is not bound to " +RabbitMQService.LOGTAG);
	}
}
