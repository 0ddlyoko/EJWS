package me.oddlyoko.ejws.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Events {
    private static final Map<Class<? extends Event>, HandlerList<? extends Event>> events = new HashMap<>();

    private Events() {}

    @SuppressWarnings("unchecked")
    public static <E extends Event> Optional<HandlerList<E>> getHandlerList(Class<E> clazz) {
        Optional<?> op = Optional.ofNullable(events.get(clazz));
        return (Optional<HandlerList<E>>) op;
    }

    /**
     * Execute an action when given event is performed
     *
     * @param clazz        The given event class
     * @param eventHandler The action to execute
     * @param <E>          The generic Event class
     */
    public static <E extends Event> void subscribe(Class<E> clazz, EventHandler<E> eventHandler) {
        subscribe(clazz, Priority.NORMAL, eventHandler);
    }

    public static <E extends Event> void subscribe(Class<E> clazz, Priority priority, EventHandler<E> eventHandler) {
        HandlerList<E> handlerList = getHandlerList(clazz).orElseGet(() -> {
            HandlerList<E> hl = new HandlerList<>();
            events.put(clazz, hl);
            return hl;
        });
        handlerList.subscribe(priority, eventHandler);
    }

    /**
     * Unsubscribe specific EventHandler
     *
     * @param clazz        The event class
     * @param eventHandler The action to unsubscribe
     * @param <E>          The generic Event class
     */
    public static <E extends Event> void unsubscribe(Class<E> clazz, EventHandler<E> eventHandler) {
        getHandlerList(clazz).ifPresent(handlerList -> handlerList.unsubscribe(eventHandler));
    }

    /**
     * Unsubscribe all listener to given class
     *
     * @param clazz The class to unsubscribe
     * @param <E> The generic Event class
     */
    public static <E extends Event> void unsubscribe(Class<E> clazz) {
        getHandlerList(clazz).ifPresent(HandlerList::unsubscribeAll);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> void publish(E event) {
        Class<E> clazz = (Class<E>) event.getClass();
        getHandlerList(clazz).ifPresent(handlerList -> handlerList.publish(event));
    }
}
