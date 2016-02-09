package gse.pathfinder.api;

import gse.pathfinder.models.PathLines;
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

	public static List<PathLines> getShortestPath(Context context, String username, String password, Point from, Point to) throws IOException, JSONException {
		String url = getTasksUrl(context) + ".json";

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		params.put("from", from.getLng() + ":" + from.getLat());
		params.put("to", to.getLng() + ":" + to.getLat());

		JSONArray json = NetworkUtils.getJSONArray(context, url, params);

		List<Point> points = new ArrayList<Point>();
		List<PathLines> pathLines=new ArrayList<PathLines>();
		JSONObject path0=null;
		PathLines pathLine=null;
		for (int i=0;i<json.length();i++) {
			path0 = json.getJSONObject(i);
			points = new ArrayList<Point>();
			pathLine=new PathLines();
			JSONArray points0 = path0.getJSONArray("points");
			pathLine.setColor(path0.getString("pathcolor"));
			pathLine.setLineTypeName(path0.getString("lineType"));
			pathLine.setSurficeName(path0.getString("surfaceName"));
			pathLine.setDescription(path0.getString("path_description"));
			pathLine.setLength(path0.getString("length"));
			pathLine.setLineName(path0.getString("lineName"));
			for (int j = 0; j < points0.length(); j++) {
				JSONObject point0 = points0.getJSONObject(j);
				double lat = point0.getDouble("lat");
				double lng = point0.getDouble("lng");
				points.add(new Point(lat, lng));
			}
			pathLine.setPoints(points);
			pathLines.add(pathLine);
		}
		return pathLines;
	}

}
