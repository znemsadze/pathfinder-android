package gse.pathfinder.ui;

import gse.pathfinder.LoginActivity;
import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public abstract class BaseActivity extends Activity implements ILoggable {
	static final int ERROR_SHORT = 0;
	static final int ERROR_DETAILED = 1;

	static int errorDetailLevel = ERROR_SHORT;

	@Override
	public void warning(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void error(String errorMessage) {
		new AlertDialog.Builder(this).setTitle("შეცდომა").setMessage(errorMessage).setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	public void error(Exception ex) {
		String msg;

		if (errorDetailLevel == ERROR_DETAILED) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			msg = sw.toString();
		} else {
			msg = ex.getMessage();
			if (null == msg || msg.isEmpty()) msg = ex.toString();
		}

		ex.printStackTrace();

		error(msg);
	}

	public boolean validateLogin() {
		if (ApplicationController.isLoggedIn()) return true;
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		return false;
	}

	public User getUser() {
		return ApplicationController.getCurrentUser();
	}

	public SharedPreferences getPreferences() {
		return getSharedPreferences("PathfinderSettings", 0);
	}

	protected void fitBounds(GoogleMap map, LatLngBounds.Builder builder) {
		if (null != builder && null != map) {
			try {
				fitBounds(map, builder.build());
			} catch (Exception ex) {
				Log.d("ERROR", ex.toString());
			}
		}
	}

	protected void fitBounds(GoogleMap map, LatLngBounds mapBounds) {
		if (null != mapBounds && null != map) {
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 50));
		}
	}

	protected Polyline drawPoliline(GoogleMap map, List<Point> points, int color, int width, LatLngBounds.Builder builder) {
		PolylineOptions rectOptions = new PolylineOptions();
		rectOptions.color(color);
		rectOptions.width(width);
		for (Point p : points) {
			rectOptions.add(p.getCoordinate());
			if (null != builder) rectOptions.add(p.getCoordinate());
		}
		return map.addPolyline(rectOptions);
	}

	protected Marker putMarket(GoogleMap map, Point point, int image, String title, LatLngBounds.Builder builder) {
		if (null != builder) builder.include(point.getCoordinate());
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(image);
		return map.addMarker(new MarkerOptions().position(point.getCoordinate()).title(title).icon(icon));
	}
}
