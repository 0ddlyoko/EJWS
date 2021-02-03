package me.oddlyoko.ejws.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class EventManager {
    private final Map<Long, Event> events;
    private final AtomicLong value;

    public EventManager() {
        events = new HashMap<>();
        value = new AtomicLong();
        registerEvents(Event.class, e -> {

        });
    }

    public <E extends Event> long registerEvents(Class<E> clazz, Consumer<E> consumer) {
        long value = nextValue();
        return value;
    }

    public synchronized long nextValue() {
        return value.incrementAndGet();
    }
}
