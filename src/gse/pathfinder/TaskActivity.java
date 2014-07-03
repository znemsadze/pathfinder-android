package gse.pathfinder;

import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.WithPoint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class TaskActivity extends Activity {
	private ImageView	imgStatus;
	private TextView	txtNumber;
	private TextView	txtDate;
	private TextView	txtNote;
	private GoogleMap	googleMap;

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
	}

	@Override
	protected void onStart() {
		super.onStart();
		Task task = (Task) getIntent().getExtras().get("task");
		String numberText = " #" + task.getNumber();
		SpannableString spanString = new SpannableString(numberText);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		txtNumber.setText(spanString);
		imgStatus.setImageResource(task.getStatusImage());
		txtDate.setText(TasksActivity.DATE_FORMAT.format(task.getCreatedAt()));
		txtNote.setText(task.getNote());
		for (WithPoint dest : task.getDestinations()) {
			googleMap.addMarker(new MarkerOptions().position(dest.getPoint().getCoordinate()).title(dest.getName()));
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Path path : task.getPaths()) {
			PolylineOptions rectOptions = new PolylineOptions();
			for (Point p : path.getPoints()) {
				builder.include(p.getCoordinate());
				rectOptions.add(p.getCoordinate());
			}
			googleMap.addPolyline(rectOptions);
		}
		final LatLngBounds bounds = builder.build();
		// googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
		googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			Fragment fragment = getFragmentManager().findFragmentById(R.id.task_map);
			googleMap = ((MapFragment) fragment).getMap();
		}
	}
}
