package com.android.helpme.demo.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Context;

import com.android.helpme.demo.messagesystem.AbstractMessageSystem;

public interface MessageOrchestratorInterface extends PropertyChangeListener, MessageHandlerInterface{

	/**
	 * adds new Message System to Listen to;
	 * @param messageSystem
	 */
	public void listenToMessageSystem(AbstractMessageSystem messageSystem);
}