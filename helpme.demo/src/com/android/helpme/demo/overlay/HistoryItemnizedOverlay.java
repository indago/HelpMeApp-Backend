/**
 * 
 */
package com.android.helpme.demo.overlay;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import com.android.helpme.demo.R;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.User;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class HistoryItemnizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<HistoryOverlayItem> items;
	private Context context;

	public HistoryItemnizedOverlay(Drawable drawable) {
		super(boundCenterBottom(drawable));
		items = new ArrayList<HistoryOverlayItem>();
		populate();
	}

	public HistoryItemnizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		items = new ArrayList<HistoryOverlayItem>();
		populate();
	}

	public void addOverlay(HistoryOverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected HistoryOverlayItem createItem(int i) {
		return items.get(i);
	}
	
	@Override
	public int size() {
		return items.size();
	}

	public ArrayList<HistoryOverlayItem> getItems() {
		return items;
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {
//		HistoryOverlayItem item = items.get(0);
//		if (item != null) {
//			buildDialog(item).show();
//			return true;
//
//		}
//		return false;
//	}

//	@Override
//	public boolean onTap(GeoPoint arg0, MapView arg1) {
//		return false;
//	}

	@Override
	protected boolean onTap(int index) {
		HistoryOverlayItem item = items.get(index);
		if (item != null) {
			buildDialog(item).show();
			return true;

		}
		return false;
	}

	private Dialog buildDialog(HistoryOverlayItem item){
		JSONObject object = item.getJsonObject();
		UserInterface userInterface = new User((JSONObject) object.get(Task.USER));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		Dialog dialog = dialogBuilder.show();
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog);
		TextView text; String string;

		text = (TextView) dialog.findViewById(R.id.tv_help_ee_name);
		string = context.getString(R.string.dialog_name);
		string = string.replace("[name]", userInterface.getName());
		string = string.replace("[font]", "<font color=\"#00a9e8\">");
		string = string.replace("[/font]", "</font>");
		text.setText(Html.fromHtml(string));

		text = (TextView) dialog.findViewById(R.id.tv_help_ee_date);
		string = context.getString(R.string.dialog_date);
		Date date = new Date((Long) object.get(Task.START_TIME));
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		string = string.replace("[date]", dateFormat.format(date));
		string = string.replace("[font]", "<font color=\"#00a9e8\">");
		string = string.replace("[/font]", "</font>");
		text.setText(Html.fromHtml(string));


		text = (TextView) dialog.findViewById(R.id.tv_help_ee_age);
		string = context.getString(R.string.dialog_age);
		string = string.replace("[age]", new Integer(userInterface.getAge()).toString());
		string = string.replace("[font]", "<font color=\"#00a9e8\">");
		string = string.replace("[/font]", "</font>");
		text.setText(Html.fromHtml(string));

		text = (TextView) dialog.findViewById(R.id.tv_help_ee_gender);
		string = context.getString(R.string.dialog_gender);
		string = string.replace("[gender]", (userInterface.getGender()));
		string = string.replace("[font]", "<font color=\"#00a9e8\">");
		string = string.replace("[/font]", "</font>");
		text.setText(Html.fromHtml(string));

		text = (TextView) dialog.findViewById(R.id.tv_help_ee_time);
		string = context.getString(R.string.dialog_time);
		Long stoptime = (Long) object.get(Task.STOP_TIME);
		Long starttime = date.getTime();

		long diff = stoptime - starttime;
		long dsecs = (diff / 1000) % (60 * 1000);
		long dminutes = diff / (60 * 1000);
		
		string = string.replace("[time]", "" +dminutes + "min. " +dsecs +"sec.");
		string = string.replace("[font]", "<font color=\"#00a9e8\">");
		string = string.replace("[/font]", "</font>");
		text.setText(Html.fromHtml(string));

		return dialog;
	}

	public void removeItem(OverlayItem item) {
		items.remove(item);
	}
}



