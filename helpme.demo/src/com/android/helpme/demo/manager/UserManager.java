package com.android.helpme.demo.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.util.Log;

import com.android.helpme.demo.eventmanagement.eventListeners.PositionEventListener;
import com.android.helpme.demo.eventmanagement.eventListeners.UserEventListener;
import com.android.helpme.demo.eventmanagement.events.PositionEvent;
import com.android.helpme.demo.eventmanagement.events.UserEvent;
import com.android.helpme.demo.interfaces.PositionManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.UserManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public class UserManager implements UserManagerInterface, PositionEventListener{
	private static final String LOGTAG = UserManager.class.getSimpleName();
	private static final String USER_PROPERTIES = "user.properties";
	private static final String CHOOSEN_USER_PREF = "choosen_user_preference";
	private static final long TIMEOUT = 60000;
	private static UserManager manager;
	private Set<UserEventListener> userEventListeners;
	
	private Context context;
	private ConcurrentHashMap<String, User> users;
	private UserInterface thisUser;
	private boolean userSet;
	private Timer timer;

	/**
	 * 
	 * @return {@link UserManager}
	 */
	public static UserManager getInstance() {
		if(manager == null) {
			manager = new UserManager();
		}
		return manager;
	}

	/**
	 * creates new User Manager
	 */
	private UserManager() {
		users = new ConcurrentHashMap<String, User>();
		userSet = false;
		timer = new Timer();
		userEventListeners = new HashSet<UserEventListener>();
	}
	
	@Override
	public boolean init() {
		if (PositionManager.getInstance() != null) {
			PositionManager.getInstance().addPositionEventListener(this);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserManagerInterface#isUserSet()
	 */
	@Override
	public boolean isUserSet() {
		return userSet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserManagerInterface#getThisUser()
	 */
	@Override
	public UserInterface getThisUser() {
		return thisUser;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserManagerInterface#thisUser()
	 */
	@Override
	public UserInterface thisUser() {
		return thisUser;
	}

	private void setThisUser(UserInterface user, String id) {
		if(!userSet) {
			this.thisUser = new User(id, user.getName(), user.isHelper(), user.getPicture(), user.getAge(), user.getGender());
			userSet = true;
		}
	}

	@Override
	public void setThisUser(final UserInterface userInterface, final Context context) {
		//		return new Runnable() {

		//			@Override
		//			public void run() {
		String uuid = UUID.randomUUID().toString();
		setThisUser(userInterface, uuid);
		saveUserChoice(context);
		clear();
		//			}
		//		};
	}

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

//	@Override
//	protected InAppMessage getMessage() {
//		return message;
//	}
//
//	@Override
//	protected void setMessage(InAppMessage inAppMessage) {
//		this.message = inAppMessage;
//	}
//
//	@Override
//	public AbstractMessageSystemInterface getManager() {
//		return manager;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.UserManagerInterface#addUser(com.android
	 * .helpme.demo.utils.User)
	 */
	@Override
	public boolean addUser(UserInterface user) {
		//		if(users.isEmpty()) {
		//			timer.scheduleAtFixedRate(createTimerTask(), TIMEOUT, TIMEOUT);
		//		}
		if(users.containsKey(user.getId())) {
			users.get(user.getId()).updatePosition(user.getPosition());
			return false;
		} else {
			users.putIfAbsent(user.getId(), (User) user);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.interfaces.UserManagerInterface#removeUser(com.android.helpme.demo.utils.User)
	 */
	@Override
	public boolean removeUser(UserInterface user) {
		if (users.containsKey(user.getId())) {
			users.remove(user.getId());
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.UserManagerInterface#getUsers()
	 */
	@Override
	public ArrayList<User> getUsers() {
		ArrayList<User> list = new ArrayList<User>();
		Set<String> keys = users.keySet();
		for(String key : keys) {
			list.add(users.get(key));
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.UserManagerInterface#getUser(java.lang
	 * .String)
	 */
	@Override
	public UserInterface getUserByName(String name) {
		Set<String> keys = users.keySet();
		for(String key : keys) {
			User user = users.get(key);
			if(user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.UserManagerInterface#readUserFromProperty
	 * (android.app.Activity)
	 */
	@Override
	public ArrayList<User> readUsersFromProperty(final Context context) {
		//		return new Runnable() {
		//
		//			@Override
		//			public void run() {
		Resources resources = context.getResources();
		AssetManager assetManager = resources.getAssets();

		// Read from the /assets directory
		try {
			InputStream inputStream = assetManager.open(USER_PROPERTIES);
			Reader reader = new InputStreamReader(inputStream);
			JSONParser parser = new JSONParser();
			Properties properties = new Properties();
			properties.load(reader);
			Set<Object> set = properties.keySet();
			ArrayList<User> list = new ArrayList<User>();
			for(Object key : set) {
				String string = (String) properties.get(key);
				JSONObject object = (JSONObject) parser.parse(string);
				//						object = setPicture(object, context);
				list.add(new User(
						(String) object.get(User.ID),
						(String) object.get(User.NAME),
						(Boolean) ( object.get(User.HELFER)),
						(String) object.get(User.PICTURE),
						new Integer ((String) object.get(User.AGE)),
						(String) object.get(User.GENDER)));
			}
			Log.i(LOGTAG, "The properties are now loaded");
			//			fireMessageFromManager(list, InAppMessageType.RECEIVED_DATA);
			return list;
		} catch(IOException e) {
			Log.e(LOGTAG, e.toString());
			//			fireError(e);
		} catch(ParseException e) {
			Log.e(LOGTAG, e.toString());
			//			fireError(e);
		}
		return null;
		//			}
		//		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#
	 * readUserChoice(android.content.Context)
	 */
	@Override
	public UserInterface readUserChoice(final Context context) {
		//		return new Runnable() {

		//			@Override
		//			public void run() {
		SharedPreferences settings = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
		userSet = readUserFromSharedPreference(settings);
		//				fireMessageFromManager(thisUser, InAppMessageType.LOADED);
		return thisUser;
		//			}
		//		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#
	 * saveUserChoice(android.content.Context)
	 */
	@Override
	public boolean saveUserChoice(final Context context) {
		//		return new Runnable() {

		//			@Override
		//			public void run() {
		SharedPreferences settings = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
		return writeUserToSharedPreference(settings); 
		//			}
		//		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#
	 * deleteUserChoice(android.content.Context)
	 */
	@Override
	public boolean deleteUserChoice(final Context context) {
		//		return new Runnable() {

		//			@Override
		//			public void run() {
		SharedPreferences preferences = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
		thisUser = null;
		userSet = false;
		return deleteUserFromSharedPreference(preferences);

		//			}
		//		};
	}

	/**
	 * saves the user as
	 * 
	 * @param preferences
	 * @param user
	 * @return
	 */
	private boolean writeUserToSharedPreference(SharedPreferences preferences) {
		Editor editor = preferences.edit();
		editor.putBoolean(User.HELFER, thisUser.isHelper());
		editor.putInt(User.AGE, thisUser.getAge());
		editor.putString(User.ID, thisUser.getId());
		editor.putString(User.GENDER, thisUser.getGender());
		editor.putString(User.NAME, thisUser.getName());
		editor.putString(User.PICTURE, thisUser.getPicture());

		return editor.commit();
	}

	/**
	 * 
	 * @param preferences
	 * @return
	 */
	private boolean readUserFromSharedPreference(SharedPreferences preferences) {
		User user = new User(preferences.getString(User.ID, null),
				preferences.getString(User.NAME, null),
				preferences.getBoolean(User.HELFER, false),
				preferences.getString(User.PICTURE, null),
				preferences.getInt(User.AGE, Integer.MIN_VALUE),
				preferences.getString(User.GENDER, null));
		if(user.getId() == null) {
			thisUser = null;
			return false;
		}
		this.thisUser = user;
		return true;
	}

	/**
	 * 
	 * @param preferences
	 * @return
	 */
	private boolean deleteUserFromSharedPreference(SharedPreferences preferences) {
		Editor editor = preferences.edit();
		editor.clear();
		return editor.commit();
	}

	public void clear() {
		//		return new Runnable() {
		//
		//			@Override
		//			public void run() {
		synchronized(users) {
			users.clear();
		}
		//			}
		//		};

	}

	@Override
	public UserInterface getUserById(String id) {
		return users.get(id);
	}
	
	@Override
	public void addUserEventListener(UserEventListener userEventListener) {
		userEventListeners.add(userEventListener);
	}
	
	@Override
	public void removeUserEventListener(UserEventListener userEventListener) {
		userEventListeners.remove(userEventListener);
	}
	
	private void notifyListeners(UserEvent userEvent){
		for (UserEventListener eventListener : userEventListeners) {
			eventListener.getUserEvent(userEvent);
		}
	}

	@Override
	public void getPositionEvent(PositionEvent positionEvent) {
		Position position = positionEvent.getPosition();
		thisUser.updatePosition(position);
	}

//	private TimerTask createTimerTask() {
//		return new TimerTask() {
//
//			@Override
//			public void run() {
//				boolean changed = false;
//				Enumeration<String> keys = users.keys();
//				while(keys.hasMoreElements()) {
//					String key = (String) keys.nextElement();
//					User user = users.get(key);
//					long timeOfLastMessage = user.getPosition().getMeasureDateTime();
//					long currentTime = System.currentTimeMillis();
//					if(currentTime - timeOfLastMessage >= TIMEOUT) {
//
//						users.remove(key);
//
//						changed = true;
//					}
//				}
//
//				if(changed) {
//					fireMessageFromManager(getUsers(), InAppMessageType.CHANGED);
//				}
//			}
//		};
//	}
}
