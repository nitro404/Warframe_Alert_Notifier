package exception;

public class InvalidFilterException extends Exception {
	
	private static final long serialVersionUID = -3880093857098571510L;

	public InvalidFilterException() {
		super();
	}
	
	public InvalidFilterException(String message) {
		super(message);
	}
	
	public InvalidFilterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidFilterException(Throwable cause) {
		super(cause);
	}
	
}
