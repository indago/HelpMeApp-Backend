package com.android.helpme.demo.interfaces;

import org.jdom2.Element;

import com.android.helpme.demo.utils.TaskState;
import com.android.helpme.demo.utils.position.Position;

public interface TaskInterface {

	public static final String FAILED = "failed";
	public static final String START_POSITION = "start_position";
	public static final String START_TIME = "start_time";
	public static final String ID = "id";
	public static final String STATE = "state";
	public static final String STOP_POSITION = "stop_position";
	public static final String STOP_TIME = "stop_time";
	public static final String TASK = "task";
	public static final String USER = "user";
	public static final String HELPER = "helper";
	public static final String HELPEE = "helpee";
	public static final int LONGDISTANCE = 5000;
	public static final int MIDDISTANCE = 500;
	public static final int SHORTDISTANCE = 50;
	public static final long TIMERDELAY = 60000;

	/**
	 * gets called by Helper
	 * @param user
	 */
	public void startTask(UserInterface user);

	/**
	 * gets called by Helpee
	 */
	public void startTask();

	public void sendPosition(Position position);

	public Boolean isAnswered();

	public UserInterface getHelpee();
	public UserInterface getHelper();

	public double getDistance();

	public void updatePosition(UserInterface userInterface);

	public boolean isUserInRange(int range);

	public boolean isUserInShortDistance();

	public boolean isUserInMidDistance();

	public boolean isUserInLongDistance();

	public void setSuccesfull();
	public void setAnswered();

	public void setFailed();

	public void stopUnfinishedTask();

	public Element stopTask();

	public boolean isSuccsessfull();
	
	public Element toXML();
	public void fromXML(Element element);
	
	public TaskState getState();
	
	public String getID();

}