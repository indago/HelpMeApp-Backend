package com.android.helpme.demo.manager;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.android.helpme.demo.eventmanagement.eventListeners.DataEventListener;
import com.android.helpme.demo.eventmanagement.events.DataEvent;
import com.android.helpme.demo.exceptions.UnboundException;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;

public class RabbitMQManager  implements RabbitMQManagerInterface, Observer{
	public static final String LOGTAG = RabbitMQManager.class.getSimpleName();
	private static int TIMEOUT = 1000;
	private static RabbitMQManager manager;
	private Set<DataEventListener> dataEventListeners;
	private ArrayList<String> exchangeNames;
	private boolean bound;
	private RabbitMQService rabbitMQService;
	private SAXBuilder saxBuilder;
	private Document document;

	public static RabbitMQManager getInstance() {
		if (manager == null) {
			manager = new RabbitMQManager();
		}
		return manager;
	}
	/*
	 * (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		//TODO
		if (data instanceof Message) {
			Message msg = (Message) data;
			Bundle bundle = msg.getData();
			InAppMessageType type = InAppMessageType.valueOf(bundle.getString(RabbitMQService.MESSAGE));
			InAppMessage message = new InAppMessage(manager, null, type);
			if (type == InAppMessageType.RECEIVED_DATA) {
				try {
					Reader reader = new StringReader(bundle.getString(RabbitMQService.DATA_STRING));
					document = saxBuilder.build(reader);
					message.setObject(new User(document.getRootElement()));
				} catch (Exception e) {
					Log.e(LOGTAG, e.toString());
				}

			}
			notifyDataEventListener(new DataEvent(manager, message));
			//			fireMessage(message);
		}
	}
	
	@Override
	public boolean init(Context context) {
		boolean b = false;
		bindToService(context);
		b = connect();
		b = subscribeToMainChannel();
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#connect()
	 */
	@Override
	public boolean connect() {
		try {
			long time = System.currentTimeMillis();
			sendToService(createConnectBundle());

			while (time + TIMEOUT <= System.currentTimeMillis()) {
				if (rabbitMQService.isConnected()) {
					return true;
				}
			}
			//TODO Timeout exception?
		} catch (RemoteException e) {
			Log.e(LOGTAG, "connect: " +e.toString());
		}catch (UnboundException e) {
			Log.e(LOGTAG, "connect: " +e.toString());
		}
		return false;
	}

	@Override
	public boolean disconnect() {
		try {
			long time = System.currentTimeMillis();
			sendToService(createDisconnectBundle());
			while (time + TIMEOUT <= System.currentTimeMillis()) {
				if (!rabbitMQService.isConnected()) {
					return true;
				}
			}
			//TODO Timeout exception?
		} catch (RemoteException e) {
			Log.e(LOGTAG, "disconnect: "+e.toString());
		} catch (UnboundException e) {
			Log.e(LOGTAG, "disconnect: "+e.toString());
		}
		return false;
	}

	private RabbitMQManager() {
		exchangeNames = new ArrayList<String>();
		bound = false;
		saxBuilder  = new SAXBuilder();
		dataEventListeners = new HashSet<DataEventListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.RabbitMQManagerInterface#sendString(java
	 * .lang.String)
	 */
	@Override
	public Runnable sendStringOnMain(final String string) {
		return sendStringOnChannel(string, "main");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#getString()
	 */
	@Override
	public boolean subscribeToMainChannel() {
		return subscribeToChannel("main");
	}

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	@Override
	public boolean subscribeToChannel(String exchangeName) {
		return subscribeToChannel(exchangeName, ExchangeType.fanout);
	}

	@Override
	public boolean subscribeToChannel(final String exchangeName, final ExchangeType type) {
		try {
			long time = System.currentTimeMillis();
			sendToService(createSubscribeToBundle(exchangeName, type));
			while (time + TIMEOUT <= System.currentTimeMillis()) {
				if (rabbitMQService.getSubscribedChannelNames().contains(exchangeName)) {
					return true;
				}
			}
			//TODO Timeout exception?
		} catch (RemoteException e) {
			Log.e(LOGTAG, "subscribe to Channel: " +e.toString());
		} catch (UnboundException e) {
			Log.e(LOGTAG, "subscribe to Channel: " +e.toString());
		}
		return false;
	}

	@Override
	public Runnable sendStringOnChannel(final String string, final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createSendDataBundle(exchangeName, string));
				} catch (RemoteException e) {
					Log.e(LOGTAG, "Send String On Channel: " +e.toString());
				} catch (UnboundException e) {
					Log.e(LOGTAG, "Send String On Channel: " +e.toString());
				}
			}
		};
	}

	@Override
	public Runnable sendStringToSubscribedChannels(final String string) {
		return new Runnable() {

			@Override
			public void run() {
				for (String exchangeName : exchangeNames) {
					/**
					 * we dont send the message on the Main channel
					 */
					if (!exchangeName.equalsIgnoreCase("main")) {
						ThreadPool.runTask(sendStringOnChannel(string, exchangeName));
					}
				}

			}
		};
	}

	@Override
	public boolean endSubscribtionToChannel(final String exchangeName) {
				//TODO
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.RabbitMQManagerInterface#bindToService(android.content.Context)
	 */
	@Override
	public void bindToService(final Context context) {
		if (!bound) {
			Log.i(LOGTAG, "Binding of Service");
			// we create a new Messanger with our defined handler
			rabbitMQService = new RabbitMQService(context);
			rabbitMQService.addObserver(manager);
			bound = true;
		}
	}

	@Override
	public void unbindFromService(final Context context) {
		Log.i(LOGTAG, "unBinding of Service");
		bound = false;
		rabbitMQService.deleteObservers();
		if (rabbitMQService.isConnected()) {
			rabbitMQService.disconnect().run();
		}
		rabbitMQService = null;
	}
	
	@Override
	public void addDataEventListener(DataEventListener dataEventListener) {
		dataEventListeners.add(dataEventListener);
	}
	
	@Override
	public void removeDataEventListener(DataEventListener dataEventListener) {
		dataEventListeners.remove(dataEventListener);		
	}
	
	private void notifyDataEventListener(DataEvent dataEvent){
		for (DataEventListener listener : dataEventListeners) {
			listener.getDataEvent(dataEvent);
		}
	}

	private void sendToService(Bundle bundle) throws RemoteException, UnboundException {
		if (bound) {
			Message message = new Message();
			message.setData(bundle);
			rabbitMQService.handleIncomingMessage(message);
		} else {
			throw new UnboundException();
		}
	}

	private Bundle createShowNotificationBundle(String text, String title) {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.NOTIFICATION.name());
		bundle.putString(RabbitMQService.TEXT, text);
		bundle.putString(RabbitMQService.TITLE, title);
		return bundle;
	}

	private Bundle createSubscribeToBundle(String exchangeName, ExchangeType type) {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.SUBSCRIBE.name());
		bundle.putString(RabbitMQService.EXCHANGE_NAME, exchangeName);
		bundle.putString(RabbitMQService.EXCHANGE_TYPE, type.name());
		return bundle;
	}

	private Bundle createSendDataBundle(String exchangeName, String data) {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.SEND.name());
		bundle.putString(RabbitMQService.EXCHANGE_NAME, exchangeName);
		bundle.putString(RabbitMQService.DATA_STRING, data);
		return bundle;
	}

	private Bundle createConnectBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.CONNECTED.name());
		return bundle;
	}

	private Bundle createDisconnectBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.DISCONNECTED.name());
		return bundle;
	}

	private Bundle createEndSubscribtionBundle(String exchangeName) {
		Bundle bundle = new Bundle();
		bundle.putString(RabbitMQService.MESSAGE, InAppMessageType.SUBSCRIBTION_ENDED.name());
		bundle.putString(RabbitMQService.EXCHANGE_NAME, exchangeName);
		return bundle;
	}

}
