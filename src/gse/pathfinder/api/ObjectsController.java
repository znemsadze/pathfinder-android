package gse.pathfinder.api;

import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ObjectsController {
	static final String getObjectsUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/objects";
	}

	private static final JSONObject getObjects(Context context, String username, String password, String url, Point point) throws IOException, JSONException {
		BasicHttpParams params = new BasicHttpParams();
		params.setParameter("username", username);
		params.setParameter("password", password);
		if (null != point) {
			params.setParameter("lat", String.valueOf(point.getLat()));
			params.setParameter("lng", String.valueOf(point.getLng()));
		}
		return NetworkUtils.get(context, getObjectsUrl(context) + url, params);
	}

	static final List<Path> getPaths(Context context, String username, String password) throws IOException, JSONException {
		JSONObject json = getObjects(context, username, password, "/pathlines.json", null);
		List<Path> paths = new ArrayList<Path>();
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			Path path = new Path();
			JSONObject feature = features.getJSONObject(i);
			JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
			for (int j = 0; j < coordinates.length(); j++) {
				JSONArray pointData = coordinates.getJSONArray(j);
				Point point = new Point(pointData.getDouble(0), pointData.getDouble(1));
				path.getPoints().add(point);
			}
			path.setId(feature.getString("id"));
			JSONObject properties = feature.getJSONObject("properties");
			path.setName(properties.optString("name"));
			path.setDescription(properties.optString("description"));
			path.setRegion(properties.optString("region"));
			paths.add(path);
		}
		return paths;
	}
}
