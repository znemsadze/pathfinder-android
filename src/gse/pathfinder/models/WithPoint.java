package gse.pathfinder.models;

public class WithPoint {
	public static final String	OFFICE	   = "Objects::Office";
	public static final String	TOWER	     = "Objects::Tower";
	public static final String	SUBSTATION	= "Objects::Substation";

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
}
