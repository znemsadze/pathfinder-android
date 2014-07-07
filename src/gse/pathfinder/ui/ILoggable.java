package gse.pathfinder.ui;

public interface ILoggable {
	public void error(Exception ex);
	public void error(String msg);
	public void warning(String msg);
}
