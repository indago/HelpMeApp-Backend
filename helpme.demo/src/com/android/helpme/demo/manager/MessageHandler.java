package com.android.helpme.demo.manager;

import java.util.ArrayList;

import com.android.helpme.demo.exceptions.DontKnowWhatHappenedException;
import com.android.helpme.demo.exceptions.UnkownMessageType;
import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.interfaces.ManagerInterfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.TaskManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.UserManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.DrawManagerInterface.DRAWMANAGER_TYPE;
import com.android.helpme.demo.interfaces.MessageHandlerInterface;
import com.android.helpme.demo.interfaces.PositionManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
@Deprecated
/**
 * 
 * @author Andreas Wieland
 * 
 */
public abstract class MessageHandler extends AbstractMessageSystem implements MessageHandlerInterface {

	abstract protected boolean reloadDatabase();

	protected static RabbitMQManagerInterface rabbitMQManagerInterface = RabbitMQManager.getInstance();
	protected static UserManagerInterface userManagerInterface = UserManager.getInstance();
	protected static PositionManagerInterface positionManagerInterface = PositionManager.getInstance();
	protected static TaskManagerInterface historyManagerInterface = TaskManager.getInstance();

	/**
	 * Handels the Messages form the {@link PositionManager}
	 * 
	 * @param message
	 */
	protected void handlePositionMessage(InAppMessage message) {
		switch(message.getType()) {
		case LOCATION:
			if(!(message.getObject() instanceof Position)) {
				fireError(new WrongObjectType(message.getObject(), Position.class));
				return;
			}
			if (!userManagerInterface.isUserSet()) {
				return;
			}
			
			Position position = (Position) message.getObject();
			if(userManagerInterface.thisUser() != null) {
				userManagerInterface.thisUser().updatePosition(position);
			}else {
				try{
				positionManagerInterface.stopLocationTracking();
				}catch(NullPointerException exception){
					fireError(new DontKnowWhatHappenedException(exception.toString()));
				}
			}
			
			// draw the position on the map(only helper has a map)
			if(getDrawManager(DRAWMANAGER_TYPE.MAP) != null) {
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(userManagerInterface.getThisUser());
			}


			if(historyManagerInterface.getTask() != null ) {
				historyManagerInterface.getTask().sendPosition(position);
				
				// if our position is in short range
				if(historyManagerInterface.getTask().isUserInShortDistance()){
					historyManagerInterface.getTask().setSuccesfull();
					
					// we set Task as succsesful and draw the finish
					if (userManagerInterface.getThisUser().isHelper()) {
						if(getDrawManager(DRAWMANAGER_TYPE.MAP) != null) {
							getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(historyManagerInterface.getTask());
						}
					}
					
					else {
						if(getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING) != null) {
							getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING).drawThis(historyManagerInterface.getTask());
						}
					}
				}
			}

			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	/**
	 * Handels the Messages form the {@link RabbitMQManager}
	 * 
	 * @param message
	 */
	protected void handleRabbitMQMessages(InAppMessage message) {
		switch(message.getType()) {
		case UNBOUND_FROM_SERVICE:
			// TODO
			break;
		case BOUND_TO_SERVICE:
			rabbitMQManagerInterface.connect();
			break;

		case CONNECTED:
			rabbitMQManagerInterface.subscribeToMainChannel();
			break;

		case RECEIVED_DATA:
			if(!(message.getObject() instanceof User)) {
				fireError(new WrongObjectType(message.getObject(), User.class));
				return;
			}
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
			}
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	/**
	 * if this user is a Helper this Method will be called and starts the List
	 * {@link DrawManagerInterface}
	 * 
	 * @param incomingUser
	 */
	private void handleIncomingUserAsHelper(User incomingUser) {
		if(userManagerInterface.addUser(incomingUser)) {
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

	}

	/**
	 * if this user is a Help Seeker this Method will be called
	 * and starts the Map {@link DrawManagerInterface}
	 * 
	 * @param incomingUser
	 */
	private void handleIncomingUserAsHelperSeeker(User incomingUser) {
		userManagerInterface.addUser(incomingUser);
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
	}

	protected void handleHistoryMessages(InAppMessage message) {
		switch(message.getType()) {
		case TIMEOUT:
			historyManagerInterface.stopTask();
			getDrawManager(DRAWMANAGER_TYPE.SEEKER).drawThis(message.getObject());
			break;
		case LOADED:
			if(getDrawManager(DRAWMANAGER_TYPE.HISTORY) != null) {
				getDrawManager(DRAWMANAGER_TYPE.HISTORY).drawThis(message.getObject());
			}
			break;

		case HISTORY:
			if(getDrawManager(DRAWMANAGER_TYPE.HISTORY) != null) {
				getDrawManager(DRAWMANAGER_TYPE.HISTORY).drawThis(message.getObject());
			}
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

//	/**
//	 * Handles Messages from the {@link UserManager}
//	 * 
//	 * @param message
//	 */
//	protected void handleUserMessages(InAppMessage message) {
//		switch(message.getType()) {
//		case LOADED:
//			if(getDrawManager(DRAWMANAGER_TYPE.SWITCHER) != null) {
//				getDrawManager(DRAWMANAGER_TYPE.SWITCHER).drawThis(message.getObject());
//			}
//
//			break;
//		case RECEIVED_DATA:
//			if(!(message.getObject() instanceof ArrayList<?>)) {
//				fireError(new WrongObjectType(message.getObject(), ArrayList.class));
//				return;
//			}
//
//			if(getDrawManager(DRAWMANAGER_TYPE.LOGIN) != null) {
//				getDrawManager(DRAWMANAGER_TYPE.LOGIN).drawThis(message.getObject());
//			}
//			break;
//
//		case CHANGED:
//			if(getDrawManager(DRAWMANAGER_TYPE.HELPER) != null) {
//				getDrawManager(DRAWMANAGER_TYPE.HELPER).drawThis(message.getObject());
//			}
//
//			break;
//
//		default:
//			fireError(new UnkownMessageType());
//			break;
//		}
//	}
}
