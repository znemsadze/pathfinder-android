package gse.pathfinder;

import gse.pathfinder.services.TrackingService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {
	public static boolean isNetworkActive(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public static boolean isTrackingActive(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (TrackingService.class.getName().equals(service.service.getClassName())) return true;
		}
		return false;
	}

	private TextView txtStatus;

	//
	// based on response:
	// http://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
	//
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GpsStatus.Listener gpsListener;
	private long lastLocationMillis;
	private boolean isGPSFix;
	private Location lastLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_status, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		txtStatus = (TextView) getActivity().findViewById(R.id.gps_fragment_status);
		txtStatus.setTextColor(Color.WHITE);
		addGpsListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		resetStatus();
	}

	@Override
	public synchronized void onDestroy() {
		locationManager.removeUpdates(locationListener);
		locationManager.removeGpsStatusListener(gpsListener);
		locationListener = null;
		gpsListener = null;
	}

	private synchronized void addGpsListeners() {
		if (locationManager == null) locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (gpsListener == null) {
			Thread triggerService = new Thread(new Runnable() {
				public void run() {
					try {
						Looper.prepare();
						locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

						locationListener = new MyLocationListener();
						gpsListener = new MyGPSListener();
						locationManager.requestLocationUpdates(TrackingService.PROVIDER, TrackingService.MIN_TIME, TrackingService.MIN_DISTANCE, locationListener);
						locationManager.addGpsStatusListener(gpsListener);

						Looper.loop();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			triggerService.start();
		}
	}

	private void resetStatus() {
		boolean serviceActive = StatusFragment.isTrackingActive(getActivity());
		boolean networkActive = StatusFragment.isNetworkActive(getActivity());
		boolean gpsActive = isGPSFix;

		if (!serviceActive) {
			txtStatus.setBackgroundColor(Color.RED);
			txtStatus.setText("ტრეკინგის სერვისი არ მუშაობს!");
		} else if (!gpsActive) {
			txtStatus.setBackgroundColor(Color.RED);
			txtStatus.setText("სერვისი აქტიურია მაგრამ GPS სერვისი არ მუშაობს!");
		} else if (!networkActive) {
			txtStatus.setBackgroundColor(Color.rgb(100, 100, 0));
			txtStatus.setText("ტრეკინგი აქტიურია მაგრამ ინტერნეტ-კავშირი არ მუშაობს!");
		} else {
			txtStatus.setBackgroundColor(Color.GREEN);
			txtStatus.setText("ყველა სერვისი გამართულია.");
		}
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			try {
				lastLocation = location;
				lastLocationMillis = System.currentTimeMillis();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

	private class MyGPSListener implements GpsStatus.Listener {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (lastLocation != null) isGPSFix = (SystemClock.elapsedRealtime() - lastLocationMillis) < 3000;
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				isGPSFix = true;
				break;
			}
			StatusFragment.this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					resetStatus();
				}
			});
		}
	}
}
