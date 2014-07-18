package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Serializable {
	private static final long serialVersionUID = -4357635171293624439L;
	private double lat;
	private double lng;
	private Double easting;
	private Double northing;

	public Point(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	private void initUTM() {
		double[] coords = new LatLon2UTM().convertLatLonToUTM(lat, lng);
		this.easting = coords[0];
		this.northing = coords[1];
	}

	public double getEasting() {
		if (easting == null) initUTM();
		return easting;
	}

	public double getNorthing() {
		if (northing == null) initUTM();
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
			if (i > 0) b.append(",");
			b.append(p.getLat());
			b.append(",");
			b.append(p.getLng());
		}
		return b.toString();
	}

	public static List<Point> fromText(String text) {
		List<Point> points = new ArrayList<Point>();
		String[] coords = text.split(",");
		for (int i = 0; i < coords.length / 2; i++) {
			double lat = Double.parseDouble(coords[2 * i]);
			double lng = Double.parseDouble(coords[2 * i + 1]);
			points.add(new Point(lat, lng));
		}
		return points;
	}
}
