package me.oddlyoko.ejws.base.exceptions;

public class ModuleLoadException extends ModuleException {
    private static final long serialVersionUID = -1649294974173723846L;

    public ModuleLoadException(String message) {
        super(message);
    }

    public ModuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleLoadException(Throwable cause) {
        super(cause);
    }
}
