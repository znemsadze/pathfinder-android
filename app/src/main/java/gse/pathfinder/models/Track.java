package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Track implements Serializable {
	private static final long	serialVersionUID	= 1634976051262387637L;

	private String	          id;
	private boolean	          open;
	private List<Point>	      points	         = new ArrayList<Point>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public List<Point> getPoints() {
		return points;
	}
}
