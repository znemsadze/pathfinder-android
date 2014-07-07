package gse.pathfinder.sql;

import gse.pathfinder.sql.DatabaseContract.HttpRequestContract;
import gse.pathfinder.sql.DatabaseContract.HttpRequestParamsContract;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int	    DATABASE_VERSION	     = 1;
	public static final String	DATABASE_NAME	         = "pathfinder.db";

	private static final String SQL_CREATE_HTTR_REQUEST =
			"CREATE TABLE " + HttpRequestContract.TABLE_NAME + " ("
			+ HttpRequestContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ HttpRequestContract.COLUMN_URL + " TEXT "
			+ ")";
	
	private static final String	SQL_CREATE_HTTP_REQUEST_PARAMS =
			"CREATE TABLE " + HttpRequestParamsContract.TABLE_NAME + " ("
			+ HttpRequestParamsContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ HttpRequestParamsContract.COLUMN_NAME_REQUEST_ID + " INTEGER, "
			+ HttpRequestParamsContract.COLUMN_NAME_PARNAME + " VARCHAR, "
			+ HttpRequestParamsContract.COLUMN_NAME_PARVALUE + " TEXT "
			+ ")";

	private static final String	SQL_DELETE_HTTP_REQUEST	= "DROP TABLE IF EXISTS " + HttpRequestContract.TABLE_NAME;
	private static final String	SQL_DELETE_HTTP_REQUEST_PARAMS	= "DROP TABLE IF EXISTS " + HttpRequestParamsContract.TABLE_NAME;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_HTTR_REQUEST);
		db.execSQL(SQL_CREATE_HTTP_REQUEST_PARAMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_HTTP_REQUEST);
		db.execSQL(SQL_DELETE_HTTP_REQUEST_PARAMS);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
