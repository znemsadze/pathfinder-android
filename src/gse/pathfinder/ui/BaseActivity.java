package gse.pathfinder.ui;

import android.app.Activity;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements ILoggable {
	@Override
	public void error(String errorMessage) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void error(Exception ex) {
		ex.printStackTrace();
		String msg = ex.getMessage();
		if (null == msg || msg.isEmpty()) msg = ex.toString();
		error(msg);
	}
}
