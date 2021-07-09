package me.oddlyoko.ejws.base.exceptions;

import me.oddlyoko.ejws.module.TheModule;

/**
 * Throws when an exception occurs when trying to load a {@link me.oddlyoko.ejws.module.Module}
 */
public class ModuleLoadException extends ModuleException {
    private static final long serialVersionUID = -1649294974173723846L;
    private static final String ERROR_MESSAGE = "Error while loading module %s";

    public ModuleLoadException(String message) {
        super(message);
    }

    public ModuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleLoadException(Throwable cause) {
        super(cause);
    }

    public ModuleLoadException(TheModule<?> module) {
        super(module, String.format(ERROR_MESSAGE, module.getName()));
    }

    public ModuleLoadException(TheModule<?> module, String message) {
        super(module, String.format(ERROR_MESSAGE + ": %s", module.getName(), message));
    }

    public ModuleLoadException(TheModule<?> module, String message, Throwable cause) {
        super(module, String.format(ERROR_MESSAGE + ": %s", module.getName(), message), cause);
    }

    public ModuleLoadException(TheModule<?> module, Throwable cause) {
        super(module, String.format(ERROR_MESSAGE, module.getName()), cause);
    }
}
