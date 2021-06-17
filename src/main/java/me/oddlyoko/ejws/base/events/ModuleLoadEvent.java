package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

public class ModuleLoadEvent extends ModuleEvent {

    public ModuleLoadEvent(TheModule<? extends Module> theModule) {
        super(theModule);
    }
}
