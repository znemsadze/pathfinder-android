package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.User;
import gse.pathfinder.models.WithName;
import gse.pathfinder.ui.BaseActivity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;

public class MapActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		User user = ApplicationController.getCurrentUser();
		new ObjectsDownload().execute(user.getUsername(), user.getPassword());
	}

	private class ObjectsDownload extends AsyncTask<String, Void, List<WithName>> {
		private Exception ex;

		@Override
		protected List<WithName> doInBackground(String... params) {
			try {
				return ApplicationController.getObjects(MapActivity.this, params[0], params[1]);
			} catch (Exception ex) {
				ex.printStackTrace();
				this.ex = ex;
				return null;
			}
		}
	}
}
