package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapActivity extends BaseActivity {
	private GoogleMap map;
	private boolean drawn;
	private LatLngBounds.Builder builder;

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
		builder = new LatLngBounds.Builder();
		new PathsDownload().execute(user.getUsername(), user.getPassword());
		new LinesDownload().execute(user.getUsername(), user.getPassword());
		new OfficesDownload().execute(user.getUsername(), user.getPassword());
		new SubstationsDownload().execute(user.getUsername(), user.getPassword());
	}

	private void displayPaths(List<Path> paths) {
		for (Path path : paths) {
			drawPoliline(map, path.getPoints(), Color.MAGENTA, 2, builder);
		}
		fitBounds(map, builder);
	}

	private void displayLines(List<Line> lines) {
		for (Line line : lines) {
			drawPoliline(map, line.getPoints(), Color.RED, 2, builder);
		}
		fitBounds(map, builder);
	}

	private void displayOffices(List<Office> offices) {
		for (Office office : offices) {
			putMarket(map, office.getPoint(), R.drawable.office, office.getName(), builder);
		}
		fitBounds(map, builder);
	}

	private void displaySubstations(List<Substation> substations) {
		for (Substation substation : substations) {
			putMarket(map, substation.getPoint(), R.drawable.substation, substation.getName(), builder);
		}
		fitBounds(map, builder);
	}

	private abstract class ObjectDownload<T> extends AsyncTask<String, Void, List<T>> {
		private Exception ex;

		abstract List<T> getObjects(Context context, String user, String password) throws JSONException, IOException;

		abstract void displayObjects(List<T> objects);

		@Override
		protected List<T> doInBackground(String... params) {
			try {
				return getObjects(MapActivity.this, params[0], params[1]);
			} catch (Exception ex) {
				ex.printStackTrace();
				this.ex = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<T> objects) {
			if (null != objects) displayObjects(objects);
			else error(ex);
		}
	}

	private class LinesDownload extends ObjectDownload<Line> {
		@Override
		List<Line> getObjects(Context context, String user, String password) throws JSONException, IOException {
			return ApplicationController.getLines(MapActivity.this, user, password);
		}

		@Override
		void displayObjects(List<Line> objects) {
			displayLines(objects);
		}
	}

	private class PathsDownload extends ObjectDownload<Path> {
		@Override
		List<Path> getObjects(Context context, String user, String password) throws JSONException, IOException {
			return ApplicationController.getPaths(MapActivity.this, user, password);
		}

		@Override
		void displayObjects(List<Path> objects) {
			displayPaths(objects);
		}
	}

	private class OfficesDownload extends ObjectDownload<Office> {
		@Override
		List<Office> getObjects(Context context, String username, String password) throws JSONException, IOException {
			return ApplicationController.getOffices(context, username, password);
		}

		@Override
		void displayObjects(List<Office> objects) {
			displayOffices(objects);
		}
	}

	private class SubstationsDownload extends ObjectDownload<Substation> {
		@Override
		List<Substation> getObjects(Context context, String username, String password) throws JSONException, IOException {
			return ApplicationController.getSubstations(context, username, password);
		}

		@Override
		void displayObjects(List<Substation> objects) {
			displaySubstations(objects);
		}
	};
}
