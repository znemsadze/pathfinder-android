package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.api.NetworkUtils;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {
	private TextView txtUsername;
	private TextView txtPassword;
	private TextView txtServer;
	private ProgressDialog waitDialog;
	private CheckBox saveLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		txtUsername = (TextView) findViewById(R.id.username_login);
		txtPassword = (TextView) findViewById(R.id.password_login);
		txtServer = (TextView) findViewById(R.id.server_login);
		saveLogin = (CheckBox) findViewById(R.id.savelogin_login);
		saveLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					clearUsernameAndPassword();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		txtServer.setText(NetworkUtils.getCurrenttHost(this));
		SharedPreferences prefs = Preferences.getPreference(this);
		boolean savelogin = prefs.getBoolean("savelogin", true);
		saveLogin.setChecked(savelogin);
		if (savelogin) {
			txtUsername.setText(Crypto.decrypt(prefs.getString("username", null)));
			txtPassword.setText(Crypto.decrypt(prefs.getString("password", null)));
		}
	}

	private void saveUsernameAndPassword(String username, String password) {
		SharedPreferences pref = Preferences.getPreference(this);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("username", Crypto.encrypt(username));
		editor.putString("password", Crypto.encrypt(password));
		editor.putBoolean("savelogin", true);
		editor.commit();
	}

	private void clearUsernameAndPassword() {
		SharedPreferences pref = Preferences.getPreference(this);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove("username");
		editor.remove("password");
		editor.putBoolean("savelogin", false);
		editor.commit();
	}

	public void onLogin(View v) {
		String username = txtUsername.getText().toString();
		if (null == username || username.trim().isEmpty()) {
			warning("ჩაწერეთ მომხმარებლის სახელი");
			txtUsername.requestFocus();
			return;
		}
		String password = txtPassword.getText().toString();
		if (null == password || password.trim().isEmpty()) {
			warning("ჩაწერეთ პაროლი");
			txtPassword.requestFocus();
			return;
		}
		String host = txtServer.getText().toString();
		if (null == host || host.trim().isEmpty()) {
			warning("ჩაწერეთ სერვერი");
			txtServer.requestFocus();
			return;
		}
		waitDialog = ProgressDialog.show(this, "გთხოვთ დაელოდეთ", "სერვერთან დაკავშირება...");
		new LoginTask().execute(username, password, host);
		if (saveLogin.isChecked()) saveUsernameAndPassword(username, password);
	}

	void userLoggedIn(User user) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		TrackingActivity.startTracking(this);
	}

	private class LoginTask extends AsyncTask<String, Void, User> {
		private Exception exception;

		@Override
		protected User doInBackground(String... params) {
			try {
				NetworkUtils.setDefaultHost(LoginActivity.this, params[2]);
				return ApplicationController.login(LoginActivity.this, params[0], params[1]);
			} catch (Exception ex) {
				this.exception = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(User user) {
			if (null != waitDialog) waitDialog.dismiss();
			if (null != user) LoginActivity.this.userLoggedIn(user);
			else LoginActivity.this.error(exception);
		}
	}
}
