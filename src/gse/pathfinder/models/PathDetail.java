package gse.pathfinder.models;

public class PathDetail {
	private String id;
	private String name;
	private int orderBy;
	private PathSurface surface;

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

	public PathSurface getSurface() {
		return surface;
	}

	public void setSurface(PathSurface surface) {
		this.surface = surface;
	}
}
