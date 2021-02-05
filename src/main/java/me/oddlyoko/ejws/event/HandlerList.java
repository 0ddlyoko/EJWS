package me.oddlyoko.ejws.event;

public class HandlerList<E extends Event> {
    private final Class<E> clazz;

    public HandlerList(Class<E> clazz) {
        this.clazz = clazz;
    }

    public long subscribe(EventHandler<E> eventHandler) {
        return 0;
    }

    public boolean unsubscribe(long id) {
        return true;
    }

    public void unsubscribeAll() {

    }

    public void publish(E event) {

    }
}
