/**
 * 
 */
package com.android.helpme.demo.interfaces.ManagerInterfaces;

import java.util.ArrayList;

import org.jdom2.Element;

import android.content.Context;

import com.android.helpme.demo.eventmanagement.eventListeners.TaskEventListener;
import com.android.helpme.demo.interfaces.TaskInterface;
import com.android.helpme.demo.interfaces.UserInterface;

/**
 * @author Andreas Wieland
 *
 */
public interface TaskManagerInterface extends ManagerInterface{
	
	public boolean init();
	
	public ArrayList<Element> getHistory();

	public TaskInterface getTask();
	
	public void startNewTask();
	
	public void startNewTask(UserInterface user);
	
	public void stopTask();
	
	public void setContext(Context context);
	
	public ArrayList<Element> loadHistory(Context applicationContext);
	
	public void removeTaskEventListener(TaskEventListener taskEventListener);
	public void addTaskEventListener(TaskEventListener taskEventListener);
	
}
