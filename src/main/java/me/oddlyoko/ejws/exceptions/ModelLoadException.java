package me.oddlyoko.ejws.exceptions;

public class ModelLoadException extends Exception {
	private static final long serialVersionUID = -513467885647017011L;

	public ModelLoadException() {
		super();
	}

	public ModelLoadException(String message) {
		super(message);
	}

	public ModelLoadException(Throwable ex) {
		super(ex);
	}

	public ModelLoadException(String message, Throwable ex) {
		super(message, ex);
	}
}
