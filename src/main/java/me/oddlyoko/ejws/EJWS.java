package me.oddlyoko.ejws;

import me.oddlyoko.ejws.event.EventManager;

public final class EJWS {
    private static final EJWS ejws = new EJWS();

    private final EventManager eventManager;

    private EJWS() {
        eventManager = new EventManager();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public static EJWS get() {
        return ejws;
    }
}
