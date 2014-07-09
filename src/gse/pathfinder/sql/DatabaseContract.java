package gse.pathfinder.sql;

import android.provider.BaseColumns;

public final class DatabaseContract {
	private DatabaseContract() {}

	public static abstract class HttpRequestContract implements BaseColumns {
		public static final String TABLE_NAME = "http_request";
		public static final String COLUMN_URL = "url";
	}

	public static abstract class HttpRequestParamsContract implements BaseColumns {
		public static final String TABLE_NAME = "http_request_params";
		public static final String COLUMN_NAME_REQUEST_ID = "request_id";
		public static final String COLUMN_NAME_PARNAME = "parname";
		public static final String COLUMN_NAME_PARVALUE = "parvalue";
	}

	public static abstract class LastTrackContract implements BaseColumns {
		public static final String TABLE_NAME = "last_track";
		public static final String COLUMN_NAME_LAT = "lat";
		public static final String COLUMN_NAME_LNG = "lng";
		public static final String COLUMN_NAME_FROM_SERVER = "from_server";
	}
}
