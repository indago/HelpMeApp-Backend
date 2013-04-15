package com.android.helpme.demo.exceptions;

import com.android.helpme.demo.manager.NetworkManager;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;

/**
 * 
 * @author azak
 */
public class NotSetException extends Exception {

	public NotSetException() {
	}

	public NotSetException(String detailMessage) {
		super(detailMessage);
	}

	public NotSetException(Throwable throwable) {
		super(throwable);
	}

	public NotSetException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
