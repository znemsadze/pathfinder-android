package gse.pathfinder.api;

import gse.pathfinder.models.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

class TasksController {
	static final String	TASKS_URL	= ApiUtils.API_URL + "/tasks";

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
			// TODO: add points
			// TODO: add destinations
			tasks.add(task);
		}

		return tasks;
	}
}
