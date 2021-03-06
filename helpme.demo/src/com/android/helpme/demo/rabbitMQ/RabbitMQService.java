package com.android.helpme.demo.rabbitMQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.android.helpme.demo.interfaces.RabbitMQSerivceInterface;
import com.android.helpme.demo.manager.NetworkManager;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMQService extends Observable implements RabbitMQSerivceInterface {
	public static final String LOGTAG = RabbitMQService.class.getSimpleName();
	private static String URL = "ec2-54-247-61-12.eu-west-1.compute.amazonaws.com";
	private ConnectionFactory factory;
	private Connection connection;
	private ConcurrentHashMap<String, Channel> subscribedChannels;
	private volatile Boolean connected = false;

	public synchronized Boolean isConnected() {
		return connected;
	}

	private Vibrator vibrator;
	private RabbitMQService service;

	public static final String EXCHANGE_NAME = "exchange_name", MESSAGE = "message", DATA_STRING = "data_string", EXCHANGE_TYPE = "exchange_type", TEXT = "text", TITLE = "title";
	public static final String MESSENGER = "MESSENGER", ACTIVITY = "ACTIVITY";

	// Used to receive messages from the Activity
	//	private final Messenger inMessenger = new Messenger(new IncomingHandler());
	// Use to send message to the Activity
	//	private Messenger outMessenger;

	public void handleIncomingMessage(Message msg) {

		Bundle bundle = msg.getData();
		InAppMessageType type = InAppMessageType.valueOf(bundle.getString(MESSAGE));
		Log.i(LOGTAG, "Got message: " + type);
		switch(type) {
			case CONNECTED:
				runThread(connectRunnable());
				break;
			case SUBSCRIBE:
				runThread(subscribeToChannelRunnable(bundle.getString(EXCHANGE_NAME), bundle.getString(EXCHANGE_TYPE)));
				break;
			case SEND:
				runThread(sendStringOnChannelRunnable(bundle.getString(DATA_STRING), bundle.getString(EXCHANGE_NAME)));
				break;
			case SUBSCRIBTION_ENDED:
				runThread(endSubscribtionToChannelRunnable(bundle.getString(EXCHANGE_NAME)));
				break;
			case NOTIFICATION:
				showNotification(bundle.getString(TEXT), bundle.getString(TITLE));
				break;

			default:
				Log.e(LOGTAG, "recevied Message with wrong Type");
				break;
		}
	}

	public RabbitMQService(Context context) {
		subscribedChannels = new ConcurrentHashMap<String, Channel>();
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		addObserver(NetworkManager.getInstance());
		service = this;
	}

	@Override
	public void showNotification(String text, String title) {
		long[] pattern = { 0, 200, 200, 200, 200 };
		vibrator.vibrate(pattern, -1);

	}
	
	@Override
	public boolean connect() {
		if(connected) {
			return true;
		}
		try {
			factory = new ConnectionFactory();
			factory.setHost(URL);
			connection = factory.newConnection();
			connected = connection.isOpen();
			sendMessage(InAppMessageType.CONNECTED, null);

			Log.i(LOGTAG, "connected to rabbitMQ");

			connected = true;
			return true;
		} catch(IOException e) {
			Log.e(LOGTAG, "connect: " +e.toString());
		}
		return false;
	}
	
	@Override
	public synchronized Runnable connectRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				connect();
			}
		};
	}

	@Override
	public boolean disconnect() {
		if(!connected) {
			return true;
		}
		Set<String> exchangeNames = subscribedChannels.keySet();
		for(String exchangeName : exchangeNames) {
			runThread(endSubscribtionToChannelRunnable(exchangeName));
		}
		subscribedChannels.clear();
		connected = false;
		try {
			connection.close();
			Log.i(LOGTAG, "disconnected");
			return true;
		} catch(IOException e) {
			Log.e(LOGTAG, "disconnect: " +e.toString());
		}
		return false;
	}
	
	@Override
	public synchronized Runnable disconnectRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				disconnect();
			}
		};
	}
	
	@Override
	public boolean sendStringOnChannel(String string, String exchangeName) {
		try {
			Channel channel = subscribedChannels.get(exchangeName);
			if(channel != null) {
				channel.basicPublish(exchangeName, "", null, string.getBytes());
			}
			Log.i(LOGTAG, "send to: " + exchangeName);
			return true;
		} catch(AlreadyClosedException exception) {
			Log.e(LOGTAG, exchangeName + " : " + exception.toString());
			subscribedChannels.remove(exchangeName);
		} catch(IOException e) {
			Log.e(LOGTAG, "sendStringOnChannel: " +e.toString());
		}
		return false;
	}

	@Override
	public synchronized Runnable sendStringOnChannelRunnable(final String string, final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				sendStringOnChannel(string, exchangeName);
			}
		};
	}
	
	@Override
	public boolean subscribeToChannel(String exchangeName, String type) {
		if(!connected) {
			return false;
		}
		try {
			// we create a new channel 
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, type);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchangeName, "");

			// we define what happens if we recieve a new Message
			channel.basicConsume(queueName, new RabbitMQConsumer(channel, service));
			subscribedChannels.putIfAbsent(exchangeName, channel);
			Log.i(LOGTAG, "subscribed to " + subscribedChannels.size() + " Channels" + "\n" + "started subscribtion to : " + exchangeName);
			return true;
		} catch(Exception e) {
			Log.e(LOGTAG, "Connection Problem at subscribeToChannel(): " + e.toString());
		}
		return false;
	}

	@Override
	public synchronized Runnable subscribeToChannelRunnable(final String exchangeName, final String type) {
		return new Runnable() {

			@Override
			public void run() {
				subscribeToChannel(exchangeName, type);
			}
		};
	}
	
	@Override
	public boolean endSubscribtionToChannel(String exchangeName) {
		Channel channel = subscribedChannels.get(exchangeName);
		if(channel != null) {
			try {
				String queueName = channel.queueDeclare().getQueue();
				channel.queueUnbind(queueName, exchangeName, "");
//				channel.basicConsume(queueName, arg1)

//				channel.close(0,exchangeName);
				subscribedChannels.remove(exchangeName);
				channel = null;
				Log.i(LOGTAG, "subscribed to " + subscribedChannels.size() + " Channels" + "\n" + "ended subscribtion to : " + exchangeName);
				return true;
			} catch(AlreadyClosedException e) {
				Log.e(LOGTAG, "endSubcribtionToChannel: " +e.toString());
				subscribedChannels.remove(exchangeName);
			} catch(IOException e) {
				Log.e(LOGTAG, "endSubcribtionToChannel: " +e.toString());
			} catch(ShutdownSignalException e) {
				Log.e(LOGTAG, "endSubcribtionToChannel: " +e.toString());

			}
		}
		return false;
	}

	@Override
	public synchronized Runnable endSubscribtionToChannelRunnable(final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				
			}
		};
	}

	protected void sendMessage(InAppMessageType type, String string) {
		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putString(MESSAGE, type.name());
		if(type == InAppMessageType.RECEIVED_DATA) {
			bundle.putString(DATA_STRING, string);
		}
		message.setData(bundle);
//		RabbitMQManager.getInstance().update(service, message);
		setChanged();
		notifyObservers(message);
	}

	private void runThread(Runnable runnable) {
		new Thread(runnable).start();
	}
	
	public ArrayList<String> getSubscribedChannelNames(){
		return new ArrayList<String>(subscribedChannels.keySet());
	}
}
