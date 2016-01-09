package gse.pathfinder;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {

	public static final SharedPreferences getPreference(Context context) {
		return context.getSharedPreferences("pathfinder.settings", 0);
	}

}
