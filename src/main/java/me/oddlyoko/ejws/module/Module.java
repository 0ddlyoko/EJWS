package me.oddlyoko.ejws.module;

import java.util.List;
import me.oddlyoko.ejws.event.Event;

public abstract class Module {

    /**
     * Called when the module is enabling<br />
     * Here, you can register events that are required to make the module what it should do
     */
    public abstract void onEnable();

    /**
     * Called when the module is disabling<br />
     * Used to unregister registered events<br />
     * !!! onDisable() is called even if onEnable() throws an exception !!!
     */
    public abstract void onDisable();

    /**
     * Retrieves events which are defined in this module
     *
     * @return A list of events which are defined in this module
     */
    public abstract List<Class<? extends Event>> getModuleEvents();
}
