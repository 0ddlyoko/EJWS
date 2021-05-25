package me.oddlyoko.ejws.base.exceptions;

public class InvalidModuleDescriptorException extends ModuleException {
    private static final long serialVersionUID = -6550509243599132287L;

    public InvalidModuleDescriptorException() {
    }

    public InvalidModuleDescriptorException(String message) {
        super(message);
    }

    public InvalidModuleDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModuleDescriptorException(Throwable cause) {
        super(cause);
    }

    public InvalidModuleDescriptorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
