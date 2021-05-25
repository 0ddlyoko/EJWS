package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

public class ModuleLoadEvent<E extends Module> extends ModuleEvent<E> {

    public ModuleLoadEvent(TheModule<E> theModule) {
        super(theModule);
    }
}
