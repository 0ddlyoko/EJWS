package me.oddlyoko.ejws.exceptions;

public class ModuleLoadException extends ModuleException {
    private static final long serialVersionUID = -1649294974173723846L;

    public ModuleLoadException() {
    }

    public ModuleLoadException(String message) {
        super(message);
    }

    public ModuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleLoadException(Throwable cause) {
        super(cause);
    }

    public ModuleLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
