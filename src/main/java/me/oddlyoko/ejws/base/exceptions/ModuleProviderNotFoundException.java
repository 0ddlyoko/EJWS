package me.oddlyoko.ejws.base.exceptions;

/**
 * Thrown when the {@link me.oddlyoko.ejws.module.Module} hasn't been found
 */
public class ModuleProviderNotFoundException extends ModuleLoadException {
    private static final long serialVersionUID = 1607274298758150668L;

    public ModuleProviderNotFoundException(String message) {
        super(message);
    }

    public ModuleProviderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleProviderNotFoundException(Throwable cause) {
        super(cause);
    }
}
