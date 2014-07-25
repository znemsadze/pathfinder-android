package gse.pathfinder.api;

import gse.pathfinder.models.Line;
import gse.pathfinder.models.Office;
import gse.pathfinder.models.Path;
import gse.pathfinder.models.PathDetail;
import gse.pathfinder.models.PathType;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Substation;
import gse.pathfinder.models.Task;
import gse.pathfinder.models.Tower;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.ILoggable;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.net.Uri;

public class ApplicationController {
	public static final String VERSION = "009";

	// -- user

	private static User currentUser;

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static User login(Context context, String username, String password) throws IOException, JSONException {
		return currentUser = UsersController.login(context, username, password);
	}

	public static void logout(ILoggable loggable) {
		currentUser = null;
	}

	public static void trackPoint(Context context, String userid, double lat, double lng) throws IOException, JSONException {
		UsersController.trackPoint(context, userid, lat, lng);
	}

	// -- tracking

	public static List<Task> getTasks(Context context, String username, String password, String page) throws IOException, JSONException {
		return TasksController.getTasks(context, username, password, page);
	}

	public static void changeTaskStatus(Context context, String username, String password, String id, String actionPrefix) throws IOException, JSONException {
		TasksController.changeTaskStatus(context, username, password, id, actionPrefix);
	}

	public static void addNote(Context context, String username, String password, Task task, String note, Point location, PathDetail detail) throws IOException, JSONException {
		TasksController.addNote(context, username, password, task, note, location, detail);
	}

	// -- objects

	public static List<Line> getLines(Context context, String username, String password) throws IOException, JSONException {
		return ObjectsController.getLines(context, username, password);
	}

	public static List<Path> getPaths(Context context, String username, String password) throws IOException, JSONException {
		return ObjectsController.getPaths(context, username, password);
	}

	public static List<Office> getOffices(Context context, String username, String password) throws IOException, JSONException {
		return ObjectsController.getOffices(context, username, password);
	}

	public static List<Substation> getSubstations(Context context, String username, String password) throws IOException, JSONException {
		return ObjectsController.getSubstations(context, username, password);
	}

	public static List<Tower> getTowers(Context context, String username, String password, int page) throws IOException, JSONException {
		return ObjectsController.getTowers(context, username, password, page);
	}

	public static String uploadTowerPhoto(Context context, String username, String password, Tower tower, Uri file) throws IOException, JSONException {
		return ObjectsController.uploadTowerImage(context, username, password, tower, file);
	}

	public static final List<PathType> getPathTypes(Context context, String username, String password) throws IOException, JSONException {
		return ObjectsController.getPathTypes(context, username, password);
	}
}
