package alert;

public interface AlertListener {
	
	public void notifyNewAlert(Alert a);
	
	public void notifyFilteredAlert(Alert a);
	
}
