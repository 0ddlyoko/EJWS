package me.oddlyoko.ejws.base.events;

import me.oddlyoko.ejws.event.Event;
import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;

public class ModuleEvent extends Event {
    private final TheModule<Module> theModule;

    @SuppressWarnings("unchecked")
    public ModuleEvent(TheModule<? extends Module> theModule) {
        this.theModule = (TheModule<Module>) theModule;
    }

    public TheModule<Module> getTheModule() {
        return theModule;
    }
}
