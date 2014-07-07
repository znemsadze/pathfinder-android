package gse.pathfinder.api;

import gse.pathfinder.Preferences;
import gse.pathfinder.models.HttpRequest;
import gse.pathfinder.sql.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	// static final String DEFAULT_HOST = "10.0.2.2:8000";
	// static final String	DEFAULT_HOST	= "172.16.50.128:3000";
	static final String DEFAULT_HOST = "213.157.197.227";

	public static final String getDefaultHost(Context context) {
		return Preferences.getPreference(context).getString("host", DEFAULT_HOST);
	}

	public static final void setDefaultHost(Context context, String host) {
		SharedPreferences settings = Preferences.getPreference(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("host", host);
		editor.commit();
	}

	static final String getApiUrl(Context context) {
		return "http://" + getDefaultHost(context) + "/api";
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();
	}

	private static InputStream getInputStream(String url, List<NameValuePair> params) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		return httpEntity.getContent();

	}

	static JSONObject getJSONFromUrl(Context context, String url, List<NameValuePair> params) throws IOException, JSONException, UnsupportedEncodingException {
		InputStream is = getInputStream(url, params);
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return new JSONObject(sb.toString());
		} finally {
			is.close();
		}
	}

	private static void saveToLocalDatabase(Context context, String url, List<NameValuePair> params) {
		HttpRequestUtils.saveRequestToDatabase(context, HttpRequest.newRequest(url, params));
	}

	private static void sendSingleSavedRequest(Context context) {
		if (isConnected(context)) {
			HttpRequest request;
			while ((request = HttpRequestUtils.getFirstRequest(context)) != null) {
				try {
					getInputStream(request.getUrl(), request.getParams());
					HttpRequestUtils.deleteRequest(context, request.getId());
				} catch (IOException ex) {
					return;
				}
			}
		}
	}

	static void sendData(Context context, String url, List<NameValuePair> params) {
		saveToLocalDatabase(context, url, params);
		sendSingleSavedRequest(context);
	}
}
