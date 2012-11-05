package com.android.helpme.demo.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.MessageOrchestratorInterface;
import com.android.helpme.demo.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.interfaces.DrawManagerInterface.DRAWMANAGER_TYPE;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.User;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class MessageOrchestrator extends MessageHandler implements MessageOrchestratorInterface {
	private static final String LOGTAG = MessageOrchestrator.class.getSimpleName();
	private static MessageOrchestrator messageOrchestrator;
	private InAppMessage message;
	private ArrayList<String> arrayList;
	private HashMap<DrawManagerInterface.DRAWMANAGER_TYPE, DrawManagerInterface> drawManagerMap;

	public static MessageOrchestrator getInstance() {
		if (messageOrchestrator == null) {
			messageOrchestrator = new MessageOrchestrator();
		}
		return messageOrchestrator;
	}

	private MessageOrchestrator() {
		arrayList = new ArrayList<String>();
		drawManagerMap = new HashMap<DrawManagerInterface.DRAWMANAGER_TYPE, DrawManagerInterface>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.interfaces.MessageOrchestratorInterface
	 * #listenToMessageSystem
	 * (com.android.helpme.demo.messagesystem.AbstractMessageSystem)
	 */
	@Override
	public void listenToMessageSystem(AbstractMessageSystem messageSystem) {
		if (!(getInstance().arrayList.contains(messageSystem.getLogTag()))) {
			messageSystem.addPropertyChangeListener(getInstance());
			getInstance().arrayList.add(messageSystem.getLogTag());
		}
		;
	}

	@Override
	protected boolean reloadDatabase() {
		// TODO Auto-generated method stub
		return false;
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
	protected void setMessage(InAppMessage message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.MessageOrchestratorInterface#propertyChange
	 * (java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (!(event.getNewValue() instanceof InAppMessage)) {
			fireError(new WrongObjectType(event, InAppMessage.class));
		}
		InAppMessage message = (InAppMessage) event.getNewValue();
		if (message.getType() == InAppMessageType.ERROR) {
			Log.e(((AbstractMessageSystemInterface)message.getSource()).getLogTag(), ((Exception)message.getObject()).toString());
		}else if (message.getSource() instanceof RabbitMQManagerInterface) {
			handleRabbitMQMessages(message);
		}else if (message.getSource() instanceof PositionManager) {
			handlePositionMessage(message);
		}else if (message.getSource() instanceof UserManager) {
			handleUserMessages(message);
		}else if(message.getSource() instanceof HistoryManager){
			handleHistoryMessages(message);
		}else{
			Log.e(LOGTAG, "no handle Method defined for: " + message.getObject().toString() );
		}
	}

	@Override
	public AbstractMessageSystemInterface getManager() {
		return messageOrchestrator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.MessageOrchestratorInterface#getDrawManagers
	 * ()
	 */
	@Override
	public HashMap<DrawManagerInterface.DRAWMANAGER_TYPE, DrawManagerInterface> getDrawManagers() {
		return drawManagerMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.MessageOrchestratorInterface#getDrawManager
	 * (com.android.helpme.demo.DrawManager.DRAWMANAGER_TYPE)
	 */
	@Override
	public DrawManagerInterface getDrawManager(DRAWMANAGER_TYPE type) {
		return drawManagerMap.get(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.MessageOrchestratorInterface#setDrawManager
	 * (com.android.helpme.demo.DrawManager.DRAWMANAGER_TYPE,
	 * com.android.helpme.demo.DrawManager)
	 */
	@Override
	public void addDrawManager(DrawManagerInterface.DRAWMANAGER_TYPE type, DrawManagerInterface drawManager) {
		this.drawManagerMap.put(type, drawManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.manager.interfaces.MessageHandlerInterface#
	 * removeDrawManager
	 * (com.android.helpme.demo.gui.DrawManager.DRAWMANAGER_TYPE)
	 */
	@Override
	public void removeDrawManager(DRAWMANAGER_TYPE type) {
		this.drawManagerMap.remove(type);
	}
}
