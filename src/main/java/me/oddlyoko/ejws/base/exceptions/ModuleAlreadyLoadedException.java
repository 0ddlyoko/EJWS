package me.oddlyoko.ejws.base.exceptions;

/**
 * Throws when you try to load a module that is already loaded or where the name of the module is already loaded
 */
public class ModuleAlreadyLoadedException extends ModuleLoadException {
    private static final long serialVersionUID = -2507344705629178412L;

    public ModuleAlreadyLoadedException(String message) {
        super(message);
    }
}
