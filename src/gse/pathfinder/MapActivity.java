package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;

import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends BaseActivity {
	private GoogleMap map;
	private boolean drawn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		initilizeMap();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!drawn) {
			refresh();
			drawn = true;
		}
	}

	private void initilizeMap() {
		if (null == map) {
			Fragment fragment = getFragmentManager().findFragmentById(R.id.viewer_map);
			map = ((MapFragment) fragment).getMap();
			map.setMyLocationEnabled(true);
		}
	}

	private void refresh() {
		User user = ApplicationController.getCurrentUser();
		new PathsDownload().execute(user.getUsername(), user.getPassword());
	}

	private void displayPaths(List<Path> paths) {
		for (Path path : paths) {
			PolylineOptions rectOptions = new PolylineOptions();
			rectOptions.color(Color.MAGENTA);
			rectOptions.width(2);
			for (Point p : path.getPoints()) {
				rectOptions.add(p.getCoordinate());
			}
			map.addPolyline(rectOptions);
		}
	}

	private class PathsDownload extends AsyncTask<String, Void, List<Path>> {
		private Exception ex;

		@Override
		protected List<Path> doInBackground(String... params) {
			try {
				return ApplicationController.getPaths(MapActivity.this, params[0], params[1]);
			} catch (Exception ex) {
				ex.printStackTrace();
				this.ex = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Path> paths) {
			if (null != paths) displayPaths(paths);
			else error(ex);
		}
	}
}
