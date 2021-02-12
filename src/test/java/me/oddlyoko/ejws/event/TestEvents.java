package me.oddlyoko.ejws.event;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.oddlyoko.ejws.event.src.JoinEvent;
import me.oddlyoko.ejws.event.src.QuitEvent;

@ExtendWith(MockitoExtension.class)
public class TestEvents {

    @BeforeAll
    static void initializeEvents() {
        Events.registerEventModule(JoinEvent.class, null);
        Events.registerEventModule(QuitEvent.class, null);
    }

    @AfterAll
    static void deInitializeEvents() {
        Events.unregisterEventModule(JoinEvent.class);
        Events.unregisterEventModule(QuitEvent.class);
    }

    @AfterEach
    void clearEvents() {
        Events.unsubscribe(JoinEvent.class);
        Events.unsubscribe(QuitEvent.class);
    }

    @Test
    @DisplayName("Test Subscribe Single Event")
    void testSubscribeSingleEvent(@Mock EventHandler<JoinEvent> joinEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEventEventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEventEventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Multiple Event")
    void testSubscribeMultipleEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent2EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Different Event")
    void testSubscribeDifferentEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                     @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                                     @Mock EventHandler<QuitEvent> quitEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);
        Events.subscribe(QuitEvent.class, quitEventEventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());
        verify(quitEventEventHandler, never()).execute(any());
    }

    @Test
    @DisplayName("Test Register Same Event")
    void testRegisterSameEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        // Check if it has been called one time only
        verify(joinEvent1EventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Unregistered Event")
    void testSubscribeUnregisteredEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                        @Mock EventHandler<JoinEvent> joinEvent2EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, never()).execute(any());
    }

    /**
     * Default priority is {@link Priority#NORMAL}<br />
     * If multiple event exist with same priority, event should be called an a "FIFO" queue
     */
    @Test
    @DisplayName("Test Subscribe Default Priority Event")
    void testSubscribeDefaultPriorityEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                           @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                                           @Mock EventHandler<JoinEvent> joinEvent3EventHandler,
                                           @Mock EventHandler<JoinEvent> joinEvent4EventHandler,
                                           @Mock EventHandler<JoinEvent> joinEvent5EventHandler) {
        // Order: 1 - 3 - 5 - 4 - 2
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent3EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent5EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent4EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);

        InOrder inOrder = inOrder(joinEvent1EventHandler,
                joinEvent2EventHandler,
                joinEvent3EventHandler,
                joinEvent4EventHandler,
                joinEvent5EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        inOrder.verify(joinEvent1EventHandler).execute(any());
        inOrder.verify(joinEvent3EventHandler).execute(any());
        inOrder.verify(joinEvent5EventHandler).execute(any());
        inOrder.verify(joinEvent4EventHandler).execute(any());
        inOrder.verify(joinEvent2EventHandler).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Other Priority Event")
    void testSubscribePriorityEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent3EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent4EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent5EventHandler) {
        // Order: 1 - 3 - 5 - 4 - 2
        Events.subscribe(JoinEvent.class, Priority.LOWEST, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, Priority.HIGHER, joinEvent2EventHandler);
        Events.subscribe(JoinEvent.class, Priority.LOWER, joinEvent3EventHandler);
        Events.subscribe(JoinEvent.class, Priority.HIGH, joinEvent4EventHandler);
        Events.subscribe(JoinEvent.class, Priority.NORMAL, joinEvent5EventHandler);

        InOrder inOrder = inOrder(joinEvent1EventHandler,
                joinEvent2EventHandler,
                joinEvent3EventHandler,
                joinEvent4EventHandler,
                joinEvent5EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        inOrder.verify(joinEvent1EventHandler).execute(any());
        inOrder.verify(joinEvent3EventHandler).execute(any());
        inOrder.verify(joinEvent5EventHandler).execute(any());
        inOrder.verify(joinEvent4EventHandler).execute(any());
        inOrder.verify(joinEvent2EventHandler).execute(any());
    }

    @Test
    @DisplayName("Test Unsubscribe Event")
    void testUnsubscribeEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                              @Mock EventHandler<JoinEvent> joinEvent2EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());

        // Unsubscribe the event
        Events.unsubscribe(JoinEvent.class, joinEvent2EventHandler);
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(2)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Unsubscribe Class Event")
    void testUnsubscribeClassEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                   @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                                   @Mock EventHandler<QuitEvent> quitEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);
        Events.subscribe(QuitEvent.class, quitEventEventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());

        // Unsubscribe the event
        Events.unsubscribe(JoinEvent.class);
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, times(1)).execute(any());

        // Test QuitEvent
        QuitEvent quitEvent = new QuitEvent();
        Events.publish(quitEvent);

        verify(quitEventEventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Get HandlerList")
    void testGetHandlerList(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                            @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                            @Mock EventHandler<QuitEvent> quitEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);
        Events.subscribe(QuitEvent.class, quitEventEventHandler);
        assertNotNull(Events.getHandlerList(JoinEvent.class));
        assertNotNull(Events.getHandlerList(QuitEvent.class));
    }
}
