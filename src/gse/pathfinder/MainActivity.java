package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	private TextView txtUserinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtUserinfo = (TextView) findViewById(R.id.userinfo_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (validateLogin()) {
			User user = getUser();
			txtUserinfo.setText(user.getUsername() + " (" + user.getFullName() + "); ვერსია: v" + ApplicationController.VERSION);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onLogout(MenuItem item) {
		ApplicationController.logout(this);
		TrackingActivity.stopTracking(this);
		Cache.clearAllCaches();
		validateLogin();
	}

	public void onDownloadMap(MenuItem item) {
		startActivity(new Intent(this, MapDownloadActivity.class));
	}

	public void onTracking(MenuItem item) {
		startActivity(new Intent(this, TrackingActivity.class));
	}

	public void onTasks(View view) {
		startActivity(new Intent(this, TasksActivity.class));
	}

	public void onShowMap(View view) {
		startActivity(new Intent(this, MapActivity.class));
	}
}
