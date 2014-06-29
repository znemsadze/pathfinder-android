package gse.pathfinder.api;

import gse.pathfinder.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

class UsersController {
	static final String	USERS_URL	= ApiUtils.API_URL + "/users";

	static final User login(String username, String password) throws IOException, JSONException {
		String url = USERS_URL + "/login.json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		JSONObject json = ApiUtils.getJSONFromUrl(url, params);

		if (json.has("error")) throw new RuntimeException(json.getString("error"));

		User user = new User();
		user.setId(json.getString("id"));
		user.setFirstName(json.getString("first_name"));
		user.setLastName(json.getString("last_name"));
		user.setUsername(json.getString("username"));
		user.setPassword(password);
		return user;
	}

	static final void trackPoint(String userid, double lat, double lng) throws IOException, JSONException {
		String url = USERS_URL + "/track_point.json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userid));
		params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
		params.add(new BasicNameValuePair("lng", String.valueOf(lng)));

		JSONObject json = ApiUtils.getJSONFromUrl(url, params);

		if (!json.has("status")) throw new RuntimeException("failed to send tracking info");
	}
}
