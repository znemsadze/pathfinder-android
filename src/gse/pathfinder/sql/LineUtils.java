package gse.pathfinder.sql;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Point;
import gse.pathfinder.sql.DatabaseContract.LineDb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LineUtils {
	public static void clearLines(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(LineDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void saveLines(Context context, List<Line> lines) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Line line : lines) {
				ContentValues row = new ContentValues();
				row.put(LineDb.COL_ID, line.getId());
				row.put(LineDb.COL_NAME, line.getName());
				row.put(LineDb.COL_DESCRIPTION, line.getDescription());
				row.put(LineDb.COL_REGION, line.getRegion());
				row.put(LineDb.COL_POINTS, Point.asText(line.getPoints()));
				row.put(LineDb.COL_DIRECTION, line.getDirection());
				db.insert(LineDb.TABLE, null, row);
			}
		} finally {
			db.close();
		}
	}

	public static List<Line> getLines(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Line> lines = new ArrayList<Line>();
		try {
			String[] columns = { LineDb.COL_ID, LineDb.COL_NAME, LineDb.COL_DESCRIPTION, LineDb.COL_REGION, LineDb.COL_POINTS, LineDb.COL_DIRECTION };
			cursor = db.query(LineDb.TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Line line = new Line();
				line.setId(cursor.getString(0));
				line.setName(cursor.getString(1));
				line.setDescription(cursor.getString(2));
				line.setRegion(cursor.getString(3));
				line.setPoints(Point.fromText(cursor.getString(4)));
				line.setDirection(cursor.getString(5));
				lines.add(line);
			}
			return lines;
		} finally {
			if (null != cursor) cursor.close();
			if (null != db) db.close();
		}
	}
}
