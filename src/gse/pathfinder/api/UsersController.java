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
	static final User login(String username, String password) throws IOException, JSONException {
		String url = ApiUtils.API_URL + "/users/login.json";

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
}
