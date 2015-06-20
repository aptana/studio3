package ca.mestevens.ios.xcode.parser.exceptions;

@SuppressWarnings("serial")
public class FileReferenceDoesNotExistException extends Exception {

	public FileReferenceDoesNotExistException(String message) {
		super(message);
	}
	
	public FileReferenceDoesNotExistException(Throwable cause) {
		super(cause);
	}
	
}
