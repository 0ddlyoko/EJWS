package me.oddlyoko.example;

import java.util.Collections;
import java.util.List;

import me.oddlyoko.ejws.event.Event;
import me.oddlyoko.ejws.module.Module;

public class Example extends Module {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Event>> getModuleEvents() {
        return Collections.emptyList();
    }
}
