package gse.pathfinder.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class WithPoint implements Serializable {
	private static final long	 serialVersionUID	= -910880838379409520L;
	public static final String	OFFICE	        = "Objects::Office";
	public static final String	TOWER	          = "Objects::Tower";
	public static final String	SUBSTATION	    = "Objects::Substation";

	private String	           id;
	private Point	             point;
	private String	           name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
