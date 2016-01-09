package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.Track;
import gse.pathfinder.models.User;
import gse.pathfinder.models.WithPoint;
import gse.pathfinder.services.TrackingService;
import gse.pathfinder.sql.TrackUtils;
import gse.pathfinder.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TaskActivity extends BaseActivity {
	private ImageView imgStatus;
	private TextView txtNumber;
	private TextView txtDate;
	private TextView txtNote;
	private Task task;
	private MenuItem beginItem;
	private MenuItem cancelItem;
	private MenuItem completeItem;
	private MenuItem noteItem;
	private ProgressDialog waitDialog;

	private LocationManager lm;
	private GoogleMap map;
	private LatLngBounds mapBounds;
	private Polyline currentTrack;
	private MyLocationListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		imgStatus = (ImageView) findViewById(R.id.status_image_task_activity);
		txtNumber = (TextView) findViewById(R.id.number_task_activity);
		txtDate = (TextView) findViewById(R.id.date_task_activity);
		txtNote = (TextView) findViewById(R.id.note_task_activity);
		txtNote.setTextColor(Color.GRAY);
		initilizeMap();
	}

	@Override
	protected void onStart() {
		super.onStart();
		task = (Task) getIntent().getExtras().get("task");
		refreshDisplay();
		if (listener == null) {
			addLocationListener();
		}
	}

	@Override
	protected synchronized void onStop() {
		super.onStop();
		if (null != listener) {
			lm.removeUpdates(listener);
			listener = null;
		}
	}

	private synchronized void addLocationListener() {
		Thread triggerService = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();
					if (null == lm) {
						lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					}
					listener = new MyLocationListener();
					lm.requestLocationUpdates(TrackingService.PROVIDER, TrackingService.MIN_TIME, TrackingService.MIN_DISTANCE, listener);
					Looper.loop();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, "LocationThread");
		triggerService.start();
	}

	private void refreshDisplay() {
		String numberText = " #" + task.getNumber();
		SpannableString spanString = new SpannableString(numberText);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		txtNumber.setText(spanString);
		imgStatus.setImageResource(task.getStatusImage());
		txtDate.setText(TasksActivity.DATE_FORMAT.format(task.getCreatedAt()));
		txtNote.setText(task.getNote());
		refreshMap();
		resetActions();
	}

	private void putDestinations(GoogleMap map, LatLngBounds.Builder builder, Task task) {
		for (WithPoint dest : task.getDestinations()) {
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(dest.getMarkerImage());
			map.addMarker(new MarkerOptions().position(dest.getPoint().getCoordinate()).title(dest.getName()).icon(icon));
			builder.include(dest.getPoint().getCoordinate());
		}
	}

	private void drawPaths(GoogleMap map, LatLngBounds.Builder builder, Task task) {
		for (Path path : task.getPaths()) {
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.color(Color.GREEN);
			rectOptions.width(10);
			for (Point p : path.getPoints()) {
				builder.include(p.getCoordinate());
				rectOptions.add(p.getCoordinate());
			}
			map.addPolyline(rectOptions);
		}
	}

	private void drawTracks(GoogleMap map, LatLngBounds.Builder builder, Task task) {
		for (Track tracking : task.getTracks()) {
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.color(Color.RED);
			rectOptions.width(5);
			for (Point p : tracking.getPoints()) {
				builder.include(p.getCoordinate());
				rectOptions.add(p.getCoordinate());
			}
			map.addPolyline(rectOptions);
			// if (tracking.isOpen() && !tracking.getPoints().isEmpty()) {
			// 	Point last = tracking.getPoints().get(tracking.getPoints().size() - 1);
			// 	BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.user);
			// 	map.addMarker(new MarkerOptions().position(last.getCoordinate()).icon(icon));
			// }
		}
	}

	private synchronized void drawLastTrack(GoogleMap map, LatLngBounds.Builder builder) {
		if (null != currentTrack) currentTrack.remove();
		if (task.isInProgress()) {
			List<Point> points = new ArrayList<Point>();
			if (null != task.getTracks() && !task.getTracks().isEmpty()) {
				Track t = task.getTracks().get(task.getTracks().size() - 1);
				if (!t.getPoints().isEmpty()) {
					points.add(t.getPoints().get(t.getPoints().size() - 1));
				}
			}
			points.addAll(TrackUtils.getLastTrack(this));
			PolylineOptions options = new PolylineOptions();
			options.color(Color.GRAY);
			options.width(5);
			for (Point p : points) {
				if (null != builder) builder.include(p.getCoordinate());
				options.add(p.getCoordinate());
			}
			currentTrack = map.addPolyline(options);
		}
	}

	protected void refreshMap() {
		map.clear();
		if (task == null) return;

		if (task.getTracks().isEmpty() && task.getDestinations().isEmpty()) return;

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		putDestinations(map, builder, task);
		drawPaths(map, builder, task);
		drawTracks(map, builder, task);
		drawLastTrack(map, builder);

		try {
			mapBounds = builder.build();
		} catch (Exception ex) {}
	}

	protected void fitBounds() {
		if (null != mapBounds) {
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 50));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	private void changeStatus(final String action, final int newStatus) {
		new AlertDialog.Builder(this).setMessage("ნამდვილად გინდათ სტატუსის შეცვლა?").setPositiveButton("კი", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				User user = ApplicationController.getCurrentUser();
				waitDialog = ProgressDialog.show(TaskActivity.this, "სტატუსის შეცვლა", "გთხოვთ დაელოდეთ...");
				new ChangeTaskStatus(action, newStatus).execute(user.getUsername(), user.getPassword(), task.getId());
			}
		}).setNegativeButton("არა", null).show();
	}

	public void onTaskBegin(MenuItem mi) {
		changeStatus("begin", Task.IN_PROGRESS);
	}

	public void onTaskCancel(MenuItem mi) {
		changeStatus("cancel", Task.CANCELED);
	}

	public void onTaskComplete(MenuItem mi) {
		changeStatus("complete", Task.COMPELETED);
	}

	public void onMakeNote(MenuItem mi) {
		Intent intent = new Intent(this, TaskNoteActivity.class);
		intent.putExtra("task", this.task);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);

		beginItem = (MenuItem) menu.findItem(R.id.begin_task_action);
		cancelItem = (MenuItem) menu.findItem(R.id.cancel_task_action);
		completeItem = (MenuItem) menu.findItem(R.id.complete_task_action);
		noteItem = (MenuItem) menu.findItem(R.id.note_task_action);
		resetActions();

		return true;
	}

	private void resetActions() {
		if (null != beginItem) {
			beginItem.setVisible(task != null && task.canBegin());
			completeItem.setVisible(task != null && task.canComplete());
			cancelItem.setVisible(task != null && task.canCancel());
			noteItem.setVisible(task != null && task.isInProgress());
		}
	}

	private void initilizeMap() {
		if (null == map) {
			Fragment fragment = getFragmentManager().findFragmentById(R.id.task_map);
			map = ((MapFragment) fragment).getMap();
			map.setMyLocationEnabled(true);
			map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
				@Override
				public void onMapLoaded() {
					fitBounds();
				}
			});
		}
	}

	private void changeTaskStatus(int newStatus) {
		task.setStatus(newStatus);
		Cache.updateTask(task);
		refreshDisplay();
	}

	private class ChangeTaskStatus extends AsyncTask<String, Void, Void> {
		private Exception exception;
		private String actionPrefix;
		private int newStatus;

		public ChangeTaskStatus(String actionPrefix, int newStatus) {
			this.actionPrefix = actionPrefix;
			this.newStatus = newStatus;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				ApplicationController.changeTaskStatus(TaskActivity.this, params[0], params[1], params[2], actionPrefix);
			} catch (Exception ex) {
				this.exception = ex;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != waitDialog) waitDialog.dismiss();
			if (null != exception) {
				TaskActivity.this.error(exception);
			} else {
				TaskActivity.this.changeTaskStatus(newStatus);
			}
		}
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			try {
				TaskActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						drawLastTrack(map, null);
					}
				});
			} catch (Exception ex) {
				ex.printStackTrace();
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
