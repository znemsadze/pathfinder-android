package gse.pathfinder.sql;

import gse.pathfinder.models.HttpRequest;
import gse.pathfinder.models.HttpRequestParam;
import gse.pathfinder.sql.DatabaseContract.HttpRequestDb;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParamsDb;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HttpRequestUtils {
	public static void saveRequestToDatabase(Context context, HttpRequest request) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues reqValues = new ContentValues();
			reqValues.put(HttpRequestDb.COL_URL, request.getUrl());
			long requestId = db.insert(HttpRequestDb.TABLE, null, reqValues);
			for (HttpRequestParam param : request.getParameters()) {
				ContentValues values = new ContentValues();
				values.put(HttpRequestParamsDb.COL_REQUEST_ID, requestId);
				values.put(HttpRequestParamsDb.COL_PARNAME, param.getName());
				values.put(HttpRequestParamsDb.COL_PARVALUE, param.getValue());
				db.insert(HttpRequestParamsDb.TABLE, null, values);
			}
		} finally {
			db.close();
		}
	}

	public static HttpRequest getFirstRequestFromDatabase(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor requestCursor = null;
		try {
			String[] columns = { HttpRequestDb._ID, HttpRequestDb.COL_URL };
			requestCursor = db.query(HttpRequestDb.TABLE, columns, null, null, null, null, HttpRequestDb._ID);
			if (requestCursor.moveToFirst()) {
				HttpRequest request = new HttpRequest();
				request.setId(requestCursor.getInt(0));
				request.setUrl(requestCursor.getString(1));
				String[] paramColumns = { HttpRequestParamsDb._ID, HttpRequestParamsDb.COL_PARNAME, HttpRequestParamsDb.COL_PARVALUE };
				Cursor paramsCursor = db.query(HttpRequestParamsDb.TABLE, paramColumns, "request_id=?", new String[] { String.valueOf(request.getId()) }, null, null, HttpRequestDb._ID);
				while (paramsCursor.moveToNext()) {
					HttpRequestParam param = new HttpRequestParam();
					param.setId(paramsCursor.getInt(0));
					param.setName(paramsCursor.getString(1));
					param.setValue(paramsCursor.getString(2));
					request.getParameters().add(param);
				}
				paramsCursor.close();

				return request;
			} else {
				return null;
			}
		} finally {
			try {
				if (null != requestCursor) requestCursor.close();
				if (null != db) db.close();
			} catch (Exception ex) {}
		}
	}

	public static void deleteRequestFromDatabase(Context context, int requestId) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(HttpRequestDb.TABLE, HttpRequestDb._ID + "=?", new String[] { String.valueOf(requestId) });
			db.delete(HttpRequestParamsDb.TABLE, HttpRequestParamsDb.COL_REQUEST_ID + "=?", new String[] { String.valueOf(requestId) });
		} finally {
			db.close();
		}
	}
}
