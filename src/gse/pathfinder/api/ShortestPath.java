package gse.pathfinder.api;

import gse.pathfinder.models.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ShortestPath {

	static final String getTasksUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/shortestpath";
	}

	public static List<Point> getShortestPath(Context context, String username, String password, Point from, Point to) throws IOException, JSONException {
		String url = getTasksUrl(context) + ".json";

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		params.put("from", from.getLng() + ":" + from.getLat());
		params.put("to", to.getLng() + ":" + to.getLat());

		JSONArray json = NetworkUtils.getJSONArray(context, url, params);
		List<Point> points = new ArrayList<Point>();

		JSONObject path0 = json.getJSONObject(0);
		JSONArray points0 = path0.getJSONArray("points");
		for (int i = 0; i < points0.length(); i++) {
			JSONObject point0 = points0.getJSONObject(i);
			double lat = point0.getDouble("lat");
			double lng = point0.getDouble("lng");
			points.add(new Point(lat, lng));
		}

		return points;
	}

}
