package gse.pathfinder.api;

import gse.pathfinder.sql.DatabaseContract.HttpRequest;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParams;
import gse.pathfinder.sql.DatabaseHelper;

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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class NetworkUtils {
	// static final String	API_URL	= "http://10.0.2.2:8000/api";
	static final String	API_URL	= "http://172.16.50.128:3000/api";

	private static InputStream getInputStream(String url, List<NameValuePair> params) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		return httpEntity.getContent();

	}

	static JSONObject getJSONFromUrl(String url, List<NameValuePair> params) throws IOException, JSONException, UnsupportedEncodingException {
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
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues reqValues = new ContentValues();
			reqValues.put(HttpRequest.COLUMN_URL, url);
			long requestId = db.insert(HttpRequest.TABLE_NAME, null, reqValues);
			for (NameValuePair pair : params) {
				ContentValues values = new ContentValues();
				values.put(HttpRequestParams.COLUMN_NAME_REQUEST_ID, requestId);
				values.put(HttpRequestParams.COLUMN_NAME_PARNAME, pair.getName());
				values.put(HttpRequestParams.COLUMN_NAME_PARVALUE, pair.getValue());
				db.insert(HttpRequestParams.TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	static void sendData(Context context, String url, List<NameValuePair> params) {
		saveToLocalDatabase(context, url, params);

		//		InputStream is = null;
		//		try {
		//			is = getInputStream(url, params);
		//		} catch (IOException ioex) {
		//			//
		//		} finally {
		//			if (null != is) try {
		//				is.close();
		//			} catch (IOException ex) {}
		//		}
	}
}
