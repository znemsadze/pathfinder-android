package gse.pathfinder;

import gse.pathfinder.services.TrackingService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
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

	private LocationManager locationManager;
	private GpsStatus.Listener gpsListener;
	private boolean isGPSEnabled;

	// private Location lastLocation;

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
		super.onDestroy();
		try {
			locationManager.removeGpsStatusListener(gpsListener);
		} finally {
			gpsListener = null;
		}
	}

	private synchronized void addGpsListeners() {
		if (locationManager == null) locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (gpsListener == null) {
			Thread triggerService = new Thread(new Runnable() {
				public void run() {
					try {
						Looper.prepare();
						locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

						gpsListener = new MyGPSListener();
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

	static final int COLOR_ERROR = Color.RED;
	static final int COLOR_WARNING = Color.rgb(230, 230, 0);
	static final int COLOR_SUCCESS = Color.rgb(0, 230, 0);

	private synchronized void resetStatus() {
		boolean serviceActive = StatusFragment.isTrackingActive(getActivity());
		boolean networkActive = StatusFragment.isNetworkActive(getActivity());
		boolean gpsActive = isGPSEnabled;

		if (!serviceActive) {
			txtStatus.setBackgroundColor(COLOR_ERROR);
			txtStatus.setText("ტრეკინგი გამორთულია!!!");
		} else if (!gpsActive) {
			txtStatus.setBackgroundColor(COLOR_ERROR);
			txtStatus.setText("ტრეკინგი აქტიურია მაგრამ GPS არ მუშაობს!");
		} else if (!networkActive) {
			txtStatus.setBackgroundColor(COLOR_WARNING);
			txtStatus.setText("ტრეკინგი აქტიურია მაგრამ ინტერნეტ-კავშირი არ მუშაობს!");
		} else {
			txtStatus.setBackgroundColor(COLOR_SUCCESS);
			txtStatus.setText("ყველა სერვისი გამართულია.");
		}
	}

	private class MyGPSListener implements GpsStatus.Listener {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				isGPSEnabled = true;
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				isGPSEnabled = true;
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				isGPSEnabled = false;
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
