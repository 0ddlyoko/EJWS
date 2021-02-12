package me.oddlyoko.ejws.event;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.oddlyoko.ejws.module.TheModule;

/**
 * List of {@link EventHandler} that will be called when a specific {@link Event} is published
 *
 * @param <E> The event
 */
public class HandlerList<E extends Event> {
    private static final Logger LOGGER = LogManager.getLogger(HandlerList.class);
    private final TheModule<?> theModule;
    private final EnumMap<Priority, Set<EventHandler<E>>> listeners;

    public HandlerList(TheModule<?> theModule) {
        this.theModule = theModule;
        this.listeners = new EnumMap<>(Priority.class);
        for (Priority p : Priority.values())
            listeners.put(p, new LinkedHashSet<>());
    }

    /**
     * Execute an action when given event is performed
     *
     * @param priority     The priority of the event
     * @param eventHandler The action to execute
     */
    public void subscribe(Priority priority, EventHandler<E> eventHandler) {
        Set<EventHandler<E>> eventHandlers = listeners.get(priority);
        eventHandlers.add(eventHandler);
    }

    /**
     * Unsubscribe specific EventHandler
     *
     * @param eventHandler The action to unsubscribe
     */
    public void unsubscribe(EventHandler<E> eventHandler) {
        listeners.values().forEach(eventHandlers -> eventHandlers.remove(eventHandler));
    }

    /**
     * Unsubscribe all registered event
     */
    public void unsubscribeAll() {
        listeners.values().forEach(Set::clear);
    }

    /**
     * Run an event
     *
     * @param event The event to run
     */
    public void publish(E event) {
        listeners.values().forEach(eventHandlers -> eventHandlers.forEach(eventHandler -> {
            try {
                eventHandler.execute(event);
            } catch (Exception ex) {
                LOGGER.error("", ex);
            }
        }));
    }

    public TheModule<?> getTheModule() {
        return theModule;
    }
}
