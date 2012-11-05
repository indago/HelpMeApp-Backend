package com.android.helpme.demo.interfaces;

import java.util.HashMap;

import com.android.helpme.demo.interfaces.DrawManagerInterface.DRAWMANAGER_TYPE;
import com.android.helpme.demo.manager.MessageHandler;

public interface MessageHandlerInterface {

	/**
	 * Gets all {@link DrawManagerInterface} which are associated to the Message Handler
	 * @return
	 */
	public HashMap<DrawManagerInterface.DRAWMANAGER_TYPE, DrawManagerInterface> getDrawManagers();

	/**
	 * returns associated {@link DrawManagerInterface} with {@link DRAWMANAGER_TYPE}
	 * @param type
	 * @return
	 */
	public DrawManagerInterface getDrawManager(DRAWMANAGER_TYPE type);

	/**
	 * adds a new {@link DrawManagerInterface} with {@link DRAWMANAGER_TYPE} to {@link MessageHandler}
	 * @param type
	 * @param drawManager
	 */
	public void addDrawManager(DrawManagerInterface.DRAWMANAGER_TYPE type, DrawManagerInterface drawManager);
	
	public void removeDrawManager(DrawManagerInterface.DRAWMANAGER_TYPE type);

}