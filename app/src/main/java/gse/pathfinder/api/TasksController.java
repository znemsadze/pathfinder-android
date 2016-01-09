package gse.pathfinder.api;

import gse.pathfinder.models.Path;
import gse.pathfinder.models.PathDetail;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.Track;
import gse.pathfinder.models.WithPoint;
import gse.pathfinder.sql.TrackUtils;

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
import android.content.Context;

class TasksController {
	static final String getTasksUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/tasks";
	}

	@SuppressLint("SimpleDateFormat")
	static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static final List<Task> getTasks(Context context, String username, String password, String page) throws IOException, JSONException {
		String url = getTasksUrl(context) + ".json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("page", String.valueOf(page)));

		JSONObject json = NetworkUtils.postJSONObject(context, url, params);

		if (json.has("error")) throw new RuntimeException(json.getString("error"));

		List<Task> tasks = new ArrayList<Task>();
		Task currentTask = null;

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
			JSONArray paths = taskJson.optJSONArray("paths");
			if (paths != null) {
				for (int j = 0; j < paths.length(); j++) {
					JSONArray points = paths.getJSONArray(j);
					Path path = new Path();
					for (int k = 0; k < points.length(); k++) {
						JSONObject point = points.getJSONObject(k);
						path.getPoints().add(new Point(point.getDouble("lat"), point.getDouble("lng")));
					}
					task.getPaths().add(path);
				}
			}
			JSONArray destinations = taskJson.optJSONArray("destinations");
			if (destinations != null) {
				for (int j = 0; j < destinations.length(); j++) {
					task.getDestinations().add(WithPoint.fromJson(destinations.getJSONObject(j)));
				}
			}
			if (taskJson.has("trackings")) {
				JSONArray tracks = taskJson.getJSONArray("trackings");
				for (int j = 0; j < tracks.length(); j++) {
					Track track = new Track();
					JSONObject trackJson = tracks.getJSONObject(j);
					track.setId(trackJson.getString("id"));
					track.setOpen(trackJson.getBoolean("open"));
					JSONArray points = trackJson.getJSONArray("points");
					for (int k = 0; k < points.length(); k++) {
						JSONObject pointJson = points.getJSONObject(k);
						track.getPoints().add(new Point(pointJson.getDouble("lat"), pointJson.getDouble("lng")));
					}
					task.getTracks().add(track);
				}
			}
			tasks.add(task);
			if (task.isInProgress()) currentTask = task;
		}

		if (null != currentTask && !currentTask.getTracks().isEmpty()) TrackUtils.clearLastTrack(context);

		return tasks;
	}

	static final void changeTaskStatus(Context context, String username, String password, String id, String actionPrefix) throws IOException, JSONException {
		String url = getTasksUrl(context) + "/" + actionPrefix + "_task.json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("id", id));

		JSONObject json = NetworkUtils.postJSONObject(context, url, params);

		if (json.has("error")) throw new IllegalArgumentException(json.getString("error"));
	}

	static final void addNote(Context context, String username, String password, Task task, String note, Point location, PathDetail detail) throws IOException, JSONException {
		String url = getTasksUrl(context) + "/add_note.json";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("id", task.getId()));
		params.add(new BasicNameValuePair("detail_id", detail.getId()));
		params.add(new BasicNameValuePair("lat", String.valueOf(location.getLat())));
		params.add(new BasicNameValuePair("lng", String.valueOf(location.getLng())));
		params.add(new BasicNameValuePair("note", note));

		JSONObject json = NetworkUtils.postJSONObject(context, url, params);

		if (json.has("error")) throw new IllegalArgumentException(json.getString("error"));
	}
}
