package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.Track;
import gse.pathfinder.models.User;
import gse.pathfinder.models.WithPoint;
import gse.pathfinder.ui.BaseActivity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.PolylineOptions;

public class TaskActivity extends BaseActivity {
	private ImageView imgStatus;
	private TextView txtNumber;
	private TextView txtDate;
	private TextView txtNote;
	private GoogleMap map;
	private LatLngBounds mapBounds;
	private Task task;
	private MenuItem beginItem;
	private MenuItem cancelItem;
	private MenuItem completeItem;
	private ProgressDialog waitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		imgStatus = (ImageView) findViewById(R.id.status_image_task_activity);
		txtNumber = (TextView) findViewById(R.id.number_task_activity);
		txtDate = (TextView) findViewById(R.id.date_task_activity);
		txtNote = (TextView) findViewById(R.id.note_task_activity);
		txtNote.setTextColor(Color.GRAY);
		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				fitBounds();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		task = (Task) getIntent().getExtras().get("task");
		refreshDisplay();
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
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(dest.getImage());
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
	
	protected void refreshMap() {
		map.clear();
		if (null == task) return;

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		putDestinations(map, builder, task);
		drawPaths(map, builder, task);
		drawTracks(map, builder, task);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);

		beginItem = (MenuItem) menu.findItem(R.id.begin_task_action);
		cancelItem = (MenuItem) menu.findItem(R.id.cancel_task_action);
		completeItem = (MenuItem) menu.findItem(R.id.complete_task_action);
		resetActions();

		return true;
	}

	private void resetActions() {
		if (null != task && null != beginItem) {
			beginItem.setVisible(task.canBegin());
			completeItem.setVisible(task.canComplete());
			cancelItem.setVisible(task.canCancel());
		}
	}

	private void initilizeMap() {
		if (map == null) {
			Fragment fragment = getFragmentManager().findFragmentById(R.id.task_map);
			map = ((MapFragment) fragment).getMap();
			map.setMyLocationEnabled(true);
		}
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

	private void changeTaskStatus(int newStatus) {
		task.setStatus(newStatus);
		Cache.updateTask(task);
		refreshDisplay();
	}
}
