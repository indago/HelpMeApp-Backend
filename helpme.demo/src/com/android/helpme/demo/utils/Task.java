/**
 * 
 */
package com.android.helpme.demo.utils;

import java.util.Date;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.util.Log;

import com.android.helpme.demo.interfaces.PositionManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.UserManagerInterface;
import com.android.helpme.demo.interfaces.ManagerInterfaces.RabbitMQManagerInterface.ExchangeType;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.position.Position;

/**
 * @author Andreas Wieland
 *
 */
public class Task extends Observable implements TaskInterface{
	private UserInterface helpee;
	private UserInterface helper;
	private Timer timer;
	private boolean answered;
	private String exchangeName;
	private Position startPosition;
	private Position stopPosition;
	private long startTime;
	private long stopTime;
	private String state;
	private XMLOutputter xmlOutputter;
	private Document document;
	UserManagerInterface userManagerInterface;
	RabbitMQManagerInterface rabbitMQManagerInterface;
	PositionManagerInterface positionManagerInterface;

	/**
	 * 
	 */
	public Task() {
		userManagerInterface = UserManager.getInstance();
		rabbitMQManagerInterface = RabbitMQManager.getInstance();
		positionManagerInterface = PositionManager.getInstance();
		answered = false;
		helpee = null;
		state = null;
		xmlOutputter = new XMLOutputter(Format.getCompactFormat());
		startPosition = null;
		stopPosition = null;
		startTime = -1;
		stopTime = -1;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#startTask(com.android.helpme.demo.interfaces.UserInterface)
	 */
	@Override
	public void startTask(UserInterface user) {
		helper = userManagerInterface.getThisUser();
		helpee = user;
//		setAnswered();

		positionManagerInterface.startLocationTracking();
		exchangeName = user.getId();
		startPosition = user.getPosition();
		startTime = user.getPosition().getMeasureDateTime();
		rabbitMQManagerInterface.subscribeToChannel(exchangeName, ExchangeType.fanout);
		state = LOOKING;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#startTask()
	 */
	@Override
	public void startTask() {
		helpee = userManagerInterface.getThisUser();
		positionManagerInterface.startLocationTracking();
		exchangeName = userManagerInterface.getThisUser().getId();
		startTime = System.currentTimeMillis();

		rabbitMQManagerInterface.subscribeToChannel(exchangeName, ExchangeType.fanout);
		state = LOOKING;
		timer = new Timer();
		timer.schedule(createTimerTask(), TIMERDELAY);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#sendPosition(com.android.helpme.demo.utils.position.Position)
	 */
	@Override
	public void sendPosition(Position position){
		//TODO
		Element object;
		object = UserManager.getInstance().getThisUser().getElement();
		//		userManagerInterface.thisUser().updatePosition(position);
		object.addContent(position.getElement());
		document = new Document(object);
		//		if (answered) {
		//			run(rabbitMQManagerInterface.sendStringOnChannel(xmlOutputter.outputString(document), exchangeName));
		//		}else {
		run(rabbitMQManagerInterface.sendStringOnMain(xmlOutputter.outputString(document)));
		//		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isAnswered()
	 */
	@Override
	public Boolean isAnswered(){
		return answered;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#getUser()
	 */
	@Override
	public UserInterface getUser() {
		return helpee;
	}

	private void run(Runnable runnable){
		ThreadPool.runTask(runnable);
	}
	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#getDistance()
	 */
	@Override
	public double getDistance(){
		Position helperPosition = this.helpee.getPosition();
		Position ourPosition = userManagerInterface.thisUser().getPosition();
		return helperPosition.calculateSphereDistance(ourPosition);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#updatePosition(com.android.helpme.demo.interfaces.UserInterface)
	 */
	@Override
	public void updatePosition(UserInterface userInterface) {
		// if we are the helper we update the helpee's position
		if (userManagerInterface.getThisUser().isHelper()) {
			helpee.updatePosition(userInterface.getPosition());
		}else {
			helper.updatePosition(userInterface.getPosition());
		}
		/*// if our Task is not answered yet, with this it is now
		if (!answered) {
			setUser(userInterface);
		}else {
			this.helpee.updatePosition(userInterface.getPosition());
		}*/
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isUserInRange(int)
	 */
	@Override
	public boolean isUserInRange(int range) {
		if (answered) {
			return userManagerInterface.getThisUser().getDistanceTo(helpee) <= range;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isUserInShortDistance()
	 */
	@Override
	public boolean isUserInShortDistance(){
		return isUserInRange(SHORTDISTANCE);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isUserInMidDistance()
	 */
	@Override
	public boolean isUserInMidDistance() {
		return isUserInRange(MIDDISTANCE);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isUserInLongDistance()
	 */
	@Override
	public boolean isUserInLongDistance() {
		return isUserInRange(LONGDISTANCE);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#setSuccesfull()
	 */
	@Override
	public void setSuccesfull() {
		if (state.equalsIgnoreCase(RUNNING) && answered) {
			state = SUCCESSFUL;
		}
	}

	@Override
	public void setAnswered() {
		answered = true;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#state()
	 */
	@Override
	public String state(){
		return state;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#setFailed()
	 */
	@Override
	public void setFailed() {
		if (state.equalsIgnoreCase(RUNNING)) {
			state = FAILED;
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#stopUnfinishedTask()
	 */
	@Override
	public void stopUnfinishedTask(){
		positionManagerInterface.stopLocationTracking();
		rabbitMQManagerInterface.endSubscribtionToChannel(exchangeName);
		if (helpee != null) {
			userManagerInterface.removeUser(helpee);
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#stopTask()
	 */
	@Override
	public Element stopTask() {
		positionManagerInterface.stopLocationTracking();
		stopTime = System.currentTimeMillis();
		sendPosition(helpee.getPosition());
		sendPosition(helpee.getPosition());

		rabbitMQManagerInterface.endSubscribtionToChannel(exchangeName);
		if (positionManagerInterface.getLastPosition() == null) {
			stopPosition = startPosition;
		}else {
			stopPosition = positionManagerInterface.getLastPosition();
		}
		return toXML();
	}

	@Override
	public Element toXML() {
		Element element = new Element(TASK);
		if (startPosition != null) {
			element.addContent(startPosition.getElementAs(START_POSITION));
		}
		if (stopPosition != null) {
			element.addContent(stopPosition.getElementAs(STOP_POSITION));
		}
		if (helpee != null) {
			element.addContent(helpee.getElement(HELPEE));
		}
		if (helper != null) {
			element.addContent(helper.getElement(HELPER));
		}
		if (startTime != -1) {
			element.setAttribute(START_TIME, new Long(startTime).toString());	
		}
		if (stopTime != -1) {
			element.setAttribute(STOP_TIME, new Long(stopTime).toString());
		}
		element.setText(state);
		return element;
	}

	@Override
	public void fromXML(Element element) {
		try{
			helpee = new User(element.getChild(HELPEE));
			helper = new User(element.getChild(HELPER));
			state = element.getText();
			if (element.getAttribute(STOP_TIME) != null) {
				stopTime = element.getAttribute(STOP_TIME).getLongValue();
			}
			if (element.getAttribute(START_TIME) != null) {
				startTime = element.getAttribute(START_TIME).getLongValue();
			}//TODO
		}catch (DataConversionException e) {
			Log.e(TASK, e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.TaskInterface#isSuccsessfull()
	 */
	@Override
	public boolean isSuccsessfull() {
		if (state.equalsIgnoreCase(SUCCESSFUL)) {
			return true;
		}
		return false;
	}

	private TimerTask createTimerTask(){
		return new TimerTask() {

			@Override
			public void run() {
				if (!answered) {
					setChanged();
					notifyObservers();
				}
			}
		};
	}
}
