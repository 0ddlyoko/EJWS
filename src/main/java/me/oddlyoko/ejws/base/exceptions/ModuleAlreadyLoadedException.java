package me.oddlyoko.ejws.base.exceptions;

public class ModuleAlreadyLoadedException extends ModuleLoadException {
    private static final long serialVersionUID = -2507344705629178412L;

    public ModuleAlreadyLoadedException(String message) {
        super(message);
    }
}
