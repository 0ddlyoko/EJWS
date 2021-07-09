package me.oddlyoko.ejws.base;

import java.util.List;
import me.oddlyoko.ejws.base.events.ModuleLoadEvent;
import me.oddlyoko.ejws.base.events.ModuleUnloadEvent;
import me.oddlyoko.ejws.event.Event;
import me.oddlyoko.ejws.module.Module;

public class BaseModule extends Module {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Event>> getModuleEvents() {
        return List.of(
                ModuleLoadEvent.class,
                ModuleUnloadEvent.class
        );
    }
}
