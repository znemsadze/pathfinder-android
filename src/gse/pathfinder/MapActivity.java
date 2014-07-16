package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.Tower;
import gse.pathfinder.models.User;
import gse.pathfinder.sql.LineUtils;
import gse.pathfinder.sql.OfficeUtils;
import gse.pathfinder.sql.PathUtils;
import gse.pathfinder.sql.SubstationUtils;
import gse.pathfinder.sql.TowerUtils;
import gse.pathfinder.ui.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

public class MapActivity extends BaseActivity {
	static final float MIN_ZOOM = 12;

	static final String FILTER_OFFICE = "filter_office";
	static final String FILTER_SUBSTATION = "filter_substation";
	static final String FILTER_TOWER = "filter_tower";
	static final String FILTER_PATH = "filter_path";
	static final String FILTER_LINE = "filter_line";

	static final String DOWNLOADED_OFFICE = "downloaded_office";
	static final String DOWNLOADED_SUBSTATION = "downloaded_substation";
	static final String DOWNLOADED_TOWER = "downloaded_tower";
	static final String DOWNLOADED_PATH = "downloaded_path";
	static final String DOWNLOADED_LINE = "downloaded_line";

	private GoogleMap map;
	private boolean drawn;
	private LatLngBounds.Builder builder;

	private View filterLayout;
	private CheckBox chkOffice;
	private CheckBox chkSubstation;
	private CheckBox chkTower;
	private CheckBox chkPath;
	private CheckBox chkLine;

	private List<Polyline> pathLayer = new ArrayList<Polyline>();
	private List<Polyline> lineLayer = new ArrayList<Polyline>();
	private List<Marker> officeLayer = new ArrayList<Marker>();
	private List<Marker> substationLayer = new ArrayList<Marker>();
	private List<Marker> towerLayer = new ArrayList<Marker>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		filterLayout = findViewById(R.id.filters_layout_activity_map);
		filterLayout.setVisibility(View.INVISIBLE);

		chkOffice = (CheckBox) findViewById(R.id.office_checkbox_activity_map);
		chkSubstation = (CheckBox) findViewById(R.id.substation_checkbox_activity_map);
		chkTower = (CheckBox) findViewById(R.id.tower_checkbox_activity_map);
		chkPath = (CheckBox) findViewById(R.id.path_checkbox_activity_map);
		chkLine = (CheckBox) findViewById(R.id.line_checkbox_activity_map);

		initilizeMap();
	}

	public boolean isOfficeVisible() {
		return getPreferences().getBoolean(FILTER_OFFICE, true);
	}

	public boolean isOfficeDownloaded() {
		return getPreferences().getBoolean(DOWNLOADED_OFFICE, false);
	}

	public boolean isSubstationVisible() {
		return getPreferences().getBoolean(FILTER_SUBSTATION, true);
	}

	public boolean isSubstationDownloaded() {
		return getPreferences().getBoolean(DOWNLOADED_SUBSTATION, false);
	}

	public boolean isTowerVisible() {
		return getPreferences().getBoolean(FILTER_TOWER, true);
	}

	public boolean isTowerDownloaded() {
		return getPreferences().getBoolean(DOWNLOADED_TOWER, false);
	}

	public boolean isPathVisible() {
		return getPreferences().getBoolean(FILTER_PATH, true);
	}

	public boolean isPathDownloaded() {
		return getPreferences().getBoolean(DOWNLOADED_PATH, false);
	}

	public boolean isLineVisible() {
		return getPreferences().getBoolean(FILTER_LINE, true);
	}

	public boolean isLineDownloaded() {
		return getPreferences().getBoolean(DOWNLOADED_LINE, false);
	}

	@Override
	protected void onStart() {
		super.onStart();

		chkOffice.setChecked(isOfficeVisible());
		chkSubstation.setChecked(isSubstationVisible());
		chkTower.setChecked(isTowerVisible());
		chkPath.setChecked(isPathVisible());
		chkLine.setChecked(isLineVisible());

		if (!drawn) {
			refresh(false);
			drawn = true;
		}
	}

	public void onFilterChanged(View view) {
		String prefName = null;
		switch (view.getId()) {
		case R.id.office_checkbox_activity_map:
			prefName = FILTER_OFFICE;
			break;
		case R.id.substation_checkbox_activity_map:
			prefName = FILTER_SUBSTATION;
			break;
		case R.id.tower_checkbox_activity_map:
			prefName = FILTER_TOWER;
			break;
		case R.id.path_checkbox_activity_map:
			prefName = FILTER_PATH;
			break;
		case R.id.line_checkbox_activity_map:
			prefName = FILTER_LINE;
			break;
		}
		if (null != prefName) {
			CheckBox checkBox = (CheckBox) view;
			SharedPreferences.Editor editor = getPreferences().edit();
			editor.putBoolean(prefName, checkBox.isChecked());
			editor.commit();
		}
		switch (view.getId()) {
		case R.id.office_checkbox_activity_map:
			resetOffices();
			break;
		case R.id.substation_checkbox_activity_map:
			resetSubstations();
			break;
		case R.id.tower_checkbox_activity_map:
			resetTowers();
			break;
		case R.id.path_checkbox_activity_map:
			resetPaths();
			break;
		case R.id.line_checkbox_activity_map:
			resetLines();
			break;
		}
	}

	public void onShowFilter(MenuItem item) {
		filterLayout.setVisibility(View.VISIBLE);
	}

	public void onHideFilter(View view) {
		filterLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	private void initilizeMap() {
		if (null == map) {
			Fragment fragment = getFragmentManager().findFragmentById(R.id.viewer_map);
			map = ((MapFragment) fragment).getMap();
			map.setMyLocationEnabled(true);
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition position) {
					displayTowers(getDisplayableTowers());
				}
			});
		}
	}

	public void onRefresh(MenuItem item) {
		// refresh(true);
	}

	private void refresh(boolean eager) {
		map.clear();
		User user = ApplicationController.getCurrentUser();
		builder = new LatLngBounds.Builder();

		new PathsDownload(eager || !isPathDownloaded()).execute(user.getUsername(), user.getPassword());
		new LinesDownload(eager || !isLineDownloaded()).execute(user.getUsername(), user.getPassword());
		new OfficesDownload(eager || !isOfficeDownloaded()).execute(user.getUsername(), user.getPassword());
		new SubstationsDownload(eager || !isSubstationDownloaded()).execute(user.getUsername(), user.getPassword());
		new TowersDownload(eager || !isTowerDownloaded()).execute(user.getUsername(), user.getPassword());
	}

	private void displayPaths(List<Path> paths) {
		pathLayer.clear();
		boolean visible = isPathVisible();
		for (Path path : paths) {
			Polyline poly = drawPoliline(map, path.getPoints(), Color.MAGENTA, 2, builder);
			poly.setVisible(visible);
			pathLayer.add(poly);
		}
		fitBounds(map, builder);
	}

	private void displayLines(List<Line> lines) {
		lineLayer.clear();
		boolean visible = isLineVisible();
		for (Line line : lines) {
			Polyline poly = drawPoliline(map, line.getPoints(), Color.RED, 2, builder);
			poly.setVisible(visible);
			lineLayer.add(poly);
		}
		fitBounds(map, builder);
	}

	private void displayOffices(List<Office> offices) {
		officeLayer.clear();
		boolean visible = isOfficeVisible();
		for (Office office : offices) {
			Marker marker = putMarket(map, office.getPoint(), R.drawable.office, office.getName(), builder);
			marker.setVisible(visible);
			officeLayer.add(marker);
		}
		fitBounds(map, builder);
	}

	private void displaySubstations(List<Substation> substations) {
		substationLayer.clear();
		boolean visible = isSubstationVisible();
		for (Substation substation : substations) {
			Marker marker = putMarket(map, substation.getPoint(), R.drawable.substation, substation.getName(), builder);
			marker.setVisible(visible);
			substationLayer.add(marker);
		}
		fitBounds(map, builder);
	}

	private void displayTowers(List<Tower> towers) {
		// remove old towers
		for (Marker marker : towerLayer)
			marker.remove();
		towerLayer.clear();

		// place new towers
		boolean visible = isTowerVisible();
		for (Tower tower : towers) {
			Marker marker = putMarket(map, tower.getPoint(), R.drawable.tower, tower.getName(), null);
			marker.setVisible(visible);
			towerLayer.add(marker);
		}
	}

	private void resetPaths() {
		boolean visible = isPathVisible();
		for (Polyline path : pathLayer) {
			path.setVisible(visible);
		}
	}

	private void resetLines() {
		boolean visible = isLineVisible();
		for (Polyline path : lineLayer) {
			path.setVisible(visible);
		}
	}

	private void resetOffices() {
		boolean visible = isOfficeVisible();
		for (Marker marker : officeLayer) {
			marker.setVisible(visible);
		}
	}

	private void resetSubstations() {
		boolean visible = isSubstationVisible();
		for (Marker marker : substationLayer) {
			marker.setVisible(visible);
		}
	}

	private void resetTowers() {
		boolean visible = isTowerVisible();
		for (Marker marker : towerLayer) {
			marker.setVisible(visible);
		}
	}

	private abstract class ObjectDownload<T> extends AsyncTask<String, Void, List<T>> {
		private Exception ex;
		private boolean eager;

		ObjectDownload(boolean eager) {
			this.eager = eager;
		}

		abstract List<T> downloadObjects(Context context, String user, String password) throws JSONException, IOException;

		abstract List<T> getObjectsFromDb(Context context) throws JSONException, IOException;

		abstract String downloadSettingName();

		abstract void displayObjects(List<T> objects);

		public boolean isEager() {
			return eager;
		}

		@Override
		protected List<T> doInBackground(String... params) {
			try {
				List<T> objects = null;
				if (!isEager()) {
					objects = getObjectsFromDb(MapActivity.this);
				} else {
					SharedPreferences.Editor editor = getPreferences().edit();
					editor.putBoolean(downloadSettingName(), true);
					editor.commit();
					objects = downloadObjects(MapActivity.this, params[0], params[1]);
				}
				return objects;
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
		LinesDownload(boolean eager) {
			super(eager);
		}

		@Override
		List<Line> downloadObjects(Context context, String user, String password) throws JSONException, IOException {
			List<Line> lines = ApplicationController.getLines(MapActivity.this, user, password);
			LineUtils.clearLines(context);
			LineUtils.saveLines(context, lines);
			return lines;
		}

		@Override
		void displayObjects(List<Line> objects) {
			displayLines(objects);
		}

		@Override
		List<Line> getObjectsFromDb(Context context) throws JSONException, IOException {
			return LineUtils.getLines(context);
		}

		@Override
		String downloadSettingName() {
			return DOWNLOADED_LINE;
		}
	}

	private class PathsDownload extends ObjectDownload<Path> {
		PathsDownload(boolean eager) {
			super(eager);
		}

		@Override
		List<Path> downloadObjects(Context context, String user, String password) throws JSONException, IOException {
			List<Path> paths = ApplicationController.getPaths(MapActivity.this, user, password);
			PathUtils.clearPaths(context);
			PathUtils.savePaths(context, paths);
			return paths;
		}

		@Override
		void displayObjects(List<Path> objects) {
			displayPaths(objects);
		}

		@Override
		List<Path> getObjectsFromDb(Context context) throws JSONException, IOException {
			return PathUtils.getPaths(context);
		}

		@Override
		String downloadSettingName() {
			return DOWNLOADED_PATH;
		}
	}

	private class OfficesDownload extends ObjectDownload<Office> {
		OfficesDownload(boolean eager) {
			super(eager);
		}

		@Override
		List<Office> downloadObjects(Context context, String username, String password) throws JSONException, IOException {
			List<Office> offices = ApplicationController.getOffices(context, username, password);
			OfficeUtils.clearOffices(context);
			OfficeUtils.saveOffices(context, offices);
			return offices;
		}

		@Override
		void displayObjects(List<Office> objects) {
			displayOffices(objects);
		}

		@Override
		List<Office> getObjectsFromDb(Context context) throws JSONException, IOException {
			return OfficeUtils.getOffices(context);
		}

		@Override
		String downloadSettingName() {
			return DOWNLOADED_OFFICE;
		}
	}

	private class SubstationsDownload extends ObjectDownload<Substation> {
		SubstationsDownload(boolean eager) {
			super(eager);
		}

		@Override
		List<Substation> downloadObjects(Context context, String username, String password) throws JSONException, IOException {
			List<Substation> substations = ApplicationController.getSubstations(context, username, password);
			SubstationUtils.clearSubstations(context);
			SubstationUtils.saveSubstations(context, substations);
			return substations;
		}

		@Override
		void displayObjects(List<Substation> objects) {
			displaySubstations(objects);
		}

		@Override
		List<Substation> getObjectsFromDb(Context context) throws JSONException, IOException {
			return SubstationUtils.getSubstations(context);
		}

		@Override
		String downloadSettingName() {
			return DOWNLOADED_SUBSTATION;
		}
	};

	private class TowersDownload extends ObjectDownload<Tower> {
		TowersDownload(boolean eager) {
			super(eager);
		}

		@Override
		List<Tower> downloadObjects(Context context, String username, String password) throws JSONException, IOException {
			int page = 0;
			TowerUtils.clearTowers(context);
			while (true) {
				List<Tower> towers = ApplicationController.getTowers(context, username, password, ++page);
				Log.d("MAP", page + ": " + towers.size());
				if (!towers.isEmpty()) {
					TowerUtils.saveTowers(context, towers);
				} else {
					break;
				}
			}
			return getObjectsFromDb(context);
		}

		@Override
		List<Tower> getObjectsFromDb(Context context) throws JSONException, IOException {
			return getDisplayableTowers();
		}

		@Override
		void displayObjects(List<Tower> towers) {
			displayTowers(towers);
		}

		@Override
		String downloadSettingName() {
			return DOWNLOADED_TOWER;
		}
	}

	private List<Tower> getDisplayableTowers() {
		float zoom = map.getCameraPosition().zoom;
		if (zoom > MIN_ZOOM) {
			LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
			return TowerUtils.getTowers(this, bounds);
		} else {
			return new ArrayList<Tower>();
		}
	}
}
