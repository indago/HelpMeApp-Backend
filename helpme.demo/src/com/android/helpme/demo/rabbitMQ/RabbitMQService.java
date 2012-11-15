package com.android.helpme.demo.rabbitMQ;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.android.helpme.demo.R;
import com.android.helpme.demo.interfaces.RabbitMQSerivceInterface;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

public class RabbitMQService extends Observable implements RabbitMQSerivceInterface {
	public static final String LOGTAG = RabbitMQService.class.getSimpleName();
	private static String URL = "ec2-54-247-61-12.eu-west-1.compute.amazonaws.com";
	private ConnectionFactory factory;
	private Connection connection;
	private ConcurrentHashMap<String,Channel> subscribedChannels;
	private Boolean connected = false;
	public Boolean isConnected() {
		return connected;
	}


	private Vibrator vibrator;
	private RabbitMQService service;

	public static final String EXCHANGE_NAME = "exchange_name",MESSAGE = "message",DATA_STRING = "data_string", EXCHANGE_TYPE = "exchange_type",TEXT = "text",TITLE = "title";
	public static final String MESSENGER = "MESSENGER", ACTIVITY = "ACTIVITY";

	// Used to receive messages from the Activity
	//	private final Messenger inMessenger = new Messenger(new IncomingHandler());
	// Use to send message to the Activity
	//	private Messenger outMessenger;


	public void handleIncomingMessage(Message msg) {

		Bundle bundle = msg.getData();
		InAppMessageType type = InAppMessageType.valueOf(bundle.getString(MESSAGE));
		Log.i(LOGTAG, "Got message: "+ type);
		switch (type) {
		case CONNECTED:
			runThread(connect());
			break;
		case SUBSCRIBE:
			runThread(subscribeToChannel(bundle.getString(EXCHANGE_NAME), bundle.getString(EXCHANGE_TYPE)));
			break;
		case SEND:
			runThread(sendStringOnChannel(bundle.getString(DATA_STRING), bundle.getString(EXCHANGE_NAME)));
			break;
		case SUBSCRIBTION_ENDED:
			runThread(endSubscribtionToChannel(bundle.getString(EXCHANGE_NAME)));
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
		addObserver(RabbitMQManager.getInstance());
		service = this;
	}

	@Override
	public void showNotification(String text, String title) {
		long[] pattern = {0,200,200,200,200};
		vibrator.vibrate(pattern, -1);

	}

	@Override
	public Runnable connect() {
		return new Runnable() {

			@Override
			public void run() {
				if (connected) {
					return;
				}
				try {
					factory  = new ConnectionFactory();
					factory.setHost(URL);
					connection = factory.newConnection();
					connected = connection.isOpen();
					sendMessage(InAppMessageType.CONNECTED, null);

					Log.i(LOGTAG, "connected to rabbitMQ");
				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				} 

			}
		};
	}

	@Override
	public Runnable disconnect() {
		return new Runnable() {

			@Override
			public void run() {
				if (!connected) {
					return;
				}
				Set<String> exchangeNames = subscribedChannels.keySet();
				for (String exchangeName : exchangeNames) {
					runThread(endSubscribtionToChannel(exchangeName));
				}
				subscribedChannels.clear();
				connected = false;
				try {
					connection.close();
				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				}
				Log.i(LOGTAG, "disconnected");
			}
		};
	}

	@Override
	public Runnable sendStringOnChannel(final String string,final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				try {

					Channel channel = subscribedChannels.get(exchangeName);
					if (channel != null) {
						channel.basicPublish(exchangeName, "", null, string.getBytes());
					}
					Log.i(LOGTAG, "send to: "+exchangeName);
				} catch(AlreadyClosedException exception){
					Log.e(LOGTAG, exchangeName +" : " +exception.toString());
					subscribedChannels.remove(exchangeName);
				}
				catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				}

			}
		};
	}

	@Override
	public Runnable subscribeToChannel(final String exchangeName,final String type) {
		return new Runnable() {

			@Override
			public void run() {
				if (!connected) {
					return;
				}
				try {
					// we create a new channel 
					Channel channel = connection.createChannel();
					channel.exchangeDeclare(exchangeName, type);
					String queueName = channel.queueDeclare().getQueue();
					channel.queueBind(queueName,exchangeName , "");

					// we define what happens if we recieve a new Message
					channel.basicConsume(queueName,new RabbitMQConsumer(channel, service));
					subscribedChannels.putIfAbsent(exchangeName, channel);
					Log.i(LOGTAG, "subscribed to " + subscribedChannels.size() +" Channels" +"\n"+ "started subscribtion to : " + exchangeName);

				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				}
			}
		};
	}

	@Override
	public Runnable endSubscribtionToChannel(final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				Channel channel = subscribedChannels.get(exchangeName);
				if (channel != null ) {
					try {
						channel.close(0,exchangeName);
						subscribedChannels.remove(exchangeName);
						Log.i(LOGTAG, "subscribed to " + subscribedChannels.size() +" Channels" + "\n"+ "ended subscribtion to : " +exchangeName);
					}catch(AlreadyClosedException e){
						Log.e(LOGTAG, e.toString());
						subscribedChannels.remove(exchangeName);
					} catch (IOException e) {
						Log.e(LOGTAG, e.toString());
					} 
				}
			}
		};
	}

	protected void sendMessage(InAppMessageType type, String string){
		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putString(MESSAGE, type.name());
		if (type == InAppMessageType.RECEIVED_DATA) {
			bundle.putString(DATA_STRING, string);
		}
		message.setData(bundle);
		RabbitMQManager.getInstance().update(service, message);
		}


	private void runThread(Runnable runnable){
		new Thread(runnable).start();
	}
}
