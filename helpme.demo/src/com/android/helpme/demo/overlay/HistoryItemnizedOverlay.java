/**
 * 
 */
package com.android.helpme.demo.overlay;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jdom2.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
		Element object = item.getElement();
		UserInterface userInterface = new User(object.getChild(Task.USER));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		Dialog dialog = dialogBuilder.show();
		//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.history_detail_dialog);

		TextView text;

		text = (TextView) dialog.findViewById(R.id.history_name);
		text.setText(Html.fromHtml(context.getText(R.string.dialog_name) + userInterface.getName()));

		text = (TextView) dialog.findViewById(R.id.history_date);
		Date date = new Date( new Long(object.getAttributeValue(Task.START_TIME)));
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		text.setText(Html.fromHtml(context.getText(R.string.dialog_date) + dateFormat.format(date)));


		text = (TextView) dialog.findViewById(R.id.history_age);
		text.setText(Html.fromHtml(context.getString(R.string.dialog_age) + new Integer(userInterface.getAge()).toString()));

		text = (TextView) dialog.findViewById(R.id.history_gender);
		String gender = userInterface.getGender();
		if (gender.equalsIgnoreCase("female")) {
			text.setText(Html.fromHtml(context.getString(R.string.dialog_gender) + context.getString(R.string.female)));
		}
		else
		{
			text.setText(Html.fromHtml(context.getString(R.string.dialog_gender) + context.getString(R.string.male)));
		}
		

		text = (TextView) dialog.findViewById(R.id.history_time);
		Long stoptime = new Long (object.getAttributeValue(Task.STOP_TIME));
		Long starttime = date.getTime();

		long diff = stoptime - starttime;
		long dsecs = (diff / 1000) % (60 * 1000);
		long dminutes = diff / (60 * 1000);

		text.setText(Html.fromHtml(context.getString(R.string.dialog_time) + "" +dminutes + "min. " +dsecs +"sec."));

		return dialog;
	}

	public void removeItem(OverlayItem item) {
		items.remove(item);
	}
}



