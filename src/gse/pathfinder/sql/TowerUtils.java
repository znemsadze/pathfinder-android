package gse.pathfinder.sql;

import gse.pathfinder.models.Point;
import gse.pathfinder.models.Tower;
import gse.pathfinder.sql.DatabaseContract.TowerDb;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TowerUtils {
	static final int MAX_TOWERS = 200;

	public static void clearTowers(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(TowerDb.TABLE, null, null);
		} finally {
			db.close();
		}
	}

	public static void saveTowers(Context context, List<Tower> towers) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (Tower tower : towers) {
				ContentValues row = new ContentValues();
				row.put(TowerDb.COL_ID, tower.getId());
				row.put(TowerDb.COL_NAME, tower.getName());
				row.put(TowerDb.COL_DESCRIPTION, tower.getDescription());
				row.put(TowerDb.COL_REGION, tower.getRegion());
				row.put(TowerDb.COL_LAT, tower.getPoint().getLat());
				row.put(TowerDb.COL_LNG, tower.getPoint().getLng());
				row.put(TowerDb.COL_CATEGORY, tower.getCategory());
				row.put(TowerDb.COL_LINENAME, tower.getLinename());
				db.insert(TowerDb.TABLE, null, row);
			}
		} finally {
			db.close();
		}
	}

	public static List<Tower> getTowers(Context context, LatLngBounds bounds) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		List<Tower> towers = new ArrayList<Tower>();
		try {
			String[] columns = { TowerDb.COL_ID, TowerDb.COL_NAME, TowerDb.COL_DESCRIPTION, TowerDb.COL_REGION, TowerDb.COL_LAT, TowerDb.COL_LNG, TowerDb.COL_CATEGORY, TowerDb.COL_LINENAME };
			String condition = "lat BETWEEN ? AND ? AND lng BETWEEN ? AND ?";
			LatLng p1 = bounds.northeast;
			LatLng p2 = bounds.southwest;
			String min_lat = String.valueOf(p2.latitude);
			String max_lat = String.valueOf(p1.latitude);
			String min_lng = String.valueOf(p2.longitude);
			String max_lng = String.valueOf(p1.longitude);
			String[] conditionParams = { min_lat, max_lat, min_lng, max_lng };
			cursor = db.query(TowerDb.TABLE, columns, condition, conditionParams, null, null, null);
			while (cursor.moveToNext()) {
				Tower tower = new Tower();
				tower.setId(cursor.getString(0));
				tower.setName(cursor.getString(1));
				tower.setDescription(cursor.getString(2));
				tower.setRegion(cursor.getString(3));
				tower.setPoint(new Point(cursor.getDouble(4), cursor.getDouble(5)));
				tower.setCategory(cursor.getString(6));
				tower.setLinename(cursor.getString(7));
				towers.add(tower);
				if (towers.size() >= MAX_TOWERS) break;
			}
			return towers;
		} finally {
			if (null != cursor) cursor.close();
			if (null != db) db.close();
		}
	}
}
