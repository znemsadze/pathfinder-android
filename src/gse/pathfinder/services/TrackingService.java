package gse.pathfinder.services;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Point;
import gse.pathfinder.sql.TrackUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class TrackingService extends Service {
	public static final int MIN_TIME = 1000; // ms
	public static final float MIN_DISTANCE = 10; // meter
	public static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final String TAG = "TRACKING";

	private LocationManager lm;
	private MyLocationListener listener;
	private String userid;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.userid = intent.getExtras().getString("userid");
		if (listener == null) addLocationListener();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(listener);
		listener = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void addLocationListener() {
		Thread triggerService = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();
					lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

					listener = new MyLocationListener();
					lm.requestLocationUpdates(PROVIDER, MIN_TIME, MIN_DISTANCE, listener);

					Looper.loop();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, "LocationThread");
		triggerService.start();
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// Log.d(TAG, userid + ": " + location.getLatitude() + " / " + location.getLongitude());
			try {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				ApplicationController.trackPoint(TrackingService.this, userid, lat, lng);
				TrackUtils.saveLastTrack(TrackingService.this, new Point(lat, lng));
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.e(TAG, ex.toString());
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	}
}
