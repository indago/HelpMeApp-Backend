package com.android.helpme.demo.interfaces;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

import com.android.helpme.demo.utils.User;

public interface UserManagerInterface {

	public  boolean addUser(User position);

	public  ArrayList<User> getUsers();

	public  UserInterface getUserByName(String userName);
	public  UserInterface getUserById(String id);

	public Runnable readUserFromProperty(Context context);

	public Runnable saveUserChoice(Context context);

	public Runnable readUserChoice(Context context);
	
	public Runnable deleteUserChoice(Context context);

	public UserInterface thisUser();

	public UserInterface getThisUser();

	public void setThisUser(UserInterface user, String id);
	
	public Runnable setThisUser(UserInterface userInterface, Context context);

}