package me.oddlyoko.ejws.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.oddlyoko.ejws.module.TheModule;

/**
 * The Event Manager class<br />
 * Use this class to subscribe / unsubscribe to an event or to publish an event
 */
public final class Events {
    private static final Map<Class<? extends Event>, HandlerList<? extends Event>> events = new HashMap<>();

    private Events() {
    }

    /**
     * Retrieves the {@link HandlerList} associated to the specific event
     *
     * @param clazz The event class
     * @param <E>   The event class
     * @return The {@link Optional} {@link HandlerList} associated with the given event class
     */
    @SuppressWarnings("unchecked")
    public static <E extends Event> Optional<HandlerList<E>> getHandlerList(Class<E> clazz) {
        return Optional.ofNullable((HandlerList<E>) events.get(clazz));
    }

    public static <E extends Event> void registerEventModule(Class<E> eventClass, TheModule<?> theModule) {
        HandlerList<E> handlerList = new HandlerList<>(theModule);
        events.put(eventClass, handlerList);
    }

    public static <E extends Event> void unregisterEventModule(Class<E> eventClass) {
        // We just have to remove the specific handler from the Map
        events.remove(eventClass);
    }

    /**
     * Retrieves the number of registered events
     *
     * @return The number of registered events
     */
    public static int countEvent() {
        return events.size();
    }

    /**
     * Execute an action when given event is performed<br />
     * Subscribe the action as default priority of {@link Priority#NORMAL}<br />
     * Same as <pre>Events.subscribe(clazz, Priority.NORMAL, eventHandler);</pre>
     *
     * @param clazz        The given event class
     * @param eventHandler The action to execute
     * @param <E>          The generic Event class
     */
    public static <E extends Event> void subscribe(Class<E> clazz, EventHandler<E> eventHandler) {
        subscribe(clazz, Priority.NORMAL, eventHandler);
    }

    /**
     * Execute an action when given event is performed
     *
     * @param clazz        The given event class
     * @param priority     The priority of the event
     * @param eventHandler The action to execute
     * @param <E>          The generic Event class
     * @see Priority
     */
    public static <E extends Event> void subscribe(Class<E> clazz, Priority priority, EventHandler<E> eventHandler) {
        getHandlerList(clazz).orElseThrow(() -> new IllegalStateException(
                String.format("Trying to subscribe an event that has never been initialized ! (%s)", clazz.getSimpleName())
        )).subscribe(priority, eventHandler);
    }

    /**
     * Unsubscribe specific EventHandler
     *
     * @param clazz        The event class
     * @param eventHandler The action to unsubscribe
     * @param <E>          The generic Event class
     */
    public static <E extends Event> void unsubscribe(Class<E> clazz, EventHandler<E> eventHandler) {
        getHandlerList(clazz).ifPresent(handler -> handler.unsubscribe(eventHandler));
    }

    /**
     * Unsubscribe all listener to given class
     *
     * @param clazz The class to unsubscribe
     * @param <E>   The generic Event class
     */
    public static <E extends Event> void unsubscribe(Class<E> clazz) {
        getHandlerList(clazz).ifPresent(HandlerList::unsubscribeAll);
    }

    /**
     * Run an event
     *
     * @param event The event to run
     * @param <E>   The generic Event class
     */
    @SuppressWarnings("unchecked")
    public static <E extends Event> void publish(E event) {
        Class<E> clazz = (Class<E>) event.getClass();
        getHandlerList(clazz).orElseThrow(() -> new IllegalStateException(
                String.format("Trying to public an event that has never been initialized ! (%s)", clazz.getSimpleName())
        )).publish(event);
    }
}
