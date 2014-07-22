package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Serializable {
	private static final long serialVersionUID = -4357635171293624439L;
	private double lat;
	private double lng;
	private double easting;
	private double northing;

	public Point(double lat, double lng) {
		this(lat, lng, 0, 0);
	}

	public Point(double lat, double lng, double easting, double northing) {
		this.lat = lat;
		this.lng = lng;
		this.easting = easting;
		this.northing = northing;
	}

	public double getEasting() {
		return easting;
	}

	public double getNorthing() {
		return northing;
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

	public static String asText(List<Point> points) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			if (i > 0) b.append(":");
			b.append(p.getLat());
			b.append(",");
			b.append(p.getLng());
			b.append(",");
			b.append(p.getEasting());
			b.append(",");
			b.append(p.getNorthing());
		}
		return b.toString();
	}

	public static List<Point> fromText(String text) {
		List<Point> points = new ArrayList<Point>();
		String[] coords = text.split(":");
		for (int i = 0; i < coords.length; i++) {
			String[] pointCoords = coords[i].split(",");
			double lat = Double.parseDouble(pointCoords[0]);
			double lng = Double.parseDouble(pointCoords[1]);
			double easting = Double.parseDouble(pointCoords[2]);
			double northing = Double.parseDouble(pointCoords[3]);
			Point p = new Point(lat, lng, easting, northing);
			points.add(p);
		}
		return points;
	}
}
