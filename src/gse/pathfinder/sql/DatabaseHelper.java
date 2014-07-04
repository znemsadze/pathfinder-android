package gse.pathfinder.sql;

import gse.pathfinder.sql.DatabaseContract.HttpRequestParams;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int	    DATABASE_VERSION	     = 1;
	public static final String	DATABASE_NAME	         = "database01.db";

	private static final String	SQL_CREATE_HTTP_PARAMS =
			"CREATE TABLE " + HttpRequestParams.TABLE_NAME + " ("
			+ HttpRequestParams._ID + " INTEGER PRIMARY KEY, "
			+ HttpRequestParams.COLUMN_NAME_REQUEST + " INTEGER, "
			+ HttpRequestParams.COLUMN_NAME_PARNAME + " VARCHAR, "
			+ HttpRequestParams.COLUMN_NAME_PARVALUE + " TEXT "
			+ ")";

	private static final String	SQL_DELETE_HTTP_PARAMS	= "DROP TABLE IF EXISTS " + HttpRequestParams.TABLE_NAME;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_HTTP_PARAMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_HTTP_PARAMS);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
