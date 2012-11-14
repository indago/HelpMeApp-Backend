/**
 * 
 */
package com.android.helpme.demo.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;

import com.android.helpme.demo.exceptions.DontKnowWhatHappenedException;
import com.android.helpme.demo.interfaces.HistoryManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.Task;

/**
 * @author Andreas Wieland
 * 
 */
public class HistoryManager extends AbstractMessageSystem implements HistoryManagerInterface, Observer {
	private static final String LOGTAG = HistoryManager.class.getSimpleName();
	private static HistoryManager manager;
	private static Task currentTask;
	private InAppMessage message;
	private static final String FILENAME = "history_file";
	private Context context;
	private ArrayList<JSONObject> arrayList;

	public static HistoryManager getInstance() {
		if (manager == null) {
			manager = new HistoryManager();
		}
		return manager;
	}

	/**
	 * 
	 */
	private HistoryManager() {
		context = null;
		arrayList = new ArrayList<JSONObject>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#setContext
	 * (android.content.Context)
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#
	 * getLogTag()
	 */
	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#
	 * getManager()
	 */
	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	@Override
	public Runnable getHistory() {
		return new Runnable() {

			@Override
			public void run() {
				fireMessageFromManager(arrayList, InAppMessageType.HISTORY);
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystem#getMessage()
	 */
	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystem#setMessage
	 * (com.android.helpme.demo.messagesystem.InAppMessage)
	 */
	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		this.message = inAppMessage;
	}

	@Override
	public Task getTask() {
		return currentTask;
	}

	@Override
	public void startNewTask() {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask();
	}

	@Override
	public void startNewTask(UserInterface user) {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask(user);
	}

	@Override
	public void stopTask() {
		if (currentTask != null) {
			if (currentTask.isSuccsessfull()) {
				arrayList.add(currentTask.stopTask());
			} else {
				currentTask.stopUnfinishedTask();
			}
			currentTask = null;
		}
	}

	private boolean readHistory() {
		if (context != null) {
			File file = context.getFileStreamPath(FILENAME);
			if (!file.exists()) {
				return false;
			}
			try {
				FileInputStream inputStream = context.openFileInput(FILENAME);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String string = null;
				JSONParser parser = new JSONParser();
				while ((string = reader.readLine()) != null) {
					JSONObject jsonObject = (JSONObject) parser.parse(string);
					arrayList.add(jsonObject);
				}
				reader.close();
				inputStream.close();
				return true;
			} catch (IOException e) {
				fireError(e);
			} catch (ParseException e) {
				fireError(e);
			}
		}
		return false;
	}

	private boolean writeHistory() {
		if (context != null) {
			try {
				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

				for (JSONObject jsonObject : arrayList) {
					String string = jsonObject.toJSONString();
					string = string.replaceAll("(\\r|\\n)", "");
					writer.write(string);
				}

				writer.close();
				fos.close();
				return true;
			} catch (IOException e) {
				fireError(e);
			}
		}
		return false;
	}

	@Override
	public Runnable loadHistory(Context applicationContext) {
		setContext(applicationContext);
		return new Runnable() {

			@Override
			public void run() {
				if (readHistory()) {
					fireMessageFromManager(arrayList, InAppMessageType.LOADED);
				}
			}
		};
	}

	@Override
	public Runnable saveHistory(Context applicationContext) {
		setContext(applicationContext);
		return new Runnable() {

			@Override
			public void run() {
				if (!writeHistory()) {
					fireError(new DontKnowWhatHappenedException());
				}
			}
		};
	}

	@Override
	public void update(Observable observable, Object data) {
		if (currentTask != null) {
			fireMessageFromManager(currentTask, InAppMessageType.TIMEOUT);
		}
	}
}
