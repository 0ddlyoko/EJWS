package me.oddlyoko.ejws.event;

/**
 * Priorities for events<br />
 * Add priority to specific events to run these events before or after the other ones
 */
public enum Priority {
    LOWEST(1),
    LOWER(2),
    NORMAL(3),
    HIGH(4),
    HIGHER(5);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
