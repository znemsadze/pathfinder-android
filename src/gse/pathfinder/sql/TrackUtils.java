package gse.pathfinder.sql;

import gse.pathfinder.models.Point;
import gse.pathfinder.sql.DatabaseContract.LastTrackContract;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TrackUtils {
	public static void clearLastTrack(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(LastTrackContract.TABLE_NAME, null, null);
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
				reqValues.put(LastTrackContract.COLUMN_NAME_LAT, point.getLat());
				reqValues.put(LastTrackContract.COLUMN_NAME_LNG, point.getLng());
				db.insert(LastTrackContract.TABLE_NAME, null, reqValues);
			}
		} finally {
			db.close();
		}
	}
}
