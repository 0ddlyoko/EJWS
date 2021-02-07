package me.oddlyoko.ejws.event;

/**
 * Event class<br />
 * Extend this class if you want to create your own event system
 */
public abstract class Event {

    /**
     * Retrieve the name of this event<br />
     * Default name is the class name
     *
     * @return The name of this event
     */
    public String getName() {
        return getClass().getSimpleName();
    }
}
