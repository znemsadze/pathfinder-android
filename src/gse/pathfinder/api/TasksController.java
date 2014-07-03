package gse.pathfinder.api;

import gse.pathfinder.models.Task;
import gse.pathfinder.models.WithPoint;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

class TasksController {
	static final String	          TASKS_URL	  = ApiUtils.API_URL + "/tasks";

	@SuppressLint("SimpleDateFormat")
	static final SimpleDateFormat	DATE_FORMAT	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static final List<Task> getTasks(String username, String password, String page) throws IOException, JSONException {
		String url = TASKS_URL + ".json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("page", String.valueOf(page)));

		JSONObject json = ApiUtils.getJSONFromUrl(url, params);

		if (json.has("error")) throw new RuntimeException(json.getString("error"));

		Log.d("TASKS", json.toString());

		List<Task> tasks = new ArrayList<Task>();

		JSONArray allTasks = json.getJSONArray("tasks");
		for (int i = 0; i < allTasks.length(); i++) {
			JSONObject taskJson = allTasks.getJSONObject(i);
			Task task = new Task();
			task.setId(taskJson.getString("id"));
			task.setNote(taskJson.optString("note"));
			task.setStatus(taskJson.getInt("status"));
			task.setNumber(taskJson.getInt("number"));
			try {
				task.setCreatedAt(DATE_FORMAT.parse(taskJson.getString("created_at")));
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
			// TODO: add points
			JSONArray destinations = taskJson.getJSONArray("destinations");
			for (int j = 0; j < destinations.length(); j++) {
				task.getDestinations().add(WithPoint.fromJson(destinations.getJSONObject(j)));
			}
			tasks.add(task);
		}

		return tasks;
	}
}
