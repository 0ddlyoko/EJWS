package me.oddlyoko.ejws.event;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * List of {@link EventHandler} that will be called when a specific {@link Event} is published
 * @param <E> The event
 */
public class HandlerList<E extends Event> {
    private final EnumMap<Priority, List<EventHandler<E>>> listeners;

    public HandlerList() {
        this.listeners = new EnumMap<>(Priority.class);
        for (Priority p : Priority.values())
            listeners.put(p, new ArrayList<>());
    }

    /**
     * Execute an action when given event is performed
     *
     * @param priority     The priority of the event
     * @param eventHandler The action to execute
     */
    public void subscribe(Priority priority, EventHandler<E> eventHandler) {
        List<EventHandler<E>> eventHandlers = listeners.get(priority);
        if (eventHandlers.contains(eventHandler))
            throw new IllegalArgumentException("Given EventHandler is already listening !");
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
        listeners.values().forEach(List::clear);
    }


    /**
     * Run an event
     *
     * @param event The event to run
     */
    public void publish(E event) {
        listeners.values().forEach(eventHandlers -> eventHandlers.forEach(eventHandler -> eventHandler.execute(event)));
    }
}
