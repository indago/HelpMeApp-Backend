package com.android.helpme.demo.interfaces;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;

public interface RabbitMQSerivceInterface {
	
	public enum SERVICE_STATICS{EXCHANGE_NAME,MESSAGE,DATA_STRING};
	
	/**
	 * Connects to RabbitMQ and generates new Main Channel
	 * @return
	 */
	public  Runnable connectRunnable();
	public boolean connect();
	
	/**
	 * Disconnects from RabbitMQ 
	 * @return
	 */
	public Runnable disconnectRunnable();
	public boolean disconnect();

	/**
	 * Sends {@link String} on the exchange {@link Channel} with the given Name
	 * @param string
	 * @param exchangeName
	 * @return
	 */
	public  Runnable sendStringOnChannelRunnable(String string, String exchangeName);
	public boolean sendStringOnChannel(String string, String exchangeName);
	
	/**
	 * Sends {@link String} on all subscribed Exchange {@link Channel}s 
	 * @return
	 */
	public Runnable subscribeToChannelRunnable(String exchangeName,String type);
	public boolean subscribeToChannel(String exchangeName,String type);
	
	/**
	 * Ends subscribtion to exchange {@link Channel} with given name and sends the name as {@link ShutdownSignalException} reason
	 * @param exchangeName
	 * @return
	 */
	public Runnable endSubscribtionToChannelRunnable(String exchangeName);
	public boolean endSubscribtionToChannel(String exchangeName);
	
	/**
	 * Show a notification while this service is running.
	 */
	public void showNotification(String text, String title);
}