package gse.pathfinder.sql;

import gse.pathfinder.sql.DatabaseContract.HttpRequestDb;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParamsDb;
import gse.pathfinder.sql.DatabaseContract.LastTrackDb;
import gse.pathfinder.sql.DatabaseContract.OfficeDb;
import gse.pathfinder.sql.DatabaseContract.SubstationDb;
import gse.pathfinder.sql.DatabaseContract.TowerDb;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int	    DATABASE_VERSION	     = 4;
	public static final String	DATABASE_NAME	         = "pathfinder.db";

	private static final String SQL_CREATE_HTTR_REQUEST =
			"CREATE TABLE " + HttpRequestDb.TABLE + " ("
			+ HttpRequestDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ HttpRequestDb.COL_URL + " TEXT "
			+ ")";
	
	private static final String	SQL_CREATE_HTTP_REQUEST_PARAMS =
			"CREATE TABLE " + HttpRequestParamsDb.TABLE + " ("
			+ HttpRequestParamsDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ HttpRequestParamsDb.COL_REQUEST_ID + " INTEGER, "
			+ HttpRequestParamsDb.COL_PARNAME + " VARCHAR, "
			+ HttpRequestParamsDb.COL_PARVALUE + " TEXT "
			+ ")";

	private static final String SQL_CREATE_LAST_TRACK =
			"CREATE TABLE " + LastTrackDb.TABLE + " ("
			+ LastTrackDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ LastTrackDb.COL_LAT + " DOUBLE, "
			+ LastTrackDb.COL_LNG + " DOUBLE "
			+ ")";
	
	private static final String SQL_CREATE_OFFICE = 
			"CREATE TABLE " + OfficeDb.TABLE + " ("
			+ OfficeDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ OfficeDb.COL_SID + " VARCHAR, "
			+ OfficeDb.COL_NAME + " VARCHAR, "
			+ OfficeDb.COL_DESCRIPTION + " VARCHAR, "
			+ OfficeDb.COL_REGION + " VARCHAR, "
			+ OfficeDb.COL_LAT + " DOUBLE, "
			+ OfficeDb.COL_LNG + " DOUBLE, "
			+ OfficeDb.COL_ADDRESS + " VARCHAR "
			+ ")";
	
	private static final String SQL_CREATE_SUBSTATION = 
			"CREATE TABLE " + SubstationDb.TABLE + " ("
			+ SubstationDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SubstationDb.COL_SID + " VARCHAR, "
			+ SubstationDb.COL_NAME + " VARCHAR, "
			+ SubstationDb.COL_DESCRIPTION + " VARCHAR, "
			+ SubstationDb.COL_REGION + " VARCHAR, "
			+ SubstationDb.COL_LAT + " DOUBLE, "
			+ SubstationDb.COL_LNG + " DOUBLE "
			+ ")";
	
	private static final String SQL_CREATE_TOWER = 
			"CREATE TABLE " + TowerDb.TABLE + " ("
			+ TowerDb._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TowerDb.COL_SID + " VARCHAR, "
			+ TowerDb.COL_NAME + " VARCHAR, "
			+ TowerDb.COL_DESCRIPTION + " VARCHAR, "
			+ TowerDb.COL_REGION + " VARCHAR, "
			+ TowerDb.COL_LAT + " DOUBLE, "
			+ TowerDb.COL_LNG + " DOUBLE, "
			+ TowerDb.COL_CATEGORY + " VARCHAR, "
			+ TowerDb.COL_LINENAME + " VARCHAR "
			+ ")";
	
	private static final String	SQL_DELETE_HTTP_REQUEST	= "DROP TABLE IF EXISTS " + HttpRequestDb.TABLE;
	private static final String	SQL_DELETE_HTTP_REQUEST_PARAMS	= "DROP TABLE IF EXISTS " + HttpRequestParamsDb.TABLE;
	private static final String SQL_DELETE_LAST_TRACK = "DROP TABLE IF EXISTS " + LastTrackDb.TABLE;
	private static final String SQL_DELETE_OFFICE = "DROP TABLE IF EXISTS " + OfficeDb.TABLE;
	private static final String SQL_DELETE_SUBSTATION = "DROP TABLE IF EXISTS " + SubstationDb.TABLE;
	private static final String SQL_DELETE_TOWER = "DROP TABLE IF EXISTS " + TowerDb.TABLE;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_HTTR_REQUEST);
		db.execSQL(SQL_CREATE_HTTP_REQUEST_PARAMS);
		db.execSQL(SQL_CREATE_LAST_TRACK);
		db.execSQL(SQL_CREATE_OFFICE);
		db.execSQL(SQL_CREATE_SUBSTATION);
		db.execSQL(SQL_CREATE_TOWER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_HTTP_REQUEST);
		db.execSQL(SQL_DELETE_HTTP_REQUEST_PARAMS);
		db.execSQL(SQL_DELETE_LAST_TRACK);
		db.execSQL(SQL_DELETE_OFFICE);
		db.execSQL(SQL_DELETE_SUBSTATION);
		db.execSQL(SQL_DELETE_TOWER);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
