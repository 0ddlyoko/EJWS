package me.oddlyoko.ejws.base.exceptions;

/**
 * Throws when the Java {@link Module} hasn't been found in the module
 */
public class ModuleNotFoundException extends ModuleLoadException {
    private static final long serialVersionUID = 3624328805288320278L;

    public ModuleNotFoundException(String message) {
        super(message);
    }

    public ModuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotFoundException(Throwable cause) {
        super(cause);
    }
}
