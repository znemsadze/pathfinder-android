package gse.pathfinder.api;

import gse.pathfinder.sql.DatabaseContract.HttpRequest;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParams;
import gse.pathfinder.sql.DatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	// static final String	API_URL	= "http://10.0.2.2:8000/api";
	static final String	API_URL	= "http://172.16.50.128:3000/api";

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

	private static void sendSingleSavedRequest(Context context) {
		if (isConnected(context)) {
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor requestCursor = null;
			try {
				String[] columns = { HttpRequest._ID, HttpRequest.COLUMN_URL };
				requestCursor = db.query(HttpRequest.TABLE_NAME, columns, null, null, null, null, HttpRequest._ID);
				while (requestCursor.moveToNext()) {
					long requestId = requestCursor.getInt(0);
					String requestUrl = requestCursor.getString(1);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String[] paramColumns = { HttpRequestParams.COLUMN_NAME_PARNAME, HttpRequestParams.COLUMN_NAME_PARVALUE };
					String selection = HttpRequestParams.COLUMN_NAME_REQUEST_ID;
					String[] selectionArgs = { String.valueOf(requestId) };
					Cursor paramsCursor = db.query(HttpRequestParams.TABLE_NAME, paramColumns, selection, selectionArgs, null, null, HttpRequest._ID);
					while (paramsCursor.moveToNext()) {
						String name = paramsCursor.getString(0);
						String value = paramsCursor.getString(1);
						params.add(new BasicNameValuePair(name, value));
					}
					paramsCursor.close();
					try {
						getInputStream(requestUrl, params);
						db.delete(HttpRequest.TABLE_NAME, HttpRequest._ID, new String[] { String.valueOf(requestId) });
						db.delete(HttpRequestParams.TABLE_NAME, HttpRequestParams.COLUMN_NAME_REQUEST_ID, new String[] { String.valueOf(requestId) });
					} catch (IOException ex) {
						return;
					}
				}
			} finally {
				try {
					if (null != db) db.close();
					if (null != requestCursor) requestCursor.close();
				} catch (Exception ex) {}
			}
		}
	}

	static void sendData(Context context, String url, List<NameValuePair> params) {
		saveToLocalDatabase(context, url, params);
		sendSingleSavedRequest(context);
	}
}
