package gse.pathfinder;

public class Cache {
	public static void clearAllCaches() {
		clearTaskCache();
	}

	public static void clearTaskCache() {
		if (null != TasksActivity.CACHE) TasksActivity.CACHE.clear();
		TasksActivity.CACHE = null;
	}
}
