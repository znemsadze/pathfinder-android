package gse.pathfinder.sql;

import gse.pathfinder.models.Path;
import gse.pathfinder.models.Point;
import gse.pathfinder.sql.DatabaseContract.PathDb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PathUtils {
	public static void clearPaths(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(PathDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void savePaths(Context context, List<Path> paths) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Path path : paths) {
				ContentValues row = new ContentValues();
				row.put(PathDb.COL_ID, path.getId());
				row.put(PathDb.COL_NAME, path.getName());
				row.put(PathDb.COL_DESCRIPTION, path.getDescription());
				row.put(PathDb.COL_REGION, path.getRegion());
				row.put(PathDb.COL_POINTS, Point.asText(path.getPoints()));
				db.insert(PathDb.TABLE, null, row);
			}
		} finally {
			db.close();
		}
	}

	public static List<Path> getPaths(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Path> paths = new ArrayList<Path>();
		try {
			String[] columns = { PathDb.COL_ID, PathDb.COL_NAME, PathDb.COL_DESCRIPTION, PathDb.COL_REGION, PathDb.COL_POINTS };
			cursor = db.query(PathDb.TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Path path = new Path();
				path.setId(cursor.getString(0));
				path.setName(cursor.getString(1));
				path.setDescription(cursor.getString(2));
				path.setRegion(cursor.getString(3));
				path.setPoints(Point.fromText(cursor.getString(4)));
				paths.add(path);
			}
			return paths;
		} finally {
			if (null != cursor) cursor.close();
			if (null != db) db.close();
		}
	}
}
