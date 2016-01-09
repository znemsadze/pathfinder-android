package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;

public class Substation extends WithPoint implements Serializable {
	private static final long	serialVersionUID	= 2436406724856310088L;

	@Override
	public int getMarkerImage() {
		return R.drawable.substation;
	}
}
