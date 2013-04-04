package com.android.helpme.demo.manager;

import java.util.Set;

import android.util.Log;

import com.android.helpme.demo.eventmanagement.eventListeners.DataEventListener;
import com.android.helpme.demo.eventmanagement.events.DataEvent;
import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.interfaces.ManagerInterfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.RecievedDataManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.DrawManagerInterface.DRAWMANAGER_TYPE;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.utils.User;

public class RecievedDataManager implements RecievedDataManagerInterface{
	private static final String LOGTAG = RecievedDataManager.class.getSimpleName();
	private static RecievedDataManager manager;

	private RecievedDataManager(){

	}

	@Override
	public void getDataEvent(DataEvent dataEvent) {
		//TODO
		try{
			InAppMessage message = dataEvent.getInAppMessage();
			if(!(message.getObject() instanceof User)) {
				throw(new WrongObjectType(message.getObject(), User.class));
			}
			User user = (User) message.getObject();
			
			if (UserManager.getInstance().thisUser().isHelper()) {
				handleIncomingUserAsHelper(user);
			}else {
				handleIncomingUserAsHelpee(user);
			}
		}catch (WrongObjectType e) {
			Log.e(LOGTAG, "getDataEvent: " +e.toString());
		}/*
		User user = (User) message.getObject();

		if(!UserManager.getInstance().isUserSet()) {
			return;
		}

		if(!user.getId().equalsIgnoreCase(userManagerInterface.getThisUser().getId())) {

			if(userManagerInterface.getThisUser().isHelper()) {
				handleIncomingUserAsHelper(user);
			} else {
				if(user.isHelper()) {
					handleIncomingUserAsHelperSeeker(user);
				}

			}
		}*/

	}


	public void handleIncomingUserAsHelper(User incomingUser) {
		if(TaskManager.getInstance().getTask() != null){
			TaskManager.getInstance().startNewTask(incomingUser);
		}
		//TODO
		
	}
	/*	if(userManagerInterface.addUser(incomingUser)) {
//			run(rabbitMQManagerInterface.showNotification(incomingUser));
		}

		if(historyManagerInterface.getTask() != null && getDrawManager(DRAWMANAGER_TYPE.MAP) != null) {
			historyManagerInterface.getTask().updatePosition(incomingUser);

			if(historyManagerInterface.getTask().isUserInShortDistance()) {
				historyManagerInterface.getTask().setSuccesfull();
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(historyManagerInterface.getTask());

			} else {
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(incomingUser);
			}
		}

		if(getDrawManager(DRAWMANAGER_TYPE.HELPER) != null) {
			getDrawManager(DRAWMANAGER_TYPE.HELPER).drawThis(incomingUser);
		}

	}*/


	public void handleIncomingUserAsHelpee(User incomingUser) {
		//TODO
		TaskManager.getInstance().getTask().updatePosition(incomingUser);
	}
	/*	userManagerInterface.addUser(incomingUser);
		if(historyManagerInterface.getTask() != null) {
			historyManagerInterface.getTask().updatePosition(incomingUser);
		}

		if(getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING) != null) {
			if(historyManagerInterface.getTask().isUserInShortDistance()) {
				historyManagerInterface.getTask().setSuccesfull();
				getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING).drawThis(historyManagerInterface.getTask());
			} else {
				getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING).drawThis(incomingUser);
			}

		} else {
			if(getDrawManager(DRAWMANAGER_TYPE.SEEKER) != null) {
				getDrawManager(DRAWMANAGER_TYPE.SEEKER).drawThis(incomingUser);
			}

		}
	}*/

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

}
