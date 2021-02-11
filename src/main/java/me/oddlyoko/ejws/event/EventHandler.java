package me.oddlyoko.ejws.event;

/**
 * Execute an action when specific event is published
 *
 * @param <E>
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {

    /**
     * The action to execute when previously registered event is published
     *
     * @param event The action to execute
     */
    void execute(E event);
}
