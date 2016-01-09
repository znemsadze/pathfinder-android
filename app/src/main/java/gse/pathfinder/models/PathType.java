package gse.pathfinder.models;

import java.util.ArrayList;
import java.util.List;

public class PathType {
	private String id;
	private String name;
	private int orderBy;
	private List<PathSurface> surfaces = new ArrayList<PathSurface>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public List<PathSurface> getSurfaces() {
		return surfaces;
	}

	@Override
	public String toString() {
		return name;
	}
}
