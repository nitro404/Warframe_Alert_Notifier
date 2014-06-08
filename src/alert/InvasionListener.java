package alert;

public interface InvasionListener {
	
	public void notifyNewInvasion(Invasion v);
	
	public void notifyFilteredInvasion(Invasion v);
	
}
