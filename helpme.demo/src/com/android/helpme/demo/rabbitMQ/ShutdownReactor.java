/**
 * 
 */
package com.android.helpme.demo.rabbitMQ;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author Andreas Wieland
 *
 */
public class ShutdownReactor implements ShutdownListener {
	private ConcurrentHashMap<String,Channel> subscribedChannels;
	/**
	 * 
	 */
	public ShutdownReactor(ConcurrentHashMap<String, Channel> map) {
		this.subscribedChannels = map;
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.ShutdownListener#shutdownCompleted(com.rabbitmq.client.ShutdownSignalException)
	 */
	@Override
	public void shutdownCompleted(ShutdownSignalException arg0) {
		String string = arg0.getMessage();

		/*
		 * if by chance the Message was not set than we have to search each channel for the closed one
		 */
		if (string == null) {
			Set<String> keys = subscribedChannels.keySet();
			for (String key : keys) {
				if (!subscribedChannels.get(key).isOpen()) {
					string = key;
					break;
				}
			}

		}
		subscribedChannels.remove(string);
	}
}
