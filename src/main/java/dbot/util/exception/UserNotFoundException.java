package dbot.util.exception;

/**
 * Created by Niklas on 16.03.2017.
 */
public class UserNotFoundException extends Exception {
	public UserNotFoundException() {}

	public UserNotFoundException(String message) {
		super(message);
	}
}
