/**
 * 
 */
package com.android.helpme.demo.overlay;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.ImageView;
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
public class MapItemnizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<MapOverlayItem> items;
	private Context context;

	public MapItemnizedOverlay(Drawable drawable) {
		super(boundCenterBottom(drawable));
		items = new ArrayList<MapOverlayItem>();
		populate();
	}

	public MapItemnizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		items = new ArrayList<MapOverlayItem>();
		populate();
	}

	public void addOverlay(MapOverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}
	
	public ArrayList<MapOverlayItem> getItems() {
		return items;
	}
	
	@Override
	protected boolean onTap(int index) {
		MapOverlayItem item = items.get(index);
		if (item != null) {
			buildDialog(item).show();
			return true;
		}
		return false;
	}

	public void removeItem(MapOverlayItem item) {
		items.remove(item);
	}
	
	private Dialog buildDialog(MapOverlayItem item) {
		JSONObject object = item.getJsonObject();
		UserInterface userInterface = new User((JSONObject) object.get(Task.USER));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		Dialog dialog = dialogBuilder.show();
		//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.map_detail_dialog);

		ImageView imageView; TextView text;
		imageView = (ImageView) dialog.findViewById(R.id.map_pic);
		imageView.setImageDrawable(item.getDrawable());
		
		text = (TextView) dialog.findViewById(R.id.map_name);
		text.setText(Html.fromHtml(context.getText(R.string.dialog_name) + userInterface.getName()));
		
		text = (TextView) dialog.findViewById(R.id.history_age);
		text.setText(Html.fromHtml(context.getString(R.string.dialog_age) + new Integer(userInterface.getAge()).toString()));

		text = (TextView) dialog.findViewById(R.id.history_gender);
		text.setText(Html.fromHtml(context.getString(R.string.dialog_gender) + userInterface.getGender() ));

		return dialog;
	}
}
