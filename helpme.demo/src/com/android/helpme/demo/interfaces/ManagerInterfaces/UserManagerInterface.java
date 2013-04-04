package com.android.helpme.demo.interfaces.ManagerInterfaces;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

import com.android.helpme.demo.eventmanagement.eventListeners.UserEventListener;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.utils.User;

public interface UserManagerInterface  extends ManagerInterface{

	public  boolean addUser(UserInterface position);
	
	public boolean removeUser(UserInterface user);

	public  ArrayList<User> getUsers();

	public  UserInterface getUserByName(String userName);
	public  UserInterface getUserById(String id);

	public ArrayList<User> readUsersFromProperty(Context context);

	public boolean saveUserChoice(Context context);

	public UserInterface readUserChoice(Context context);
	
	public boolean deleteUserChoice(Context context);

	public UserInterface thisUser();

	public UserInterface getThisUser();

	public void setThisUser(UserInterface userInterface, Context context);

	public boolean isUserSet();
	
	public void addUserEventListener(UserEventListener userEventListener);
	public void removeUserEventListener(UserEventListener userEventListener);
	
	public boolean init();
}