package gse.pathfinder.api;

import gse.pathfinder.Preferences;
import gse.pathfinder.models.HttpRequest;
import gse.pathfinder.sql.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

	public static final String getCurrenttHost(Context context) {
		return Preferences.getPreference(context).getString("host", DEFAULT_HOST);
	}

	public static final void setDefaultHost(Context context, String host) {
		SharedPreferences settings = Preferences.getPreference(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("host", host);
		editor.commit();
	}

	static final String getApiUrl(Context context) {
		return "http://" + getCurrenttHost(context) + "/api";
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();
	}

	private static InputStream postInputStream(String url, List<NameValuePair> params) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		return httpEntity.getContent();
	}

	private static InputStream getInputStream(String url, Map<String, String> query) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		if (!url.endsWith("?")) url += "?";
		if (null != query && !query.isEmpty()) {
			List<NameValuePair> q = new LinkedList<NameValuePair>();
			for (String k : query.keySet()) {
				q.add(new BasicNameValuePair(k, query.get(k)));
			}
			url += URLEncodedUtils.format(q, "utf-8");
		}

		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		return httpEntity.getContent();
	}

	private static String readInputStream(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		return sb.toString();
	}

	public static JSONObject getJSONObjectFromInputStream(InputStream is) throws IOException, JSONException {
		try {
			return new JSONObject(readInputStream(is));
		} finally {
			is.close();
		}
	}

	public static JSONArray getJSONArrayFromInputStream(InputStream is) throws IOException, JSONException {
		try {
			return new JSONArray(readInputStream(is));
		} finally {
			is.close();
		}
	}

	static JSONObject postJSONObject(Context context, String url, List<NameValuePair> params) throws IOException, JSONException {
		InputStream is = postInputStream(url, params);
		return getJSONObjectFromInputStream(is);
	}

	static JSONObject getJSONObject(Context context, String url, Map<String, String> params) throws IOException, JSONException {
		return getJSONObjectFromInputStream(getInputStream(url, params));
	}

	static JSONArray getJSONArray(Context context, String url, Map<String, String> params) throws IOException, JSONException {
		return getJSONArrayFromInputStream(getInputStream(url, params));
	}

	private static void saveToLocalDatabase(Context context, String url, List<NameValuePair> params) {
		HttpRequestUtils.saveRequestToDatabase(context, HttpRequest.newRequest(url, params));
	}

	private static void sendQueue(Context context) {
		if (isConnected(context)) {
			HttpRequest request;
			InputStream is = null;
			while ((request = HttpRequestUtils.getFirstRequestFromDatabase(context)) != null) {
				try {
					is = postInputStream(request.getUrl(), request.getParams());
					HttpRequestUtils.deleteRequestFromDatabase(context, request.getId());
				} catch (IOException ex) {
					ex.printStackTrace();
					return;
				} finally {
					try {
						is.close();
					} catch (Exception ex) {}
				}
			}
		}
	}

	static void sendData(Context context, String url, List<NameValuePair> params) {
		saveToLocalDatabase(context, url, params);
		sendQueue(context);
	}

	private static int file_prefix = 0;

	public static String downloadFile(Context context, String url) throws IOException {
		url = "http://" + getCurrenttHost(context) + url;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = getInputStream(url, null);

			file_prefix++;
			File file = new File(context.getCacheDir(), "tempfile-" + file_prefix);
			out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}

			return file.getAbsolutePath();
		} finally {
			if (null != in) in.close();
			if (null != out) out.close();
		}
	}
}
