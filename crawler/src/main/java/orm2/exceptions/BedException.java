package orm2.exceptions;

public class BedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7341337946244868509L;
	
	public BedException(String message) {
		super(message);
	}
	
	public BedException (Exception exception) {
		super(exception);
	}

}
