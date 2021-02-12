package me.oddlyoko.ejws.events;

import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

/**
 * Event called when a module is unloading<br />
 * This event is called before {@link Module#onDisable()} so please be sure that you are unregistering links from this class
 * @param <E>
 */
public class ModuleUnloadEvent<E extends Module> extends ModuleEvent<E> {

    public ModuleUnloadEvent(TheModule<E> theModule) {
        super(theModule);
    }
}
