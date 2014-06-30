package gse.pathfinder.api;

import gse.pathfinder.models.Task;
import gse.pathfinder.models.User;
import gse.pathfinder.ui.ILoggable;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

public class ApplicationController {
	private static User	currentUser;

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static User login(String username, String password) throws IOException, JSONException {
		return currentUser = UsersController.login(username, password);
	}

	public static void logout(ILoggable loggable) {
		currentUser = null;
	}

	public static void trackPoint(String userid, double lat, double lng) throws IOException, JSONException {
		UsersController.trackPoint(userid, lat, lng);
	}

	public static List<Task> getTasks(String username, String password, String page) throws IOException, JSONException {
		return TasksController.getTasks(username, password, page);
	}
}
