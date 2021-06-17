package me.oddlyoko.ejws.base.exceptions;

public class ModuleException extends Exception {
    private static final long serialVersionUID = -2038829587724342133L;

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleException(Throwable cause) {
        super(cause);
    }
}
