package gse.pathfinder;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class TasksActivity extends BaseActivity {
	@SuppressLint("SimpleDateFormat")
	static final SimpleDateFormat	DATE_FORMAT	= new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	private ListView	            listView;
	private ProgressDialog	      waitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);

		listView = (ListView) findViewById(R.id.tasksListView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Task task = (Task) listView.getAdapter().getItem(position);
				Intent intent = new Intent(TasksActivity.this, TaskActivity.class);
				intent.putExtra("task", task);
				TasksActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		User user = ApplicationController.getCurrentUser();
		waitDialog = ProgressDialog.show(this, "გთხოვთ დაელოდეთ", "სერვერთან დაკავშირება...");
		new TasksDownload().execute(user.getUsername(), user.getPassword(), "1");
	}

	public void displayTasks(List<Task> tasks) {
		listView.setAdapter(new TaskListAdapter(this, tasks));
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
				return ApplicationController.getTasks(TasksActivity.this, params[0], params[1], params[2]);
			} catch (Exception ex) {
				this.exception = ex;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Task> tasks) {
			if (null != waitDialog) waitDialog.dismiss();
			if (null != tasks) TasksActivity.this.displayTasks(tasks);
			else TasksActivity.this.error(exception);
		}
	}

	private class TaskListAdapter extends ArrayAdapter<Task> {
		private TextView	txtNumber;
		private TextView	txtDate;
		private ImageView	imgStatus;
		private TextView	txtNote;

		public TaskListAdapter(Context context, List<Task> objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Task task = getItem(position);

			if (convertView == null) {
				LinearLayout row1 = new LinearLayout(getContext());
				row1.setOrientation(LinearLayout.HORIZONTAL);
				txtNumber = new TextView(getContext());
				txtNumber.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 100));
				txtDate = new TextView(getContext());
				imgStatus = new ImageView(getContext());
				LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				imgStatus.setLayoutParams(p);
				row1.addView(imgStatus);
				row1.addView(txtNumber);
				row1.addView(txtDate);

				LinearLayout row2 = new LinearLayout(getContext());
				row2.setOrientation(LinearLayout.HORIZONTAL);
				txtNote = new TextView(getContext());
				txtNote.setTextColor(Color.GRAY);
				row2.addView(txtNote);

				LinearLayout layout = new LinearLayout(getContext());
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(10, 10, 10, 10);
				layout.addView(row1);
				layout.addView(row2);
				convertView = layout;

			}

			String numberText = " #" + task.getNumber();
			SpannableString spanString = new SpannableString(numberText);
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
			txtNumber.setText(spanString);

			imgStatus.setImageResource(task.getStatusImage());
			txtDate.setText(DATE_FORMAT.format(task.getCreatedAt()));
			txtNote.setText(task.getNote());

			return convertView;
		}
	}
}
