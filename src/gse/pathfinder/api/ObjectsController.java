package gse.pathfinder.api;

import gse.pathfinder.models.WithName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ObjectsController {
	static final String getObjectsUrl(Context context) {
		return NetworkUtils.getApiUrl(context) + "/objects";
	}

	static final List<WithName> getLines(Context context, String username, String password) throws IOException, JSONException {
		String url = getObjectsUrl(context) + "/lines.json";
		BasicHttpParams params = new BasicHttpParams();
		params.setParameter("username", username);
		params.setParameter("password", password);

		JSONObject json = NetworkUtils.get(context, url, params);
		List<WithName> objects = new ArrayList<WithName>();

		Log.d("OBJECTS", json.toString());

		return objects;
	}
}
