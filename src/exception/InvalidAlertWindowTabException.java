package exception;

public class InvalidAlertWindowTabException extends Exception {
	
	private static final long serialVersionUID = 3959386582010099686L;

	public InvalidAlertWindowTabException() {
		super();
	}
	
	public InvalidAlertWindowTabException(String message) {
		super(message);
	}
	
	public InvalidAlertWindowTabException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidAlertWindowTabException(Throwable cause) {
		super(cause);
	}
	
}
