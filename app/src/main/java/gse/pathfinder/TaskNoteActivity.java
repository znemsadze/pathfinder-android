package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.PathDetail;
import gse.pathfinder.models.PathSurface;
import gse.pathfinder.models.PathType;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.User;
import gse.pathfinder.services.TrackingService;
import gse.pathfinder.ui.BaseActivity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TaskNoteActivity extends BaseActivity {
	static List<PathType> PATH_TYPES;
	private static Point lastKnownLocation;

	private Task task;
	private Spinner spnTypes;
	private Spinner spnSurfaces;
	private Spinner spnDetails;
	private EditText txtNote;
	private ProgressDialog waitDialog;
	private TextView txtStatus;
	private Button btnSave;

	private LocationManager locationManager;
	private MyLocationListener locationListenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_note);

		spnTypes = (Spinner) findViewById(R.id.path_type_activity_task_note);
		spnSurfaces = (Spinner) findViewById(R.id.path_surface_activity_task_note);
		spnDetails = (Spinner) findViewById(R.id.path_detail_activity_task_note);
		txtNote = (EditText) findViewById(R.id.note_activity_task_note);
		txtStatus = (TextView) findViewById(R.id.gps_status_activity_task_note);
		btnSave = (Button) findViewById(R.id.save_action_activity_task_note);

		spnTypes.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				resetSurface();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		spnSurfaces.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				resetDetails();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	@Override
	protected synchronized void onStart() {
		super.onStart();

		this.task = (Task) getIntent().getExtras().get("task");

		if (PATH_TYPES == null) {
			new PathTypesDownload().execute();
		} else {
			usePathTypes(PATH_TYPES);
		}
		if (locationListenter == null) addLocationListener();

		resetGPSLocation(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(locationListenter);
		locationListenter = null;
	}

	public void onSave(View view) {
		PathDetail detail = (PathDetail) spnDetails.getSelectedItem();
		Point location = lastKnownLocation;
		String note = txtNote.getText().toString();
		if (detail == null) {
			error("აარჩიეთ საფარის დეტალი");
			return;
		}
		if (location == null) {
			error("თქვენი ადგილმდებარეობა უცნობია: შენიშვნა ვერ გაიგზავნება!");
			return;
		}

		User user = ApplicationController.getCurrentUser();
		waitDialog = ProgressDialog.show(this, "სტატუსის შეცვლა", "გთხოვთ დაელოდეთ...");
		new AddNoteTask(task, note, location, detail).execute(user.getUsername(), user.getPassword());
	}

	private void addLocationListener() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListenter = new MyLocationListener();
		locationManager.requestLocationUpdates(TrackingService.PROVIDER, TrackingService.MIN_TIME, TrackingService.MIN_DISTANCE, locationListenter);
	}

	private void usePathTypes(List<PathType> types) {
		ArrayAdapter<PathType> adapter = new ArrayAdapter<PathType>(this, android.R.layout.simple_expandable_list_item_1, types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnTypes.setAdapter(adapter);
		resetSurface();
	}

	private void resetSurface() {
		PathType type = (PathType) spnTypes.getSelectedItem();
		ArrayAdapter<PathSurface> adapter = new ArrayAdapter<PathSurface>(this, android.R.layout.simple_expandable_list_item_1, type.getSurfaces());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnSurfaces.setAdapter(adapter);
		resetDetails();
	}

	private void resetDetails() {
		PathSurface surface = (PathSurface) spnSurfaces.getSelectedItem();
		ArrayAdapter<PathDetail> adapter = new ArrayAdapter<PathDetail>(this, android.R.layout.simple_expandable_list_item_1, surface.getDetails());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnDetails.setAdapter(adapter);
	}

	private void onNoteComplete() {
		warning("შენიშვნა გაგზავნილია");
		finish();
	}

	private synchronized void resetGPSLocation(Location location) {
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			lastKnownLocation = new Point(lat, lng);
			txtStatus.setText("ადგილმდებარეობა დადგენილია");
			txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_green, 0, 0, 0);
			txtStatus.setTextColor(Color.GREEN);
			btnSave.setEnabled(true);
		} else {
			lastKnownLocation = null;
			txtStatus.setText("თქვენი ადგილმდებარეობა უცნობია");
			txtStatus.setTextColor(Color.RED);
			txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_red, 0, 0, 0);
			btnSave.setEnabled(false);
		}
	}

	private class PathTypesDownload extends AsyncTask<String, Integer, List<PathType>> {
		private Exception ex;

		@Override
		protected List<PathType> doInBackground(String... params) {
			try {
				return ApplicationController.getPathTypes(TaskNoteActivity.this, null, null);
			} catch (Exception ex) {
				this.ex = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<PathType> result) {
			if (result != null) {
				PATH_TYPES = result;
				usePathTypes(result);
			} else {
				error(ex);
			}
		}
	};

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			try {
				resetGPSLocation(location);
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

	private class AddNoteTask extends AsyncTask<String, Integer, Void> {
		private Task task;
		private String note;
		private Point location;
		private PathDetail detail;
		private Exception ex;

		AddNoteTask(Task task, String note, Point location, PathDetail detail) {
			this.task = task;
			this.note = note;
			this.location = location;
			this.detail = detail;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				ApplicationController.addNote(TaskNoteActivity.this, params[0], params[1], task, note, location, detail);
			} catch (Exception ex) {
				this.ex = ex;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != waitDialog) waitDialog.dismiss();
			if (null != ex) error(ex);
			else onNoteComplete();
		}
	}
}
