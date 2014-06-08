package exception;

public class MissingDataException extends Exception {
	
	private static final long serialVersionUID = 3959386582010099686L;

	public MissingDataException() {
		super();
	}
	
	public MissingDataException(String message) {
		super(message);
	}
	
	public MissingDataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MissingDataException(Throwable cause) {
		super(cause);
	}
	
}
