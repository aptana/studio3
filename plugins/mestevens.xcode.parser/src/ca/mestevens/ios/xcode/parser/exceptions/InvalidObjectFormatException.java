package ca.mestevens.ios.xcode.parser.exceptions;

@SuppressWarnings("serial")
public class InvalidObjectFormatException extends Exception {

	public InvalidObjectFormatException(String message) {
		super(message);
	}
	
	public InvalidObjectFormatException(Throwable cause) {
		super(cause);
	}
	
}
