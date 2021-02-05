package me.oddlyoko.ejws.event;

@FunctionalInterface
public interface EventHandler<E extends Event> {
    void execute(E event);
}
