package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

/**
 * Event called when a module is unloading<br />
 * This event is called before {@link Module#onDisable()} so please be sure that you are unregistering links from this class
 */
public class ModuleUnloadEvent extends ModuleEvent {

    public ModuleUnloadEvent(TheModule<? extends Module> theModule) {
        super(theModule);
    }
}
