package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;

public class Tower extends WithPoint implements Serializable {
	private static final long	serialVersionUID	= -6038603914193828143L;

	@Override
	public int getImage() {
		return R.drawable.tower;
	}
}
