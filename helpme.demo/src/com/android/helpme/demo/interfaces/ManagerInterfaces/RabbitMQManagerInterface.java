package com.android.helpme.demo.interfaces.ManagerInterfaces;


import android.content.Context;

import com.android.helpme.demo.eventmanagement.eventListeners.DataEventListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;

public interface RabbitMQManagerInterface extends ManagerInterface {
	
	public static enum ExchangeType{fanout, driect};

	/**
	 * Connects to RabbitMQ and generates new Main Channel
	 * @return
	 */
	public  boolean connect();
	
	/**
	 * Disconnects to RabbitMQ
	 * @return
	 */
	public boolean disconnect();
	/**
	 * binds 
	 * @param context
	 * @return
	 */
	public void bindToService(Context context);

	/**
	 * Sends {@link String} on main channel with the name "main"
	 * @param string
	 * @return
	 */
	public  Runnable sendStringOnMain(String string);
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
	public Runnable sendStringToSubscribedChannels(String string);

	/**
	 * Subscribes to main exchange {@link Channel}
	 * @return
	 */
	public  boolean subscribeToMainChannel();
	/**
	 * Subscribes to exchange {@link Channel} with given name with {@link ExchangeType} "fanout"
	 * @param exchangeName
	 * @return
	 */
	public boolean subscribeToChannel(String exchangeName);
	/**
	 * Subscribes to exchange {@link Channel} with given namen and given {@link ExchangeType}
	 * @param exchangeName
	 * @param type
	 * @return
	 */
	public boolean subscribeToChannel(String exchangeName,ExchangeType type);
	
	/**
	 * Ends subscription to exchange {@link Channel} with given name and sends the name as {@link ShutdownSignalException} reason
	 * @param exchangeName
	 * @return
	 */
	public boolean endSubscribtionToChannel(String exchangeName);
	
	/**
	 * Shows a Android Notification with the specified Text and Title 
	 * @param text
	 * @param title
	 * @return
	 */
	public void unbindFromService(Context context);
	/**
	 * binds to service and subscribes to main {@link Channel}
	 * @param context
	 * @return
	 */
	public boolean init(Context context);
	
	public void addDataEventListener(DataEventListener dataEventListener);
	public void removeDataEventListener(DataEventListener dataEventListener);
}