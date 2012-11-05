package com.android.helpme.demo.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.util.Log;

import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.UserManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public class UserManager extends AbstractMessageSystem implements UserManagerInterface {
	private static final String LOGTAG = UserManager.class.getSimpleName();
	private static final String USER_PROPERTIES = "user.properties";
	private static final String CHOOSEN_USER_PREF = "choosen_user_preference";
	private static UserManager manager;
	private Context context;
	private InAppMessage message;
	private HashMap<String,User> users;
	private UserInterface thisUser;
	private boolean userSet;

	public static UserManager getInstance() {
		if (manager == null) {
			manager = new UserManager();
		}
		return manager;
	}

	private UserManager() {
		users = new HashMap<String, User>();
		userSet = false;
	}

	public boolean isUserSet(){
		return userSet;
	}

	@Override
	public UserInterface getThisUser() {
		return thisUser;
	}

	@Override
	public UserInterface thisUser() {
		return thisUser;
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#setThisUser(com.android.helpme.demo.utils.UserInterface, java.lang.String)
	 */
	@Override
	public void setThisUser(UserInterface user, String id) {
		if (!userSet) {
			this.thisUser = new User(id, user.getName(), user.getHelfer(), user.getPicture(), user.getAge(), user.getHandyNr());
			userSet =true;
		}
	}

	@Override
	public Runnable setThisUser(final UserInterface userInterface,final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				String android_id = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); 
				setThisUser(userInterface, android_id);
				ThreadPool.runTask(clear());
			}
		};
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
		this.message = inAppMessage;
	}

	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#addUser(com.android.helpme.demo.utils.User)
	 */
	@Override
	public boolean addUser(User user) {
		if (users.containsKey(user.getId())) {
			users.get(user.getId()).updatePosition(user.getPosition());
			return false;
		}else{
			users.put(user.getId(), user);
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#getUsers()
	 */
	@Override
	public ArrayList<User> getUsers() {
		ArrayList<User> list = new ArrayList<User>();
		Set<String> keys = users.keySet();
		for (String key : keys) {
			list.add(users.get(key));
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#getUser(java.lang.String)
	 */
	@Override
	public UserInterface getUserByName(String name) {
		Set<String> keys = users.keySet();
		for (String key : keys) {
			User user = users.get(key);
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#readUserFromProperty(android.app.Activity)
	 */
	@Override
	public Runnable readUserFromProperty(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
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
					for (Object key : set) {
						String string = (String) properties.get(key);
						JSONObject object = (JSONObject) parser.parse(string);
						list.add(new User(object));
					}
					Log.i(LOGTAG, "The properties are now loaded");
					fireMessageFromManager(list, InAppMessageType.RECEIVED_DATA);
				} catch (IOException e) {
					fireError(e);
				} catch (ParseException e) {
					fireError(e);
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#readUserChoice(android.content.Context)
	 */
	@Override
	public Runnable readUserChoice(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				SharedPreferences settings = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
				userSet = readUserFromSharedPreference(settings);
				fireMessageFromManager(thisUser, InAppMessageType.LOADED);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#saveUserChoice(android.content.Context)
	 */
	@Override
	public Runnable saveUserChoice(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				SharedPreferences settings = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
				writeUserToSharedPreference(settings);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.UserManagerInterface#deleteUserChoice(android.content.Context)
	 */
	@Override
	public Runnable deleteUserChoice(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				SharedPreferences preferences = context.getSharedPreferences(CHOOSEN_USER_PREF, 0);
				deleteUserFromSharedPreference(preferences);
			}
		};
	}

	/**
	 * saves the user as 
	 * @param preferences
	 * @param user
	 * @return
	 */
	private boolean writeUserToSharedPreference(SharedPreferences preferences){
		Editor editor=  preferences.edit();
		editor.putBoolean(User.HELFER, thisUser.getHelfer());
		editor.putInt(User.AGE, thisUser.getAge());
		editor.putString(User.ID, thisUser.getId());
		editor.putString(User.HANDYNUMBER, thisUser.getHandyNr());
		editor.putString(User.NAME, thisUser.getName());
		editor.putString(User.PICTURE, thisUser.getPicture());

		return editor.commit();
	}
	/**
	 * 
	 * @param preferences
	 * @return
	 */
	private boolean readUserFromSharedPreference(SharedPreferences preferences){
		User user = new User(preferences.getString(User.ID,null),
				preferences.getString(User.NAME, null), 
				preferences.getBoolean(User.HELFER, false), 
				preferences.getString(User.PICTURE, null), 
				preferences.getInt(User.AGE, Integer.MIN_VALUE), 
				preferences.getString(User.HANDYNUMBER, null));
		if (user.getId() == null) {
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
	private boolean deleteUserFromSharedPreference(SharedPreferences preferences){
		Editor editor = preferences.edit();
		editor.clear();
		return editor.commit();
	}

	public Runnable clear() {
		return new Runnable() {

			@Override
			public void run() {
				synchronized (users) {
					users.clear();
				}
			}
		};

	}

	@Override
	public UserInterface getUserById(String id) {
		return users.get(id);
	}
}
