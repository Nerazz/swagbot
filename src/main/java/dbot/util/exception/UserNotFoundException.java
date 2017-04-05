package dbot.util.exception;

/**
 * exception if an accessed user wasn't found
 *
 * @author Niklas Zd
 * @since 16.03.2017
 */
public class UserNotFoundException extends Exception {
	public UserNotFoundException() {}

	public UserNotFoundException(String message) {
		super(message);
	}
}
