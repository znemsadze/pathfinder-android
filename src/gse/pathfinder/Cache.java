package gse.pathfinder;

import gse.pathfinder.models.Task;

public class Cache {
	public static void clearAllCaches() {
		clearTaskCache();
	}

	public static void clearTaskCache() {
		if (null != TasksActivity.TASKS) TasksActivity.TASKS.clear();
		if (null != TaskNoteActivity.PATH_TYPES) TaskNoteActivity.PATH_TYPES.clear();
		TasksActivity.TASKS = null;
	}

	public static void updateTask(Task task) {
		if (null != TasksActivity.TASKS) {
			for (int i = 0; i < TasksActivity.TASKS.size(); i++) {
				Task t = TasksActivity.TASKS.get(i);
				if (task.getId().equals(t.getId())) {
					TasksActivity.TASKS.remove(i);
					TasksActivity.TASKS.add(i, task);
				}
			}
		}
	}
}
