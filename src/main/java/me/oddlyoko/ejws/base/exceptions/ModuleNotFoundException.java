package me.oddlyoko.ejws.base.exceptions;

public class ModuleNotFoundException extends ModuleLoadException {
    private static final long serialVersionUID = 3624328805288320278L;

    public ModuleNotFoundException() {
    }

    public ModuleNotFoundException(String message) {
        super(message);
    }

    public ModuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModuleNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
