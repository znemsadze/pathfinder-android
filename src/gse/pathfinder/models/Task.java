package gse.pathfinder.models;

import gse.pathfinder.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task implements Serializable {
	private static final long	serialVersionUID	= 8906487151614225794L;

	public static final int	  START	           = 0;
	public static final int	  CANCELED	       = 1;
	public static final int	  IN_PROGRESS	     = 2;
	public static final int	  COMPELETED	     = 10;

	private String	          id;
	private int	              number;
	private String	          note;
	private int	              status	         = START;
	private Date	            createdAt;
	private User	            assignee;
	private List<Path>	      paths	           = new ArrayList<Path>();
	private List<WithPoint>	  destinations	   = new ArrayList<WithPoint>();
	private List<Track>	      tracks	         = new ArrayList<Track>();

	public boolean isStart() {
		return status == START;
	}

	public boolean isCanceled() {
		return status == CANCELED;
	}

	public boolean isInProgress() {
		return status == IN_PROGRESS;
	}

	public boolean isCompleted() {
		return status == COMPELETED;
	}

	public boolean canBegin() {
		return isStart();
	}

	public boolean canCancel() {
		return isStart() || isInProgress();
	}

	public boolean canComplete() {
		return isInProgress();
	}

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

	public List<Path> getPaths() {
		return paths;
	}

	public List<WithPoint> getDestinations() {
		return destinations;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public int getStatusImage() {
		switch (this.getStatus()) {
		case COMPELETED:
			return R.drawable.status_completed;
		case IN_PROGRESS:
			return R.drawable.status_in_progress;
		case CANCELED:
			return R.drawable.status_canceled;
		default:
			return R.drawable.status_start;
		}
	}
}
