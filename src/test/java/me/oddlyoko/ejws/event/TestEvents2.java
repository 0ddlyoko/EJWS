package me.oddlyoko.ejws.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;

import me.oddlyoko.ejws.event.src.JoinEvent;
import me.oddlyoko.ejws.event.src.QuitEvent;
import me.oddlyoko.ejws.module.Module;
import me.oddlyoko.ejws.module.TheModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestEvents2 {

    @AfterEach
    public void afterEach() {
        Events.unregisterEventModule(JoinEvent.class);
        Events.unregisterEventModule(QuitEvent.class);
    }

    @Test
    @DisplayName("Test getHandlerList(Class)")
    public void testGetHandlerList() {
        assertTrue(Events.getHandlerList(JoinEvent.class).isEmpty());
        assertTrue(Events.getHandlerList(QuitEvent.class).isEmpty());

        Events.registerEventModule(JoinEvent.class, null);
        Events.registerEventModule(QuitEvent.class, null);
        assertTrue(Events.getHandlerList(JoinEvent.class).isPresent());
        assertTrue(Events.getHandlerList(QuitEvent.class).isPresent());

        Events.unregisterEventModule(JoinEvent.class);
        Events.unregisterEventModule(QuitEvent.class);
        assertTrue(Events.getHandlerList(JoinEvent.class).isEmpty());
        assertTrue(Events.getHandlerList(QuitEvent.class).isEmpty());
    }

    @Test
    @DisplayName("Test NullPointerException if getHandlerList on unregister event")
    public void testGetHandlerListUnregisteredEvent(@Mock EventHandler<JoinEvent> event) {
        assertThrows(IllegalStateException.class, () -> Events.subscribe(JoinEvent.class, event));
    }

    @Test
    @DisplayName("Test IllegalStateException when publishing unregistered event")
    public void testPublishNotRegisteredEvent() {
        assertThrows(IllegalStateException.class, () -> Events.publish(new JoinEvent()));
    }

    @Test
    @DisplayName("Test Error in event does not stop other events")
    public void testErrorInEvent(@Mock EventHandler<JoinEvent> event,
                          @Mock EventHandler<JoinEvent> event1,
                          @Mock EventHandler<JoinEvent> event2,
                          @Mock EventHandler<JoinEvent> event3,
                          @Mock EventHandler<JoinEvent> event4) {
        Events.registerEventModule(JoinEvent.class, null);
        // Order: event2, event3, event, event1, event4
        Events.subscribe(JoinEvent.class, Priority.NORMAL, event);
        Events.subscribe(JoinEvent.class, Priority.HIGH, event1);
        Events.subscribe(JoinEvent.class, Priority.LOWEST, event2);
        Events.subscribe(JoinEvent.class, Priority.LOW, event3);
        Events.subscribe(JoinEvent.class, Priority.HIGHEST, event4);
        doAnswer(invocation -> {
            throw new IllegalStateException();
        }).when(event).execute(any());
        InOrder inOrder = inOrder(event, event1, event2, event3, event4);
        JoinEvent e = new JoinEvent();
        Events.publish(e);
        inOrder.verify(event2).execute(e);
        inOrder.verify(event3).execute(e);
        inOrder.verify(event).execute(e);
        inOrder.verify(event1).execute(e);
        inOrder.verify(event4).execute(e);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test GetTheModule()")
    public void testGetTheModule(@Mock TheModule<Module> theModule) {
        HandlerList<JoinEvent> handlerList = new HandlerList<>(theModule);
        assertEquals(theModule, handlerList.getTheModule());
    }
}
