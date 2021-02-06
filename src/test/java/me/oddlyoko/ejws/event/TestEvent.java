package me.oddlyoko.ejws.event;

import me.oddlyoko.ejws.event.src.JoinEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// TODO Add more test
public class TestEvent {

    @Test
    @DisplayName("Test system")
    void test() {
        Events.subscribe(JoinEvent.class, event -> {
            System.out.println("TestEvent.test");
        });

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);
    }
}
