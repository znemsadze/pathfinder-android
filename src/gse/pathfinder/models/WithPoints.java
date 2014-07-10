package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class WithPoints extends WithName implements Serializable {
	private static final long serialVersionUID = -4924677945096509922L;

	private List<Point> points = new ArrayList<Point>();

	public List<Point> getPoints() {
		return points;
	}
}
