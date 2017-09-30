package orm.exception;

public class BedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6286617147151957204L;
	
	public BedException(Exception exception) {
		super(exception);
	}

}
