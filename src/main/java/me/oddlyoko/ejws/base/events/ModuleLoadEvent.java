package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

/**
 * Event called when a module is loading<br />
 * This event is called after {@link Module#onEnable()}
 */
public class ModuleLoadEvent extends ModuleEvent {

    public ModuleLoadEvent(TheModule<? extends Module> theModule) {
        super(theModule);
    }
}
