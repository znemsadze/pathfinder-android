package gse.pathfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MapDownloadActivity extends Activity {
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

	}
}
