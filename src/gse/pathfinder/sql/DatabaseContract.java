package gse.pathfinder.sql;

import android.provider.BaseColumns;

public final class DatabaseContract {
	private DatabaseContract() {}

	public static abstract class HttpRequestDb implements BaseColumns {
		public static final String TABLE = "http_request";
		public static final String COL_URL = "url";
	}

	public static abstract class HttpRequestParamsDb implements BaseColumns {
		public static final String TABLE = "http_request_params";
		public static final String COL_REQUEST_ID = "request_id";
		public static final String COL_PARNAME = "parname";
		public static final String COL_PARVALUE = "parvalue";
	}

	public static abstract class LastTrackDb implements BaseColumns {
		public static final String TABLE = "last_track";
		public static final String COL_LAT = "lat";
		public static final String COL_LNG = "lng";
	}

	public static abstract class OfficeDb implements BaseColumns {
		public static final String TABLE = "office";
		public static final String COL_SID = "sid";
		public static final String COL_NAME = "name";
		public static final String COL_DESCRIPTION = "description";
		public static final String COL_REGION = "region";
		public static final String COL_LAT = "lat";
		public static final String COL_LNG = "lng";
		public static final String COL_ADDRESS = "address";
	}

	public static abstract class SubstationDb implements BaseColumns {
		public static final String TABLE = "substation";
		public static final String COL_SID = "sid";
		public static final String COL_NAME = "name";
		public static final String COL_DESCRIPTION = "description";
		public static final String COL_REGION = "region";
		public static final String COL_LAT = "lat";
		public static final String COL_LNG = "lng";
	}

	public static abstract class TowerDb implements BaseColumns {
		public static final String TABLE = "tower";
		public static final String COL_SID = "sid";
		public static final String COL_NAME = "name";
		public static final String COL_DESCRIPTION = "description";
		public static final String COL_REGION = "region";
		public static final String COL_LAT = "lat";
		public static final String COL_LNG = "lng";
		public static final String COL_CATEGORY = "category";
		public static final String COL_LINENAME = "linename";
	}
}
