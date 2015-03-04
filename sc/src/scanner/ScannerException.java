package scanner;

/**
 * Exception class for the scanner to throw.
 * @author tuvialerea
 *
 */
public class ScannerException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -294879877967068625L;

	/**
	 * Constructor uses RuntimeException super constructor.
	 * @param msg the message to send with the exception
	 */
	public ScannerException(String msg) {
		super(msg);
	}
}
