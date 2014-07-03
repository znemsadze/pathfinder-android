package gse.pathfinder.models;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Serializable {
	private static final long	serialVersionUID	= -4357635171293624439L;
	private double	          lat;
	private double	          lng;

	public Point(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public LatLng getCoordinate() {
		return new LatLng(this.lat, this.lng);
	}
}
