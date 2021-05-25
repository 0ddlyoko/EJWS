package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.event.Event;
import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

public class ModuleEvent<E extends Module> extends Event {
    private final TheModule<E> theModule;

    public ModuleEvent(TheModule<E> theModule) {
        this.theModule = theModule;
    }

    public TheModule<E> getTheModule() {
        return theModule;
    }
}
