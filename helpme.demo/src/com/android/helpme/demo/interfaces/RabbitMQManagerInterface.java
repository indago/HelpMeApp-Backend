package com.android.helpme.demo.interfaces;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;

public interface RabbitMQManagerInterface {
	
	public static enum ExchangeType{fanout, driect};

	/**
	 * Connects to RabbitMQ and generates new Main Channel
	 * @return
	 */
	public  Runnable connect();
	
	/**
	 * Disconnects to RabbitMQ
	 * @return
	 */
	public Runnable disconnect();
	/**
	 * binds 
	 * @param context
	 * @return
	 */
	public Runnable bindToService(Activity activity);

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
	public  Runnable subscribeToMainChannel();
	/**
	 * Subscribes to exchange {@link Channel} with given name with {@link ExchangeType} "fanout"
	 * @param exchangeName
	 * @return
	 */
	public Runnable subscribeToChannel(String exchangeName);
	/**
	 * Subscribes to exchange {@link Channel} with given namen and given {@link ExchangeType}
	 * @param exchangeName
	 * @param type
	 * @return
	 */
	public Runnable subscribeToChannel(String exchangeName,ExchangeType type);
	
	/**
	 * Ends subscribtion to exchange {@link Channel} with given name and sends the name as {@link ShutdownSignalException} reason
	 * @param exchangeName
	 * @return
	 */
	public Runnable endSubscribtionToChannel(String exchangeName);
	
	/**
	 * Shows a Android Notification with the specified Text and Title 
	 * @param text
	 * @param title
	 * @return
	 */
	public Runnable showNotification(String text, String title);
	
	public Runnable showNotification(UserInterface userInterface);
	
	public Runnable unbindFromService(Context context);
}