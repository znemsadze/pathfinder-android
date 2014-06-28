package gse.pathfinder.api;

import gse.pathfinder.models.User;
import gse.pathfinder.ui.ILoggable;

public class ApplicationController {
	private static User	currentUser;

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public static User login(ILoggable loggable, String username, String password) {
		return currentUser = UsersController.login(loggable, username, password);
	}
}
