package gse.pathfinder.api;

import gse.pathfinder.models.Task;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.ILoggable;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.content.Context;

public class ApplicationController {
	private static User	currentUser;

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

	public static List<Task> getTasks(Context context, String username, String password, String page) throws IOException, JSONException {
		return TasksController.getTasks(context, username, password, page);
	}
}
