package gse.pathfinder.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class WithPoint extends WithName implements Serializable {
	private static final long serialVersionUID = -910880838379409520L;

	private Point point;

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public static WithPoint fromJson(JSONObject json) throws JSONException {
		WithPoint withPoint = null;
		String type = json.getString("type");
		if (OFFICE.equals(type)) withPoint = new Office();
		else if (TOWER.equals(type)) withPoint = new Tower();
		else if (SUBSTATION.equals(type)) withPoint = new Substation();
		else throw new IllegalArgumentException("Unknow WithPoint type: " + type);
		withPoint.setId(json.getString("id"));
		withPoint.setName(json.getString("name"));
		withPoint.setPoint(new Point(json.getDouble("lat"), json.getDouble("lng")));
		return withPoint;
	}
}
