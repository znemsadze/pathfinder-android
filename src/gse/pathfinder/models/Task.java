package gse.pathfinder.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {
	public static final int	START	       = 0;
	public static final int	CANCELED	   = 1;
	public static final int	IN_PROGRESS	 = 2;
	public static final int	COMPELETED	 = 10;

	private String	        id;
	private int	            number;
	private String	        note;
	private int	            status	     = START;
	private Date	          createdAt;
	private User	          assignee;
	private List<Point>	    points	     = new ArrayList<Point>();
	private List<WithPoint>	destinations	= new ArrayList<WithPoint>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<WithPoint> getDestinations() {
		return destinations;
	}
}
