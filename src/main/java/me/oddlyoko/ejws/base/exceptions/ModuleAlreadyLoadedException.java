package me.oddlyoko.ejws.base.exceptions;

public class ModuleAlreadyLoadedException extends ModuleLoadException {
    private static final long serialVersionUID = -2507344705629178412L;

    public ModuleAlreadyLoadedException() {
    }

    public ModuleAlreadyLoadedException(String message) {
        super(message);
    }

    public ModuleAlreadyLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleAlreadyLoadedException(Throwable cause) {
        super(cause);
    }

    public ModuleAlreadyLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
