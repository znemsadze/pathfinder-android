package gse.pathfinder.ui;

import gse.pathfinder.LoginActivity;
import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.User;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements ILoggable {
	@Override
	public void error(String errorMessage) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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
}
