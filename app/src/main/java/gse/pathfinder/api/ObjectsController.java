package gse.pathfinder.api;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.PathDetail;
import gse.pathfinder.models.PathSurface;
import gse.pathfinder.models.PathType;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.Tower;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;

public class ObjectsController {
	static final String getObjectsUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/objects";
	}

	private static final JSONObject getObjects(Context context, String username, String password, String url, Integer page) throws IOException, JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		if (null != page) params.put("page", String.valueOf(page));
		return NetworkUtils.getJSONObject(context, getObjectsUrl(context) + url, params);
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
			if (!properties.isNull("name")) line.setName(properties.optString("name"));
			if (!properties.isNull("description")) line.setDescription(properties.optString("description"));
			if (!properties.isNull("region")) line.setRegion(properties.optString("region"));
			if (!properties.isNull("direction")) line.setDirection(properties.optString("direction"));
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
			if (!properties.isNull("name")) path.setName(properties.optString("name"));
			if (!properties.isNull("description")) path.setDescription(properties.optString("description"));
			if (!properties.isNull("region")) path.setRegion(properties.optString("region"));
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
			JSONObject properties = feature.getJSONObject("properties");
			double lat = coordinates.getDouble(1);
			double lng = coordinates.getDouble(0);
			double easting = properties.getDouble("easting");
			double northing = properties.getDouble("northing");
			office.setPoint(new Point(lat, lng, easting, northing));
			office.setId(feature.getString("id"));
			if (!properties.isNull("name")) office.setName(properties.optString("name"));
			if (!properties.isNull("description")) office.setDescription(properties.optString("description"));
			if (!properties.isNull("region")) office.setRegion(properties.optString("region"));
			if (!properties.isNull("address")) office.setAddress(properties.optString("address"));
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
			JSONObject properties = feature.getJSONObject("properties");
			double lat = coordinates.getDouble(1);
			double lng = coordinates.getDouble(0);
			double easting = properties.getDouble("easting");
			double northing = properties.getDouble("northing");
			substation.setPoint(new Point(lat, lng, easting, northing));
			substation.setId(feature.getString("id"));
			if (!properties.isNull("name")) substation.setName(properties.optString("name"));
			if (!properties.isNull("description")) substation.setDescription(properties.optString("description"));
			if (!properties.isNull("region")) substation.setRegion(properties.optString("region"));
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
			JSONObject properties = feature.getJSONObject("properties");
			double lat = coordinates.getDouble(1);
			double lng = coordinates.getDouble(0);
			double easting = properties.getDouble("easting");
			double northing = properties.getDouble("northing");
			tower.setPoint(new Point(lat, lng, easting, northing));
			tower.setId(feature.getString("id"));
			if (!properties.isNull("name")) tower.setName(properties.optString("name"));
			if (!properties.isNull("description")) tower.setDescription(properties.optString("description"));
			if (!properties.isNull("region")) tower.setRegion(properties.optString("region"));
			if (!properties.isNull("category")) tower.setCategory(properties.optString("category"));
			if (!properties.isNull("linename")) tower.setLinename(properties.optString("linename"));
			JSONObject images = properties.optJSONObject("images");
			if (null != images) {
				JSONArray larges = images.getJSONArray("larges");
				for (int j = 0; j < larges.length(); j++) {
					tower.getImages().add(larges.getString(j));
				}
			}
			towers.add(tower);
		}
		return towers;
	}

	static final String uploadTowerImage(Context context, String username, String password, Tower tower, Uri file) throws IOException, JSONException {
		String url = NetworkUtils.getApiUrl(context).concat("/towers/upload_photo");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.addPart("file", new InputStreamBody(context.getContentResolver().openInputStream(file), ContentType.MULTIPART_FORM_DATA));
//		entityBuilder.addPart("file", new FileBody(new File(file.getPath())));
		entityBuilder.addTextBody("id", tower.getId());
		HttpEntity multipartFormEntity= entityBuilder.build();
		post.setEntity(multipartFormEntity);
		HttpResponse response = httpClient.execute(post);
		JSONObject json = NetworkUtils.getJSONObjectFromInputStream(response.getEntity().getContent());
		return json.getString("file");
	}

	static final List<PathType> getPathTypes(Context context, String username, String password) throws IOException, JSONException {
		String url = NetworkUtils.getApiUrl(context) + "/paths/details.json";
		JSONArray typeData = NetworkUtils.getJSONArray(context, url, null);
		List<PathType> types = new ArrayList<PathType>();
		for (int i = 0; i < typeData.length(); i++) {
			JSONObject typeObject = typeData.getJSONObject(i);
			PathType type = new PathType();
			type.setId(typeObject.getString("id"));
			type.setName(typeObject.getString("name"));
			type.setOrderBy(typeObject.getInt("order_by"));
			JSONArray surfaceData = typeObject.getJSONArray("surfaces");
			for (int j = 0; j < surfaceData.length(); j++) {
				JSONObject surfaceObject = surfaceData.getJSONObject(j);
				PathSurface surface = new PathSurface();
				surface.setId(surfaceObject.getString("id"));
				surface.setName(surfaceObject.getString("name"));
				surface.setOrderBy(surfaceObject.getInt("order_by"));
				surface.setType(type);
				type.getSurfaces().add(surface);
				JSONArray detailData = surfaceObject.getJSONArray("details");
				for (int k = 0; k < detailData.length(); k++) {
					JSONObject detailObject = detailData.getJSONObject(k);
					PathDetail detail = new PathDetail();
					detail.setId(detailObject.getString("id"));
					detail.setName(detailObject.getString("name"));
					detail.setOrderBy(detailObject.getInt("order_by"));
					detail.setSurface(surface);
					surface.getDetails().add(detail);
					// Log.d("PATH_DETAIL", type.getName() + " - " + surface.getName() + " - " + detail.getName());
				}
			}
			types.add(type);
		}
		return types;
	}
}
