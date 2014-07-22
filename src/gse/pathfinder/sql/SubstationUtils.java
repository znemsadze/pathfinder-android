package gse.pathfinder.sql;

import gse.pathfinder.models.Point;
import gse.pathfinder.models.Substation;
import gse.pathfinder.sql.DatabaseContract.SubstationDb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SubstationUtils {
	public static void clearSubstations(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(SubstationDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void saveSubstations(Context context, List<Substation> substations) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Substation substation : substations) {
				ContentValues row = new ContentValues();
				row.put(SubstationDb.COL_ID, substation.getId());
				row.put(SubstationDb.COL_NAME, substation.getName());
				row.put(SubstationDb.COL_DESCRIPTION, substation.getDescription());
				row.put(SubstationDb.COL_REGION, substation.getRegion());
				row.put(SubstationDb.COL_LAT, substation.getPoint().getLat());
				row.put(SubstationDb.COL_LNG, substation.getPoint().getLng());
				row.put(SubstationDb.COL_EASTING, substation.getPoint().getEasting());
				row.put(SubstationDb.COL_NORTHING, substation.getPoint().getNorthing());
				db.insert(SubstationDb.TABLE, null, row);
			}
		} finally {
			db.close();
		}
	}

	public static List<Substation> getSubstations(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Substation> substations = new ArrayList<Substation>();
		try {
			String[] columns = { SubstationDb.COL_ID, SubstationDb.COL_NAME, SubstationDb.COL_DESCRIPTION, SubstationDb.COL_REGION, SubstationDb.COL_LAT, SubstationDb.COL_LNG, SubstationDb.COL_EASTING, SubstationDb.COL_NORTHING };
			cursor = db.query(SubstationDb.TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Substation substation = new Substation();
				substation.setId(cursor.getString(0));
				substation.setName(cursor.getString(1));
				substation.setDescription(cursor.getString(2));
				substation.setRegion(cursor.getString(3));
				double lat = cursor.getDouble(4);
				double lng = cursor.getDouble(5);
				double easting = cursor.getDouble(6);
				double northing = cursor.getDouble(7);
				substation.setPoint(new Point(lat, lng, easting, northing));
				substations.add(substation);
			}
			return substations;
		} finally {
			if (null != cursor) cursor.close();
			if (null != db) db.close();
		}
	}
}
