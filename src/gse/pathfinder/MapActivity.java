package gse.pathfinder;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.Tower;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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

	static final String MAP_TYPE = "map_type";

	private GoogleMap map;
	private boolean drawn;
	private boolean cameraChanged;
	private LatLngBounds.Builder builder;

	private View filterLayout;
	private CheckBox chkOffice;
	private CheckBox chkSubstation;
	private CheckBox chkTower;
	private CheckBox chkPath;
	private CheckBox chkLine;

	private RadioButton radTypeNormal;
	private RadioButton radTypeSatellite;
	private RadioButton radTypeHybrid;

	private List<Polyline> pathLayer = new ArrayList<Polyline>();
	private List<Polyline> lineLayer = new ArrayList<Polyline>();
	private List<Marker> officeLayer = new ArrayList<Marker>();
	private List<Marker> substationLayer = new ArrayList<Marker>();
	private List<Marker> towerLayer = new ArrayList<Marker>();
	private List<Tower> towers;

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

		radTypeNormal = (RadioButton) findViewById(R.id.normal_type_activity_map);
		radTypeSatellite = (RadioButton) findViewById(R.id.satellite_type_activity_map);
		radTypeHybrid = (RadioButton) findViewById(R.id.hybrid_type_activity_map);

		initilizeMap();
	}

	public boolean isOfficeVisible() {
		return getPreferences().getBoolean(FILTER_OFFICE, true);
	}

	public boolean isSubstationVisible() {
		return getPreferences().getBoolean(FILTER_SUBSTATION, true);
	}

	public boolean isTowerVisible() {
		return getPreferences().getBoolean(FILTER_TOWER, true);
	}

	public boolean isPathVisible() {
		return getPreferences().getBoolean(FILTER_PATH, true);
	}

	public boolean isLineVisible() {
		return getPreferences().getBoolean(FILTER_LINE, true);
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
			getObjectsFromDb();
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

	public void onMapTypeChanged(View view) {
		int mapType = GoogleMap.MAP_TYPE_NORMAL;
		switch (view.getId()) {
		case R.id.normal_type_activity_map:
			mapType = GoogleMap.MAP_TYPE_NORMAL;
			break;
		case R.id.satellite_type_activity_map:
			mapType = GoogleMap.MAP_TYPE_SATELLITE;
			break;
		case R.id.hybrid_type_activity_map:
			mapType = GoogleMap.MAP_TYPE_HYBRID;
			break;
		}
		map.setMapType(mapType);
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt(MAP_TYPE, map.getMapType());
		editor.commit();
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
					cameraChanged = true;
					displayTowers(getDisplayableTowers());
				}
			});
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					int index = towerLayer.indexOf(marker);
					if (index != -1) {
						showTowerInfo(towers.get(index));
					}
					return true;
				}
			});
			initMapType();
		}
	}

	private void initMapType() {
		SharedPreferences pref = getPreferences();
		int mapType = pref.getInt(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
		switch (mapType) {
		case GoogleMap.MAP_TYPE_NORMAL:
			radTypeNormal.setChecked(true);
			break;
		case GoogleMap.MAP_TYPE_SATELLITE:
			radTypeSatellite.setChecked(true);
			break;
		case GoogleMap.MAP_TYPE_HYBRID:
			radTypeHybrid.setChecked(true);
			break;
		}
		map.setMapType(mapType);
	}

	private void getObjectsFromDb() {
		map.clear();
		builder = new LatLngBounds.Builder();
		new GetOffices().execute();
		new GetSubstations().execute();
		new GetLines().execute();
		new GetPaths().execute();
	}

	private void displayPaths(List<Path> paths) {
		pathLayer.clear();
		boolean visible = isPathVisible();
		for (Path path : paths) {
			Polyline poly = drawPoliline(map, path.getPoints(), Color.MAGENTA, 2, builder);
			poly.setVisible(visible);
			pathLayer.add(poly);
		}
		if (!cameraChanged) fitBounds(map, builder);
	}

	private void displayLines(List<Line> lines) {
		lineLayer.clear();
		boolean visible = isLineVisible();
		for (Line line : lines) {
			Polyline poly = drawPoliline(map, line.getPoints(), Color.RED, 2, builder);
			poly.setVisible(visible);
			lineLayer.add(poly);
		}
		if (!cameraChanged) fitBounds(map, builder);
	}

	private void displayOffices(List<Office> offices) {
		officeLayer.clear();
		boolean visible = isOfficeVisible();
		for (Office office : offices) {
			Marker marker = putMarket(map, office.getPoint(), R.drawable.office, office.getName(), builder);
			marker.setVisible(visible);
			officeLayer.add(marker);
		}
		if (!cameraChanged) fitBounds(map, builder);
	}

	private void displaySubstations(List<Substation> substations) {
		substationLayer.clear();
		boolean visible = isSubstationVisible();
		for (Substation substation : substations) {
			Marker marker = putMarket(map, substation.getPoint(), R.drawable.substation, substation.getName(), builder);
			marker.setVisible(visible);
			substationLayer.add(marker);
		}
		if (!cameraChanged) fitBounds(map, builder);
	}

	private void displayTowers(List<Tower> towers) {
		// remove old towers
		for (Marker marker : towerLayer)
			marker.remove();
		this.towerLayer.clear();
		this.towers = towers;

		// place new towers
		boolean visible = isTowerVisible();
		for (Tower tower : towers) {
			Marker marker = putMarket(map, tower.getPoint(), R.drawable.tower, tower.getName(), null);
			marker.setVisible(visible);
			this.towerLayer.add(marker);
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

	private abstract class GetObjects<T> extends AsyncTask<String, Void, List<T>> {
		private Exception ex;

		abstract List<T> getObjectsFromDb(Context context) throws JSONException, IOException;

		abstract void displayObjects(List<T> objects);

		@Override
		protected List<T> doInBackground(String... params) {
			try {
				return getObjectsFromDb(MapActivity.this);
			} catch (Exception ex) {
				ex.printStackTrace();
				this.ex = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<T> objects) {
			if (null != objects) {
				displayObjects(objects);
			} else {
				error(ex);
			}
		}
	}

	private class GetLines extends GetObjects<Line> {
		@Override
		void displayObjects(List<Line> objects) {
			displayLines(objects);
		}

		@Override
		List<Line> getObjectsFromDb(Context context) throws JSONException, IOException {
			return LineUtils.getLines(context);
		}
	}

	private class GetPaths extends GetObjects<Path> {
		@Override
		void displayObjects(List<Path> objects) {
			displayPaths(objects);
		}

		@Override
		List<Path> getObjectsFromDb(Context context) throws JSONException, IOException {
			return PathUtils.getPaths(context);
		}
	}

	private class GetOffices extends GetObjects<Office> {
		@Override
		void displayObjects(List<Office> objects) {
			displayOffices(objects);
		}

		@Override
		List<Office> getObjectsFromDb(Context context) throws JSONException, IOException {
			return OfficeUtils.getOffices(context);
		}
	}

	private class GetSubstations extends GetObjects<Substation> {
		@Override
		void displayObjects(List<Substation> objects) {
			displaySubstations(objects);
		}

		@Override
		List<Substation> getObjectsFromDb(Context context) throws JSONException, IOException {
			return SubstationUtils.getSubstations(context);
		}
	};

	private List<Tower> getDisplayableTowers() {
		float zoom = map.getCameraPosition().zoom;
		if (zoom > MIN_ZOOM) {
			LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
			return TowerUtils.getTowers(this, bounds);
		} else {
			return new ArrayList<Tower>();
		}
	}

	private void showTowerInfo(Tower tower) {
		TowerDialog dialog = new TowerDialog(tower);
		dialog.show(getFragmentManager(), "tower");
	}
}
