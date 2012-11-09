package com.android.helpme.demo.manager;

import java.util.ArrayList;
import com.android.helpme.demo.exceptions.UnkownMessageType;
import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.HistoryManagerInterface;
import com.android.helpme.demo.interfaces.MessageHandlerInterface;
import com.android.helpme.demo.interfaces.PositionManagerInterface;
import com.android.helpme.demo.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.interfaces.UserManagerInterface;
import com.android.helpme.demo.interfaces.DrawManagerInterface.DRAWMANAGER_TYPE;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public abstract class MessageHandler extends AbstractMessageSystem implements MessageHandlerInterface{

	abstract protected boolean reloadDatabase();

	protected static RabbitMQManagerInterface rabbitMQManagerInterface = RabbitMQManager.getInstance();
	protected static UserManagerInterface userManagerInterface = UserManager.getInstance();
	protected static PositionManagerInterface positionManagerInterface = PositionManager.getInstance();
	protected static HistoryManagerInterface historyManagerInterface = HistoryManager.getInstance();

	/**
	 * Handels the Messages form the {@link PositionManager}
	 * @param message
	 */
	protected void handlePositionMessage(InAppMessage message) {
		switch (message.getType()) {
		case LOCATION:
			if (!(message.getObject() instanceof Position)) {
				fireError(new WrongObjectType(message.getObject(), Position.class));
				return;
			}
			Position position = (Position) message.getObject();
			userManagerInterface.thisUser().updatePosition(position);

			if (getDrawManager(DRAWMANAGER_TYPE.MAP) != null) {
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(userManagerInterface.getThisUser());
			}

			if (historyManagerInterface.getTask() != null) {
				historyManagerInterface.getTask().sendPosition(position);
			}
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	/**
	 * Handels the Messages form the {@link RabbitMQManager} 
	 * @param message
	 */
	protected void handleRabbitMQMessages(InAppMessage message) {
		switch (message.getType()) {
		case UNBOUND_FROM_SERVICE:
			//TODO
			break;
		case BOUND_TO_SERVICE:
			run(rabbitMQManagerInterface.connect());
			break;

		case CONNECTED:
			run(rabbitMQManagerInterface.subscribeToMainChannel());
			break;

		case RECEIVED_DATA:
			if (!(message.getObject() instanceof User)) {
				fireError(new WrongObjectType(message.getObject(), User.class));
				return;
			}
			User user = (User) message.getObject();

			if (!UserManager.getInstance().isUserSet()) {
				return;
			}

			if (!user.getId().equalsIgnoreCase(userManagerInterface.getThisUser().getId())) {

				if (userManagerInterface.getThisUser().isHelper()) {
					handleIncomingUserAsHelper(user);
				}else {
					if (user.isHelper()) {
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
	 * if this user is a Helper this Method will be called and starts the List {@link DrawManagerInterface}
	 * @param incomingUser
	 */
	private void handleIncomingUserAsHelper(User incomingUser){
		if (userManagerInterface.addUser(incomingUser)) {
			run(rabbitMQManagerInterface.showNotification(incomingUser));
		}

		if (historyManagerInterface.getTask() != null) {
			historyManagerInterface.getTask().updatePosition(incomingUser);
		}

		if (getDrawManager(DRAWMANAGER_TYPE.MAP) != null) {

			if (historyManagerInterface.getTask().isUserInShortDistance()) {
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(historyManagerInterface.getTask());

			}else{
				getDrawManager(DRAWMANAGER_TYPE.MAP).drawThis(incomingUser);
			}
		} else {
			getDrawManager(DRAWMANAGER_TYPE.LIST).drawThis(incomingUser);
		}
	}

	/**
	 * if this user is a Help Seeker this Method will be called and starts the Map {@link DrawManagerInterface}
	 * @param incomingUser
	 */
	private void handleIncomingUserAsHelperSeeker(User incomingUser){
		userManagerInterface.addUser(incomingUser);
		historyManagerInterface.getTask().updatePosition(incomingUser);

		if (getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING) != null) {
			if (historyManagerInterface.getTask().isUserInShortDistance()) {
				getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING).drawThis(historyManagerInterface.getTask());
			}
			else {
				getDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING).drawThis(incomingUser);
			}

		} else {

			getDrawManager(DRAWMANAGER_TYPE.SEEKER).drawThis(incomingUser);
		}
	}

	protected void handleHistoryMessages(InAppMessage message) {
		switch (message.getType()) {
		case TIMEOUT:
			historyManagerInterface.stopTask();
			getDrawManager(DRAWMANAGER_TYPE.SEEKER).drawThis(message.getObject());
			break;
		case LOADED:
			if (getDrawManager(DRAWMANAGER_TYPE.HISTORY) != null) {
				getDrawManager(DRAWMANAGER_TYPE.HISTORY).drawThis(message.getObject());
			}
			break;
			
		case HISTORY:
			if (getDrawManager(DRAWMANAGER_TYPE.HISTORY) != null) {
				getDrawManager(DRAWMANAGER_TYPE.HISTORY).drawThis(message.getObject());
			}
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	/**
	 * Handles Messages from the {@link UserManager}
	 * @param message
	 */
	protected void handleUserMessages(InAppMessage message) {
		switch (message.getType()) {
		case LOADED:
			if (getDrawManager(DRAWMANAGER_TYPE.SWITCHER) != null) {
				getDrawManager(DRAWMANAGER_TYPE.SWITCHER).drawThis(message.getObject());
			}

			break;
		case RECEIVED_DATA:
			if (!(message.getObject() instanceof ArrayList<?>)) {
				fireError(new WrongObjectType(message.getObject(), ArrayList.class));
				return;
			}

			if (getDrawManager(DRAWMANAGER_TYPE.LOGIN) != null) {
				getDrawManager(DRAWMANAGER_TYPE.LOGIN).drawThis(message.getObject());
			}
			break;
			
		case CHANGED:
			if (getDrawManager(DRAWMANAGER_TYPE.LIST) != null) {
				getDrawManager(DRAWMANAGER_TYPE.LIST).drawThis(message.getObject());
			}
			
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}
}
