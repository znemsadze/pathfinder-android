package gse.pathfinder.sql;

import gse.pathfinder.models.HttpRequest;
import gse.pathfinder.models.HttpRequestParam;
import gse.pathfinder.sql.DatabaseContract.HttpRequestContract;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParamsContract;
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
			reqValues.put(HttpRequestContract.COLUMN_URL, request.getUrl());
			long requestId = db.insert(HttpRequestContract.TABLE_NAME, null, reqValues);
			for (HttpRequestParam param : request.getParameters()) {
				ContentValues values = new ContentValues();
				values.put(HttpRequestParamsContract.COLUMN_NAME_REQUEST_ID, requestId);
				values.put(HttpRequestParamsContract.COLUMN_NAME_PARNAME, param.getName());
				values.put(HttpRequestParamsContract.COLUMN_NAME_PARVALUE, param.getValue());
				db.insert(HttpRequestParamsContract.TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	public static HttpRequest getFirstRequest(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor requestCursor = null;
		try {
			String[] columns = { HttpRequestContract._ID, HttpRequestContract.COLUMN_URL };
			requestCursor = db.query(HttpRequestContract.TABLE_NAME, columns, null, null, null, null, HttpRequestContract._ID);
			if (requestCursor.moveToFirst()) {
				HttpRequest request = new HttpRequest();

				request.setId(requestCursor.getInt(0));
				request.setUrl(requestCursor.getString(1));

				String[] paramColumns = { HttpRequestParamsContract._ID, HttpRequestParamsContract.COLUMN_NAME_PARNAME, HttpRequestParamsContract.COLUMN_NAME_PARVALUE };
				String selection = HttpRequestParamsContract.COLUMN_NAME_REQUEST_ID;
				String[] selectionArgs = { String.valueOf(request.getId()) };
				Cursor paramsCursor = db.query(HttpRequestParamsContract.TABLE_NAME, paramColumns, selection, selectionArgs, null, null, HttpRequestContract._ID);
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

	public static void deleteRequest(Context context, int requestId) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(HttpRequestContract.TABLE_NAME, HttpRequestContract._ID, new String[] { String.valueOf(requestId) });
			db.delete(HttpRequestParamsContract.TABLE_NAME, HttpRequestParamsContract.COLUMN_NAME_REQUEST_ID, new String[] { String.valueOf(requestId) });
		} finally {
			db.close();
		}
	}
}
