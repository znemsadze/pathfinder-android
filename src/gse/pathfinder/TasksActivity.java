package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class TasksActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// XXX:
		User user = ApplicationController.getCurrentUser();
		new TasksDownload().execute(user.getUsername(), user.getPassword(), "1");
	}

	public void displayTasks(List<Task> tasks) {
		// TODO:
		Log.d("TASKS", String.valueOf(tasks.size()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tasks, menu);
		return true;
	}

	private class TasksDownload extends AsyncTask<String, Void, List<Task>> {
		private Exception	exception;

		@Override
		protected List<Task> doInBackground(String... params) {
			try {
				return ApplicationController.getTasks(params[0], params[1], params[2]);
			} catch (Exception ex) {
				this.exception = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Task> tasks) {
			if (null != tasks) TasksActivity.this.displayTasks(tasks);
			else TasksActivity.this.error(exception);
		}
	}
}
