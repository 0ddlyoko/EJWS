package me.oddlyoko.ejws.base.exceptions;

import java.util.Optional;
import me.oddlyoko.ejws.module.TheModule;

/**
 * Thrown when there is an exception about a {@link me.oddlyoko.ejws.module.Module}
 */
public class ModuleException extends Exception {
    private static final long serialVersionUID = -2038829587724342133L;

    private TheModule<?> theModule;

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleException(Throwable cause) {
        super(cause);
    }

    public ModuleException(TheModule<?> theModule, String message) {
        super(message);
        this.theModule = theModule;
    }

    public ModuleException(TheModule<?> theModule, String message, Throwable cause) {
        super(message, cause);
        this.theModule = theModule;
    }

    public ModuleException(TheModule<?> theModule, Throwable cause) {
        super(cause);
        this.theModule = theModule;
    }

    public Optional<TheModule<?>> getTheModule() {
        return Optional.ofNullable(theModule);
    }
}
