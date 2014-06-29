package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {
	private TextView	txtUsername;
	private TextView	txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		txtUsername = (TextView) findViewById(R.id.username_login);
		txtPassword = (TextView) findViewById(R.id.password_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void onLogin(View v) {
		String username = txtUsername.getText().toString();
		if (null == username || username.trim().isEmpty()) {
			error("ჩაწერეთ მომხმარებლის სახელი");
			txtUsername.requestFocus();
			return;
		}
		String password = txtPassword.getText().toString();
		if (null == password || password.trim().isEmpty()) {
			error("ჩაწერეთ პაროლი");
			txtPassword.requestFocus();
			return;
		}
		new LoginTask().execute(username, password);
	}

	void userLoggedIn(User user) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		TrackingActivity.startTracking(this);
	}

	private class LoginTask extends AsyncTask<String, Void, User> {
		private Exception	exception;

		@Override
		protected User doInBackground(String... params) {
			try {
				return ApplicationController.login(params[0], params[1]);
			} catch (Exception ex) {
				this.exception = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(User user) {
			if (null != user) LoginActivity.this.userLoggedIn(user);
			else LoginActivity.this.error(exception);
		}
	}
}
