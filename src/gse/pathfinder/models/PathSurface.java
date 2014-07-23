package gse.pathfinder.models;

import java.util.ArrayList;
import java.util.List;

public class PathSurface {
	private String id;
	private String name;
	private int orderBy;
	private PathType type;
	private List<PathDetail> details = new ArrayList<PathDetail>();

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

	public PathType getType() {
		return type;
	}

	public void setType(PathType type) {
		this.type = type;
	}

	public List<PathDetail> getDetails() {
		return details;
	}
}
