package gse.pathfinder.services;

import gse.pathfinder.api.ApplicationController;
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
	private static final int	  MIN_TIME	   = 1000;	                       // ms
	private static final float	MIN_DISTANCE	= 10;	                       // sec
	private static final String	PROVIDER	   = LocationManager.GPS_PROVIDER;
	private static final String	TAG	         = "TRACKING";

	private LocationManager	    lm;
	private MyLocationListener	listener;
	private String	            userid;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.userid = intent.getExtras().getString("userid");
		if (listener == null) addLocationListener();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(listener);
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

					// Criteria c = new Criteria();
					// c.setAccuracy(Criteria.ACCURACY_COARSE);

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
			Log.d(TAG, userid + ": " + location.getLatitude() + " / " + location.getLongitude());
			try {
				ApplicationController.trackPoint(userid, location.getLatitude(), location.getLongitude());
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
