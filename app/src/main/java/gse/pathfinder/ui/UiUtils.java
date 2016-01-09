package gse.pathfinder.ui;

import android.widget.TextView;

public class UiUtils {
	public static void showText(TextView view, String text) {
		if (text == null || text.trim().isEmpty()) {
			view.setText("");
		} else {
			view.setText(text);
		}
	}
}
