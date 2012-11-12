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
	public  Runnable connect();
	
	/**
	 * Disconnects from RabbitMQ 
	 * @return
	 */
	public Runnable disconnect();

	/**
	 * Sends {@link String} on the exchange {@link Channel} with the given Name
	 * @param string
	 * @param exchangeName
	 * @return
	 */
	public  Runnable sendStringOnChannel(String string, String exchangeName);
	/**
	 * Sends {@link String} on all subscribed Exchange {@link Channel}s 
	 * @return
	 */
	public Runnable subscribeToChannel(String exchangeName,String type);
	
	/**
	 * Ends subscribtion to exchange {@link Channel} with given name and sends the name as {@link ShutdownSignalException} reason
	 * @param exchangeName
	 * @return
	 */
	public Runnable endSubscribtionToChannel(String exchangeName);
	
	/**
	 * Show a notification while this service is running.
	 */
	public void showNotification(String text, String title);
}