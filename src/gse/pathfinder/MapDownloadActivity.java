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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MapDownloadActivity extends BaseActivity {
	static final String LAST_DOWNLOAD = "last_objects_download";
	private ProgressBar progOffices;
	private ProgressBar progSubstations;
	private ProgressBar progTowers;
	private ProgressBar progLines;
	private ProgressBar progPaths;
	private TextView txtOffices;
	private TextView txtSubstations;
	private TextView txtTowers;
	private TextView txtLines;
	private TextView txtPaths;
	private TextView txtLastDownload;
	private boolean downloadInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_download);
		progOffices = (ProgressBar) findViewById(R.id.office_progress);
		progSubstations = (ProgressBar) findViewById(R.id.substation_progress);
		progTowers = (ProgressBar) findViewById(R.id.tower_progress);
		progLines = (ProgressBar) findViewById(R.id.line_progress);
		progPaths = (ProgressBar) findViewById(R.id.path_progress);
		txtOffices = (TextView) findViewById(R.id.office_count);
		txtSubstations = (TextView) findViewById(R.id.substation_count);
		txtTowers = (TextView) findViewById(R.id.tower_count);
		txtLines = (TextView) findViewById(R.id.line_count);
		txtPaths = (TextView) findViewById(R.id.path_count);
		txtLastDownload = (TextView) findViewById(R.id.last_download);
	}

	@Override
	protected void onStart() {
		super.onStart();
		resetLastUpdate();
	}

	public synchronized void onDownload(View view) {
		if (!downloadInProgress) {
			downloadInProgress = true;
			User user = ApplicationController.getCurrentUser();
			new OfficeDownload(txtOffices, progOffices).execute(user.getUsername(), user.getPassword());
			new SubstationDownload(txtSubstations, progSubstations).execute(user.getUsername(), user.getPassword());
			new LineDownload(txtLines, progLines).execute(user.getUsername(), user.getPassword());
			new PathDownload(txtPaths, progPaths).execute(user.getUsername(), user.getPassword());
			new TowerDownload(txtTowers, progTowers).execute(user.getUsername(), user.getPassword());
			view.setEnabled(false);
		}
	}

	@Override
	public synchronized void onBackPressed() {
		if (!downloadInProgress) {
			super.onBackPressed();
		}
	}

	private void resetLastUpdate() {
		String lastDate = getPreferences().getString(LAST_DOWNLOAD, "(არასდროს)");
		txtLastDownload.setText("ბოლო განახლება: " + lastDate);
	}

	private abstract class ObjectDownload<T> extends AsyncTask<String, Integer, Void> {
		private TextView txtCount;
		private ProgressBar progressBar;
		private Exception exception;
		private int count;

		public ObjectDownload(TextView txtCount, ProgressBar progressBar) {
			this.txtCount = txtCount;
			this.progressBar = progressBar;
			progressBar.setVisibility(View.VISIBLE);
			txtCount.setText("იტვირთება...");
		}

		@Override
		protected Void doInBackground(String... params) {
			clearDatabase(MapDownloadActivity.this);
			if (isMultipage()) {
				try {
					int page = 1;
					while (true) {
						List<T> list = download(MapDownloadActivity.this, params[0], params[1], page);
						page += 1;
						count += list.size();
						if (list.isEmpty()) break;
						publishProgress(count);
					}
				} catch (Exception ex) {
					this.exception = ex;
				}
			} else {
				try {
					List<T> list = download(MapDownloadActivity.this, params[0], params[1], 0);
					count += list.size();
				} catch (Exception ex) {
					this.exception = ex;
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			txtCount.setText("ჩამოიტვირთა: " + values[0] + "...");
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.INVISIBLE);
			if (null != exception) {
				// MapDownloadActivity.this.error(exception);
				txtCount.setText(exception.toString());
				txtCount.setTextColor(Color.RED);
			} else {
				txtCount.setText("" + count);
				txtCount.setTextColor(Color.BLACK);
			}
		}

		abstract List<T> download(Context context, String username, String password, int page) throws JSONException, IOException;

		abstract void clearDatabase(Context context);

		boolean isMultipage() {
			return false;
		}
	}

	private class OfficeDownload extends ObjectDownload<Office> {
		public OfficeDownload(TextView txtCount, ProgressBar progressBar) {
			super(txtCount, progressBar);
		}

		@Override
		void clearDatabase(Context context) {
			OfficeUtils.clearOffices(context);
		}

		@Override
		List<Office> download(Context context, String username, String password, int page) throws JSONException, IOException {
			List<Office> offices = ApplicationController.getOffices(context, username, password);
			OfficeUtils.saveOffices(context, offices);
			return offices;
		}
	}

	private class SubstationDownload extends ObjectDownload<Substation> {
		public SubstationDownload(TextView txtCount, ProgressBar progressBar) {
			super(txtCount, progressBar);
		}

		@Override
		void clearDatabase(Context context) {
			SubstationUtils.clearSubstations(context);
		}

		@Override
		List<Substation> download(Context context, String username, String password, int page) throws JSONException, IOException {
			List<Substation> substations = ApplicationController.getSubstations(context, username, password);
			SubstationUtils.saveSubstations(context, substations);
			return substations;
		}
	}

	private class TowerDownload extends ObjectDownload<Tower> {
		public TowerDownload(TextView txtCount, ProgressBar progressBar) {
			super(txtCount, progressBar);
		}

		@Override
		void clearDatabase(Context context) {
			TowerUtils.clearTowers(context);
		}

		@Override
		List<Tower> download(Context context, String username, String password, int page) throws JSONException, IOException {
			List<Tower> towers = ApplicationController.getTowers(context, username, password, page);
			TowerUtils.saveTowers(context, towers);
			return towers;
		}

		@Override
		boolean isMultipage() {
			return true;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Editor editor = MapDownloadActivity.this.getPreferences().edit();
			editor.putString(LAST_DOWNLOAD, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			editor.commit();
			resetLastUpdate();
			downloadInProgress = false;
		}
	}

	private class LineDownload extends ObjectDownload<Line> {
		public LineDownload(TextView txtCount, ProgressBar progressBar) {
			super(txtCount, progressBar);
		}

		@Override
		void clearDatabase(Context context) {
			LineUtils.clearLines(context);
		}

		@Override
		List<Line> download(Context context, String username, String password, int page) throws JSONException, IOException {
			List<Line> lines = ApplicationController.getLines(context, username, password);
			LineUtils.saveLines(context, lines);
			return lines;
		}
	}

	private class PathDownload extends ObjectDownload<Path> {
		public PathDownload(TextView txtCount, ProgressBar progressBar) {
			super(txtCount, progressBar);
		}

		@Override
		void clearDatabase(Context context) {
			PathUtils.clearPaths(context);
		}

		@Override
		List<Path> download(Context context, String username, String password, int page) throws JSONException, IOException {
			List<Path> paths = ApplicationController.getPaths(context, username, password);
			PathUtils.savePaths(context, paths);
			return paths;
		}
	}
}
