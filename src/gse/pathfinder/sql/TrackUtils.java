package gse.pathfinder.sql;

import gse.pathfinder.models.Point;
import gse.pathfinder.sql.DatabaseContract.LastTrackDb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrackUtils {
	public static void clearLastTrack(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(LastTrackDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void saveLastTrack(Context context, Point point) {
		List<Point> points = new ArrayList<Point>();
		points.add(point);
		saveLastTrack(context, points);
	}

	public static void saveLastTrack(Context context, List<Point> points) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Point point : points) {
				ContentValues reqValues = new ContentValues();
				reqValues.put(LastTrackDb.COL_LAT, point.getLat());
				reqValues.put(LastTrackDb.COL_LNG, point.getLng());
				db.insert(LastTrackDb.TABLE, null, reqValues);
			}
		} finally {
			db.close();
		}
	}

	public static List<Point> getLastTrack(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Point> points = new ArrayList<Point>();
		try {
			String[] columns = { LastTrackDb.COL_LAT, LastTrackDb.COL_LNG };
			cursor = db.query(LastTrackDb.TABLE, columns, null, null, null, null, LastTrackDb._ID);
			while (cursor.moveToNext()) {
				double lat = cursor.getDouble(0);
				double lng = cursor.getDouble(1);
				points.add(new Point(lat, lng));
			}
			return points;
		} finally {
			try {
				if (null != cursor) cursor.close();
				if (null != db) db.close();
			} catch (Exception ex) {}
		}
	}
}
