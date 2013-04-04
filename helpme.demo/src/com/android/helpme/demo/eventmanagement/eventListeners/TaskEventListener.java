/**
 * 
 */
package com.android.helpme.demo.eventmanagement.eventListeners;

import java.util.EventListener;

import com.android.helpme.demo.eventmanagement.events.TaskEvent;

/**
 * @author Andreas Wieland
 *
 */
public interface TaskEventListener extends EventListener {
	public void getTaskEvent(TaskEvent taskEvent);
}
