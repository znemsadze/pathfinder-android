package gse.pathfinder.api;

import gse.pathfinder.models.User;
import gse.pathfinder.ui.ILoggable;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

class UsersController {
	static final User login(ILoggable log, String username, String password) {
		String url = ApiUtils.API_URL + "/users/login.json";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		try {
			JSONObject json = ApiUtils.getJSONFromUrl(url, params);
			User user = new User();
			user.setId(json.getString("id"));
			user.setFirstName(json.getString("first_name"));
			user.setLastName(json.getString("last_name"));
			user.setUsername(json.getString("username"));
			user.setPassword(password);
			return user;
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
	}
}
