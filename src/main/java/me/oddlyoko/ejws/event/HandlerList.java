package me.oddlyoko.ejws.event;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class HandlerList<E extends Event> {
    private final EnumMap<Priority, List<EventHandler<E>>> listeners;

    public HandlerList() {
        this.listeners = new EnumMap<>(Priority.class);
        for (Priority p : Priority.values())
            listeners.put(p, new ArrayList<>());
    }

    public void subscribe(Priority p, EventHandler<E> eventHandler) {
        List<EventHandler<E>> eventHandlers = listeners.get(p);
        if (eventHandlers.contains(eventHandler))
            throw new IllegalArgumentException("Given EventHandler is already listening !");
        eventHandlers.add(eventHandler);
    }

    public void unsubscribe(EventHandler<E> eventHandler) {
        listeners.values().forEach(eventHandlers -> eventHandlers.remove(eventHandler));
    }

    /**
     * Unsubscribe all the registered event
     */
    public void unsubscribeAll() {
        listeners.values().forEach(List::clear);
    }

    public void publish(E event) {
        listeners.values().forEach(eventHandlers -> eventHandlers.forEach(eventHandler -> eventHandler.execute(event)));
    }
}
