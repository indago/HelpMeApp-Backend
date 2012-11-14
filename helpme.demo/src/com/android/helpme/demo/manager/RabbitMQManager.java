package com.android.helpme.demo.manager;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.android.helpme.demo.R;
import com.android.helpme.demo.exceptions.UnboundException;
import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.UserManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;
import com.android.helpme.demo.rabbitMQ.RabbitMQServiceMessages;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;

public class RabbitMQManager extends AbstractMessageSystem implements RabbitMQManagerInterface {
	public static final String LOGTAG = RabbitMQManager.class.getSimpleName();
	private static RabbitMQManager rabbitMQManager;
	private ArrayList<String> exchangeNames;
	private Messenger messenger;
	private InAppMessage message;
	private boolean bound;
	private JSONParser parser;
	private Handler handler;
	private ServiceConnection serviceConnection;

	public static RabbitMQManager getInstance() {
		if (rabbitMQManager == null) {
			rabbitMQManager = new RabbitMQManager();
		}
		return rabbitMQManager;
	}

	private Handler createNewHandler() {
		return new Handler() {
			public void handleMessage(android.os.Message msg) {
				Bundle bundle = msg.getData();
				InAppMessageType type = InAppMessageType.valueOf(bundle.getString(RabbitMQService.MESSAGE));
				InAppMessage message = new InAppMessage(getManager(), null, type);
				if (type == InAppMessageType.RECEIVED_DATA) {
					JSONObject object;
					try {
						object = (JSONObject) parser.parse(bundle.getString(RabbitMQService.DATA_STRING));
						message.setObject(new User(object));
					} catch (ParseException e) {
						fireError(e);
					}

				}
				fireMessage(message);
			};
		};
	}

	private ServiceConnection createNewServiceConnection() {
		return new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				messenger = null;
				bound = false;
				fireMessageFromManager(null, InAppMessageType.UNBOUND_FROM_SERVICE);

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				messenger = new Messenger(service);
				bound = true;
				fireMessageFromManager(messenger, InAppMessageType.BOUND_TO_SERVICE);
				run(connect());
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#connect()
	 */
	@Override
	public Runnable connect() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createConnectBundle());
				} catch (RemoteException e) {
					fireError(e);
				}
			}
		};
	}

	@Override
	public Runnable disconnect() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createDisconnectBundle());
				} catch (RemoteException e) {
					fireError(e);
				}

			}
		};
	}

	private RabbitMQManager() {
		exchangeNames = new ArrayList<String>();
		bound = false;
		parser = new JSONParser();
		// TODO Binding to service
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
	public Runnable subscribeToMainChannel() {
		return subscribeToChannel("main");
	}

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		message = inAppMessage;

	}

	@Override
	public AbstractMessageSystemInterface getManager() {
		return rabbitMQManager;
	}

	@Override
	public Runnable subscribeToChannel(String exchangeName) {
		return subscribeToChannel(exchangeName, ExchangeType.fanout);
	}

	@Override
	public Runnable subscribeToChannel(final String exchangeName, final ExchangeType type) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createSubscribeToBundle(exchangeName, type));
				} catch (RemoteException e) {
					fireError(e);
				}
			}
		};
	}

	@Override
	public Runnable sendStringOnChannel(final String string, final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createSendDataBundle(exchangeName, string));
				} catch (RemoteException e) {
					fireError(e);
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
	public Runnable endSubscribtionToChannel(final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createEndSubscribtionBundle(exchangeName));
				} catch (RemoteException e) {
					fireError(e);
				}
			}
		};
	}

	@Override
	public Runnable bindToService(final Activity activity) {
		return new Runnable() {

			@Override
			public void run() {
				Intent intent = null;
				Context context = activity.getApplicationContext();
				intent = new Intent(context, RabbitMQService.class);
				// we create a new Messanger with our defined handler
				handler = createNewHandler();
				serviceConnection = createNewServiceConnection();
				Messenger messenger = new Messenger(handler);
				intent.putExtra(RabbitMQService.MESSENGER, messenger);
				activity.bindService(intent, serviceConnection, Service.BIND_ADJUST_WITH_ACTIVITY);
			}
		};
	}
	
	@Override
	public Runnable unbindFromService(final Context context) {
		return new Runnable() {
			
			@Override
			public void run() {
				handler = null;
				context.unbindService(serviceConnection);
				bound = false;
				
			}
		};
	}

	@Override
	public Runnable showNotification(final String text, final String title) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					sendToService(createShowNotificationBundle(text, title));
				} catch (RemoteException e) {
					fireError(e);
				}
			}
		};
	}

	@Override
	public Runnable showNotification(UserInterface userInterface) {
		return showNotification("This Person needs your Help: " + userInterface.getName(), "New Help Request");
	}

	private void sendToService(Bundle bundle) throws RemoteException {
		if (bound) {
			Message message = new Message();
			message.setData(bundle);
			messenger.send(message);
		} else {
			fireError(new UnboundException());
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
