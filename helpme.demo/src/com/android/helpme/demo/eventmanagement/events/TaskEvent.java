package com.android.helpme.demo.eventmanagement.events;

import java.util.EventObject;

import com.android.helpme.demo.utils.TaskInterface;

/**
 * 
 * @author Andreas Wieland
 *
 */
public class TaskEvent extends EventObject{
	private TaskInterface task;
	private boolean request;
	private boolean accepted;
	
	public TaskEvent(Object source, TaskInterface task) {
		super(source);
		this.task = task;
	}

	public TaskInterface getTask() {
		return task;
	}

	public void setTask(TaskInterface task) {
		this.task = task;
	}
}
