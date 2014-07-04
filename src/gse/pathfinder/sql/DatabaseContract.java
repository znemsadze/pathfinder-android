package gse.pathfinder.sql;

import android.provider.BaseColumns;

public final class DatabaseContract {
	private DatabaseContract() {}

	public static abstract class HttpRequestParams implements BaseColumns {
		public static final String	TABLE_NAME		       = "http_request_params";
		public static final String	COLUMN_NAME_REQUEST		= "request_id";
		public static final String	COLUMN_NAME_PARNAME		= "parname";
		public static final String	COLUMN_NAME_PARVALUE	= "parvalue";
	}
}
