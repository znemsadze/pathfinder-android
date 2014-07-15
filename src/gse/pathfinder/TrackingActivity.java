package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.services.TrackingService;
import gse.pathfinder.ui.BaseActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrackingActivity extends BaseActivity {
	public static final String TRACKING_ACTIVE = "tracking_active";
	private ToggleButton tglSetting;
	private TextView txtDescription;

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

	public boolean isTrackingActive() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (TrackingService.class.getName().equals(service.service.getClassName())) return true;
		}
		return false;
	}

	public void setTrackingActive(boolean active) {
		if (active) TrackingActivity.startTracking(this);
		else TrackingActivity.stopTracking(this);
	}

	public static final void startTracking(Context cntx) {
		Intent intent = new Intent(cntx, TrackingService.class);
		intent.putExtra("userid", ApplicationController.getCurrentUser().getId());
		cntx.startService(intent);
	}

	public static final void stopTracking(Context cntx) {
		cntx.stopService(new Intent(cntx, TrackingService.class));
	}
}
