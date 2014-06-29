package gse.pathfinder;

import gse.pathfinder.services.TrackingService;
import gse.pathfinder.ui.BaseActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrackingActivity extends BaseActivity {
	public static final String	TRACKING_ACTIVE	= "tracking_active";
	private ToggleButton	     tglSetting;
	private TextView	         txtDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking);
		tglSetting = (ToggleButton) findViewById(R.id.setting_tracking);
		txtDescription = (TextView) findViewById(R.id.description_tracking);
		tglSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showHideDescription();
				setTrackingActive(isChecked);
			}
		});
		showHideDescription();
	}

	@Override
	protected void onStart() {
		super.onStart();
		tglSetting.setChecked(isTrackingActive());
	}

	private void showHideDescription() {
		if (tglSetting.isChecked()) {
			txtDescription.setVisibility(View.VISIBLE);
		} else {
			txtDescription.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracking, menu);
		return true;
	}

	public boolean isTrackingActive() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (TrackingService.class.getName().equals(service.service.getClassName())) return true;
		}
		return false;
	}

	public void setTrackingActive(boolean active) {
		Intent intent = new Intent(this, TrackingService.class);
		if (active) {
			startService(intent);
		} else {
			stopService(intent);
		}

	}
}
