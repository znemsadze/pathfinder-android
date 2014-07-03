package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Path implements Serializable {
	private static final long	serialVersionUID	= 84295392184613424L;
	private List<Point>	      points	         = new ArrayList<Point>();

	public List<Point> getPoints() {
		return points;
	}
}
