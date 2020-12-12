package me.oddlyoko.ejws.exceptions;

public class ComputeException extends Exception {
	private static final long serialVersionUID = 7066397098548828889L;

	public ComputeException() {
		super();
	}

	public ComputeException(String message) {
		super(message);
	}

	public ComputeException(Throwable ex) {
		super(ex);
	}

	public ComputeException(String message, Throwable ex) {
		super(message, ex);
	}

}
