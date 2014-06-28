package gse.pathfinder;

import gse.pathfinder.ui.BaseActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
		tglSetting.setChecked(isTrackingActive());
		tglSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showHideDescription();
			}
		});
		showHideDescription();
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

	@Override
	protected void onStop() {
		super.onStop();
		setTrackingActive(tglSetting.isChecked());
	}

	public boolean isTrackingActive() {
		return getPreferences().getBoolean(TRACKING_ACTIVE, true);
	}

	public void setTrackingActive(boolean active) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putBoolean(TRACKING_ACTIVE, active);
		editor.commit();
	}
}
