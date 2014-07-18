package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tower extends WithPoint implements Serializable {
	private static final long serialVersionUID = -6038603914193828143L;

	private String category;
	private String linename;
	private List<String> images = new ArrayList<String>();

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
	public int getMarkerImage() {
		return R.drawable.tower;
	}

	public List<String> getImages() {
		return images;
	}

	public String imagesAsString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < images.size(); i++) {
			if (i > 0) b.append(";");
			b.append(images.get(i));
		}
		return b.toString();
	}

	public void imagesFromString(String images) {
		this.images.clear();
		for (String image : images.split(";")) {
			if (null != image && !image.trim().isEmpty()) this.images.add(image);
		}
	}
}
