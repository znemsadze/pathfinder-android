package gse.pathfinder.models;

import java.io.Serializable;

public abstract class WithName implements Serializable {
	private static final long serialVersionUID = 4435640774760363150L;

	public static final String OFFICE = "Objects::Office";
	public static final String TOWER = "Objects::Tower";
	public static final String SUBSTATION = "Objects::Substation";
	public static final String LINE = "Objects::Line";
	public static final String PATH = "Objects::Path::Line";

	private String id;
	private String name;
	private String description;
	private String region;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public abstract int getImage();
}
