package nz.ac.auckland.model;

/**
 * 
 * An Exception class to be trown as a form of sending messages to the GUI.
 * Should be constructed with a message which will most likely be shown to the
 * user of the GUI in a message box
 * 
 * @author Fraser
 *
 */
public class VidevoxException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5604850489462019693L;

	/**
	 * Default constructor with no message
	 */
	public VidevoxException() {
	}

	/**
	 * Specify a message, will most likely be shown to a user.
	 * 
	 * @param message
	 */
	public VidevoxException(String message) {
		super(message);
	}

}
