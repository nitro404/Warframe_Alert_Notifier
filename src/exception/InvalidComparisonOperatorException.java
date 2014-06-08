package exception;

public class InvalidComparisonOperatorException extends Exception {
	
	private static final long serialVersionUID = 143685958134627198L;

	public InvalidComparisonOperatorException() {
		super();
	}
	
	public InvalidComparisonOperatorException(String message) {
		super(message);
	}
	
	public InvalidComparisonOperatorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidComparisonOperatorException(Throwable cause) {
		super(cause);
	}
	
}
