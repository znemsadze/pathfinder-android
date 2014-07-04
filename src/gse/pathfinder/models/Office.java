package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;

public class Office extends WithPoint implements Serializable {
	private static final long	serialVersionUID	= -5452401583863798767L;

	@Override
	public int getImage() {
		return R.drawable.office;
	}
}
