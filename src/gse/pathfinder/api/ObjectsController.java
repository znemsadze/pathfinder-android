package gse.pathfinder.api;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.Tower;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ObjectsController {
	static final String getObjectsUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/objects";
	}

	private static final JSONObject getObjects(Context context, String username, String password, String url, Integer page) throws IOException, JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		if (null != page) params.put("page", String.valueOf(page));
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

	static final List<Tower> getTowers(Context context, String username, String password, int page) throws IOException, JSONException {
		JSONObject json = getObjects(context, username, password, "/towers.json", page);
		List<Tower> towers = new ArrayList<Tower>();
		JSONArray features = json.getJSONArray("features");
		for (int i = 0; i < features.length(); i++) {
			Tower tower = new Tower();
			JSONObject feature = features.getJSONObject(i);
			JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
			tower.setPoint(new Point(coordinates.getDouble(1), coordinates.getDouble(0)));
			tower.setId(feature.getString("id"));
			JSONObject properties = feature.getJSONObject("properties");
			tower.setName(properties.optString("name"));
			tower.setDescription(properties.optString("description"));
			tower.setRegion(properties.optString("region"));
			tower.setCategory(properties.optString("category"));
			tower.setLinename(properties.optString("linename"));
			towers.add(tower);
		}
		return towers;
	}
}
