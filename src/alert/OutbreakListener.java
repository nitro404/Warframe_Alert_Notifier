package alert;

public interface OutbreakListener {
	
	public void notifyNewOutbreak(Outbreak b);
	
	public void notifyFilteredOutbreak(Outbreak b);
	
}
