package gse.pathfinder.sql;

import gse.pathfinder.models.Office;
import gse.pathfinder.models.Point;
import gse.pathfinder.sql.DatabaseContract.OfficeDb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OfficeUtils {
	public static void clearOffices(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(OfficeDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void saveOffices(Context context, List<Office> offices) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Office office : offices) {
				ContentValues row = new ContentValues();
				row.put(OfficeDb.COL_ID, office.getId());
				row.put(OfficeDb.COL_NAME, office.getName());
				row.put(OfficeDb.COL_DESCRIPTION, office.getDescription());
				row.put(OfficeDb.COL_REGION, office.getRegion());
				row.put(OfficeDb.COL_LAT, office.getPoint().getLat());
				row.put(OfficeDb.COL_LNG, office.getPoint().getLng());
				row.put(OfficeDb.COL_EASTING, office.getPoint().getEasting());
				row.put(OfficeDb.COL_NORTHING, office.getPoint().getNorthing());
				row.put(OfficeDb.COL_ADDRESS, office.getAddress());
				db.insert(OfficeDb.TABLE, null, row);
			}
		} finally {
			db.close();
		}
	}

	public static List<Office> getOffices(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Office> offices = new ArrayList<Office>();
		try {
			String[] columns = { OfficeDb.COL_ID, OfficeDb.COL_NAME, OfficeDb.COL_DESCRIPTION, OfficeDb.COL_REGION, OfficeDb.COL_LAT, OfficeDb.COL_LNG, OfficeDb.COL_EASTING, OfficeDb.COL_NORTHING, OfficeDb.COL_ADDRESS };
			cursor = db.query(OfficeDb.TABLE, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Office office = new Office();
				office.setId(cursor.getString(0));
				office.setName(cursor.getString(1));
				office.setDescription(cursor.getString(2));
				office.setRegion(cursor.getString(3));
				double lat = cursor.getDouble(4);
				double lng = cursor.getDouble(5);
				double easting = cursor.getDouble(6);
				double northing = cursor.getDouble(7);
				office.setPoint(new Point(lat, lng, easting, northing));
				office.setAddress(cursor.getString(8));
				offices.add(office);
			}
			return offices;
		} finally {
			if (null != cursor) cursor.close();
			if (null != db) db.close();
		}
	}
}
