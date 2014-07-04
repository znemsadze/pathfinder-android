package gse.pathfinder.ui;

import gse.pathfinder.LoginActivity;
import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

public abstract class BaseActivity extends Activity implements ILoggable {
	@Override
	public void error(String errorMessage) {
		//Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		new AlertDialog.Builder(this)
			.setTitle("შეცდომა")
			.setMessage(errorMessage)
			.setPositiveButton(android.R.string.ok, null)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
	}

	@Override
	public void error(Exception ex) {
		ex.printStackTrace();
		String msg = ex.getMessage();
		if (null == msg || msg.isEmpty()) msg = ex.toString();
		error(msg);
	}

	public boolean validateLogin() {
		if (ApplicationController.isLoggedIn()) return true;
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		return false;
	}

	public User getUser() {
		return ApplicationController.getCurrentUser();
	}

	public SharedPreferences getPreferences() {
		return getSharedPreferences("PathfinderSettings", 0);
	}
}
