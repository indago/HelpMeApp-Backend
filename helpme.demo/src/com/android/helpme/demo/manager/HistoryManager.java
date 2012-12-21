/**
 * 
 */
package com.android.helpme.demo.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import android.content.Context;

import com.android.helpme.demo.interfaces.HistoryManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.Task;

/**
 * @author Andreas Wieland
 * 
 */
public class HistoryManager extends AbstractMessageSystem implements HistoryManagerInterface, Observer {
	private static final String LOGTAG = HistoryManager.class.getSimpleName();
	private static HistoryManager manager;
	private static Task currentTask;
	private InAppMessage message;
	private static final String FILENAME = "history_file";
	private Context context;
	private boolean writing = false;
	private Element root;
	private Document document;

	public static HistoryManager getInstance() {
		if (manager == null) {
			manager = new HistoryManager();
		}
		return manager;
	}

	/**
	 * 
	 */
	private HistoryManager() {
		context = null;
		root = new Element("root");
		document = new Document(root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#setContext
	 * (android.content.Context)
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#
	 * getLogTag()
	 */
	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#
	 * getManager()
	 */
	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	@Override
	public Runnable getHistory() {
		return new Runnable() {

			@Override
			public void run() {
				ArrayList<Element> arrayList = new ArrayList<Element>(root.getChildren());
				fireMessageFromManager(arrayList, InAppMessageType.HISTORY);
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystem#getMessage()
	 */
	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.helpme.demo.messagesystem.AbstractMessageSystem#setMessage
	 * (com.android.helpme.demo.messagesystem.InAppMessage)
	 */
	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		this.message = inAppMessage;
	}

	@Override
	public Task getTask() {
		return currentTask;
	}

	@Override
	public void startNewTask() {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask();
	}

	@Override
	public void startNewTask(UserInterface user) {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask(user);
	}

	@Override
	public void stopTask() {
		if (currentTask != null) {
			if (currentTask.isSuccsessfull()) {
				root.addContent(currentTask.stopTask());
				writeHistory();
			} else {
				currentTask.stopUnfinishedTask();
			}
			currentTask = null;
		}
	}

	private boolean readHistory() {
		if (context != null) {
			File file = context.getFileStreamPath(FILENAME);
			if (file == null || !file.exists()) {
				return false;
			}
			try {

//				FileInputStream inputStream = new FileInputStream(file);
//				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//				String string = null;
//				String text = new String();
//				while ((string = reader.readLine()) != null) {
//					text += string; 
//				}
//				reader.close();

				SAXBuilder saxBuilder = new SAXBuilder();
				document = saxBuilder.build(file);
				root = document.getRootElement();

				//				SAXParser saxParser = saxParserFactory.newSAXParser();
				//				HistorySaxHandler handler = new HistorySaxHandler();
				//				context.getFileStreamPath(FILENAME);
				//				saxParser.parse(file, handler);

				//				
				return true;
			} catch (IOException e) {
				fireError(e);
			} catch (JDOMException e) {
				fireError(e);
			} 
		}
		return false;
	}

	//	private JSONObject xmlToJsonObject(Element element) throws ParseException, DataConversionException {
	//		JSONObject jsonObject = new JSONObject();
	//		jsonObject.put(Task.USER,  (JSONObject) jsonParser.parse(element.getChild(Task.USER).getText()));
	//		jsonObject.put(Task.START_POSITION, (JSONObject) jsonParser.parse(element.getChild(Task.START_POSITION).getText()));
	//		jsonObject.put(Task.STOP_POSITION, (JSONObject) jsonParser.parse(element.getChild(Task.STOP_POSITION).getText()));
	//
	//		jsonObject.put(Task.START_TIME, element.getAttribute(Task.START_TIME).getLongValue());
	//		jsonObject.put(Task.STOP_TIME, element.getAttribute(Task.STOP_TIME).getLongValue());
	//
	//		return jsonObject;
	//	}
	//
	//	private Element jsonToXml(JSONObject jsonObject){
	//		Element historyelement = new Element(Task.TASK);
	//
	//		Element element = new Element(Task.USER);
	//		element.setText(jsonObject.get(Task.USER).toString());
	//		historyelement.addContent(element);
	//
	//		element = new Element(Task.START_POSITION);
	//		element.setText(jsonObject.get(Task.START_POSITION).toString());
	//		historyelement.addContent(element);
	//
	//		element = new Element(Task.STOP_POSITION);
	//		element.setText(jsonObject.get(Task.STOP_POSITION).toString());
	//		historyelement.addContent(element);
	//
	//		historyelement.setAttribute(Task.START_TIME, jsonObject.get(Task.START_TIME).toString());
	//		historyelement.setAttribute(Task.STOP_TIME, jsonObject.get(Task.STOP_TIME).toString());
	//		return historyelement;
	//	}

	private boolean writeHistory() {
		if (context != null) {
			try {
				writing = true;

				//				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				//				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
				Writer writer=new FileWriter(context.getFileStreamPath(FILENAME));

				//				Transformer transformer = transformerFactory.newTransformer();

				XMLOutputter xmlOutputter = new XMLOutputter(Format.getCompactFormat());

				// write the content into xml file
				// XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
				xmlOutputter.output(document, writer);

				//				transformer.transform(source, result);


				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);



				//				 
				//				
				//				writer.write(string);
				//
				//				writer.flush();
				//				fos.flush();
				//
				//				writer.close();
				//				fos.close();
				writing = false;
				return true;
			} catch (IOException e) {
				fireError(e);
			} 
		}
		return false;
	}

	@Override
	public Runnable loadHistory(Context applicationContext) {
		setContext(applicationContext);
		return new Runnable() {

			@Override
			public void run() {
				readHistory();
				ArrayList<Element> arrayList = new ArrayList<Element>(root.getChildren());
				fireMessageFromManager(arrayList, InAppMessageType.LOADED);
			}
		};
	}

	@Override
	public void update(Observable observable, Object data) {
		if (currentTask != null) {
			fireMessageFromManager(currentTask, InAppMessageType.TIMEOUT);
		}
	}
}
