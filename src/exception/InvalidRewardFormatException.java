package exception;

public class InvalidRewardFormatException extends Exception {
	
	private static final long serialVersionUID = -6617261886624661861L;

	public InvalidRewardFormatException() {
		super();
	}
	
	public InvalidRewardFormatException(String message) {
		super(message);
	}
	
	public InvalidRewardFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidRewardFormatException(Throwable cause) {
		super(cause);
	}
	
}
