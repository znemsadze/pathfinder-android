package gse.pathfinder.api;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Substation;

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

	static final List<Line> getLines(Context context, String username, String password) throws IOException, JSONException {
		JSONObject json = getObjects(context, username, password, "/lines.json", null);
		List<Line> lines = new ArrayList<Line>();
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			Line line = new Line();
			JSONObject feature = features.getJSONObject(i);
			JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
			for (int j = 0; j < coordinates.length(); j++) {
				JSONArray pointData = coordinates.getJSONArray(j);
				Point point = new Point(pointData.getDouble(1), pointData.getDouble(0));
				line.getPoints().add(point);
			}
			line.setId(feature.getString("id"));
			JSONObject properties = feature.getJSONObject("properties");
			line.setName(properties.optString("name"));
			line.setDescription(properties.optString("description"));
			line.setRegion(properties.optString("region"));
			line.setDirection(properties.optString("direction"));
			lines.add(line);
		}
		return lines;
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
				Point point = new Point(pointData.getDouble(1), pointData.getDouble(0));
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

	static final List<Office> getOffices(Context context, String username, String password) throws IOException, JSONException {
		JSONObject json = getObjects(context, username, password, "/offices.json", null);
		List<Office> offices = new ArrayList<Office>();
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			Office office = new Office();
			JSONObject feature = features.getJSONObject(i);
			JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
			office.setPoint(new Point(coordinates.getDouble(1), coordinates.getDouble(0)));
			office.setId(feature.getString("id"));
			JSONObject properties = feature.getJSONObject("properties");
			office.setName(properties.optString("name"));
			office.setDescription(properties.optString("description"));
			office.setRegion(properties.optString("region"));
			office.setAddress(properties.getString("address"));
			offices.add(office);
		}
		return offices;
	}

	static final List<Substation> getSubstations(Context context, String username, String password) throws IOException, JSONException {
		JSONObject json = getObjects(context, username, password, "/substations.json", null);
		List<Substation> substations = new ArrayList<Substation>();
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			Substation substation = new Substation();
			JSONObject feature = features.getJSONObject(i);
			JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
			substation.setPoint(new Point(coordinates.getDouble(1), coordinates.getDouble(0)));
			substation.setId(feature.getString("id"));
			JSONObject properties = feature.getJSONObject("properties");
			substation.setName(properties.optString("name"));
			substation.setDescription(properties.optString("description"));
			substation.setRegion(properties.optString("region"));
			substations.add(substation);
		}
		return substations;
	}
}
