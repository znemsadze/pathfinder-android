package gse.pathfinder.models;

import java.util.List;

/**
 * Created by zviad on 1/9/16.
 */
public class PathLines  {

    private String color;
    private String lineTypeName;
    private String surficeName;
    private List<Point> points;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public String getSurficeName() {
        return surficeName;
    }

    public void setSurficeName(String surficeName) {
        this.surficeName = surficeName;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
