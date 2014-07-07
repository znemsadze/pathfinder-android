package gse.pathfinder;

import gse.pathfinder.models.Task;

public class Cache {
	public static void clearAllCaches() {
		clearTaskCache();
	}

	public static void clearTaskCache() {
		if (null != TasksActivity.CACHE) TasksActivity.CACHE.clear();
		TasksActivity.CACHE = null;
	}

	public static void updateTask(Task task) {
		if (null != TasksActivity.CACHE) {
			for (int i = 0; i < TasksActivity.CACHE.size(); i++) {
				Task t = TasksActivity.CACHE.get(i);
				if (task.getId().equals(t.getId())) {
					TasksActivity.CACHE.remove(i);
					TasksActivity.CACHE.add(i, task);
				}
			}
		}
	}
}
