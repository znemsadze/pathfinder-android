package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;

public class Tower extends WithPoint implements Serializable {
	private static final long serialVersionUID = -6038603914193828143L;

	private String category;
	private String linename;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLinename() {
		return linename;
	}

	public void setLinename(String linename) {
		this.linename = linename;
	}

	@Override
	public int getImage() {
		return R.drawable.tower;
	}
}
