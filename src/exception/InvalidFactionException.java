package exception;

public class InvalidFactionException extends Exception {
	
	private static final long serialVersionUID = 258045968540914721L;

	public InvalidFactionException() {
		super();
	}
	
	public InvalidFactionException(String message) {
		super(message);
	}
	
	public InvalidFactionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidFactionException(Throwable cause) {
		super(cause);
	}
	
}
