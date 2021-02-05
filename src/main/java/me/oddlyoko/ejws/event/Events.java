package me.oddlyoko.ejws.event;

import java.util.HashMap;
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
     * @return The id of the created listener. Used to unregister it
     */
    public static <E extends Event> long subscribe(Class<E> clazz, EventHandler<E> eventHandler) {
        HandlerList<E> handlerList = getHandlerList(clazz).orElseGet(() -> {
            HandlerList<E> hl = new HandlerList<>(clazz);
            events.put(clazz, hl);
            return hl;
        });
        return handlerList.subscribe(eventHandler);
    }

    /**
     * Unsubscribe specific id
     *
     * @param id The id of the listener
     */
    public static void unsubscribe(long id) {
        for (HandlerList<?> handlerList : events.values())
            if (handlerList.unsubscribe(id))
                return;
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
