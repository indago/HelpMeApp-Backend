/**
 * 
 */
package com.android.helpme.demo.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import android.content.Context;
import android.util.Log;

import com.android.helpme.demo.eventmanagement.eventListeners.PositionEventListener;
import com.android.helpme.demo.eventmanagement.eventListeners.TaskEventListener;
import com.android.helpme.demo.eventmanagement.events.PositionEvent;
import com.android.helpme.demo.eventmanagement.events.TaskEvent;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.TaskManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.TaskInterface;
import com.android.helpme.demo.utils.position.Position;

/**
 * @author Andreas Wieland
 * 
 */
public class TaskManager implements TaskManagerInterface, Observer, PositionEventListener{
	private static final String LOGTAG = TaskManager.class.getSimpleName();
	private static TaskManager manager;
	private static Task currentTask;
	private static final String FILENAME = "history_file";
	private Set<TaskEventListener> taskEventListeners;
	private Context context;
	private boolean writing = false;
	private Element root;
	private Document document;

	public static TaskManager getInstance() {
		if (manager == null) {
			manager = new TaskManager();
		}
		return manager;
	}

	/**
	 * 
	 */
	private TaskManager() {
		context = null;
		root = new Element("root");
		document = new Document(root);
		taskEventListeners= new HashSet<TaskEventListener>();
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
	 * 
	 * @see
	 * com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#setContext
	 * (android.content.Context)
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	@Override
	public ArrayList<Element> getHistory() {
		//		return new Runnable() {
		//
		//			@Override
		//			public void run() {
		ArrayList<Element> arrayList = new ArrayList<Element>(root.getChildren());
		return arrayList;
		//			}
		//		};

	}

	@Override
	public TaskInterface getTask() {
		return currentTask;
	}

	@Override
	public void startNewTask() {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask();
		notifyTaskEventListeners(new TaskEvent(manager, currentTask));
	}

	@Override
	public void startNewTask(UserInterface user) {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask(user);
		notifyTaskEventListeners(new TaskEvent(manager, currentTask));
	}

	@Override
	public void stopTask() {
		if (currentTask != null) {
			if (currentTask.isSuccsessfull()) {
				root.addContent(currentTask.stopTask());
				writeHistory();
			} else {
				currentTask.stopUnfinishedTask();
			}
			currentTask = null;
		}
	}

	private boolean readHistory() {
		if (context != null) {
			File file = context.getFileStreamPath(FILENAME);
			if (file == null || !file.exists()) {
				return false;
			}
			try {
				SAXBuilder saxBuilder = new SAXBuilder();
				document = saxBuilder.build(file);
				root = document.getRootElement();				
				return true;
			} catch (IOException e) {
				Log.e(LOGTAG, e.toString());
			} catch (JDOMException e) {
				Log.e(LOGTAG, e.toString());
			} 
		}
		return false;
	}


	private boolean writeHistory() {
		if (context != null) {
			try {
				writing = true;

				Writer writer=new FileWriter(context.getFileStreamPath(FILENAME));
				XMLOutputter xmlOutputter = new XMLOutputter(Format.getCompactFormat());
				xmlOutputter.output(document, writer);
				writing = false;
				return true;
			} catch (IOException e) {
				Log.e(LOGTAG, e.toString());
			} 
		}
		return false;
	}

	@Override
	public ArrayList<Element> loadHistory(Context applicationContext) {
		setContext(applicationContext);
		//		return new Runnable() {
		//
		//			@Override
		//			public void run() {
		readHistory();
		ArrayList<Element> arrayList = new ArrayList<Element>(root.getChildren());
		return arrayList;
		//			}
		//		};
	}
	
	@Override
	public void addTaskEventListener(TaskEventListener taskEventListener) {
		taskEventListeners.add(taskEventListener);
	}
	
	@Override
	public void removeTaskEventListener(TaskEventListener taskEventListener) {
		taskEventListeners.remove(taskEventListener);
	}
	
	private void notifyTaskEventListeners(TaskEvent taskEvent){
		for (TaskEventListener listener : taskEventListeners) {
			listener.getTaskEvent(taskEvent);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (currentTask != null) {
			notifyTaskEventListeners(new TaskEvent(manager, currentTask));
//TODO			fireMessageFromManager(currentTask, InAppMessageType.TIMEOUT);
		}
	}

	@Override
	public void getPositionEvent(PositionEvent positionEvent) {
		Position position = positionEvent.getPosition();
		currentTask.sendPosition(position);
	}
}
