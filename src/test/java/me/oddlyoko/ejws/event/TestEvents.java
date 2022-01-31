package me.oddlyoko.ejws.event;

import me.oddlyoko.ejws.event.src.JoinEvent;
import me.oddlyoko.ejws.event.src.QuitEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestEvents {

    @BeforeAll
    public static void beforeAll() {
        Events.registerEventModule(JoinEvent.class, null);
        Events.registerEventModule(QuitEvent.class, null);
    }

    @AfterAll
    public static void afterAll() {
        Events.unregisterEventModule(JoinEvent.class);
        Events.unregisterEventModule(QuitEvent.class);
    }

    @AfterEach
    public void afterEach() {
        Events.unsubscribe(JoinEvent.class);
        Events.unsubscribe(QuitEvent.class);
    }

    @Test
    @DisplayName("Test Subscribe Single Event")
    public void testSubscribeSingleEvent(@Mock EventHandler<JoinEvent> joinEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEventEventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEventEventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Multiple Event")
    public void testSubscribeMultipleEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
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
    public void testSubscribeDifferentEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
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
    public void testRegisterSameEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        // Check if it has been called one time only
        verify(joinEvent1EventHandler, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test Subscribe Unregistered Event")
    public void testSubscribeUnregisteredEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                        @Mock EventHandler<JoinEvent> joinEvent2EventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);

        JoinEvent joinEvent = new JoinEvent();
        Events.publish(joinEvent);

        verify(joinEvent1EventHandler, times(1)).execute(any());
        verify(joinEvent2EventHandler, never()).execute(any());
    }

    /**
     * Default priority is {@link Events#NORMAL}<br />
     * If multiple event exist with same priority, event should be called an a "FIFO" queue
     */
    @Test
    @DisplayName("Test Subscribe Default Priority Event")
    public void testSubscribeDefaultPriorityEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
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
    public void testSubscribePriorityEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent3EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent4EventHandler,
                                    @Mock EventHandler<JoinEvent> joinEvent5EventHandler) {
        // Order: 1 - 3 - 5 - 4 - 2
        Events.subscribe(JoinEvent.class, Events.LOWEST, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, Events.HIGHEST, joinEvent2EventHandler);
        Events.subscribe(JoinEvent.class, Events.LOW, joinEvent3EventHandler);
        Events.subscribe(JoinEvent.class, Events.HIGH, joinEvent4EventHandler);
        Events.subscribe(JoinEvent.class, Events.NORMAL, joinEvent5EventHandler);

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
    public void testUnsubscribeEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
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
    public void testUnsubscribeClassEvent(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
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
    public void testGetHandlerList(@Mock EventHandler<JoinEvent> joinEvent1EventHandler,
                            @Mock EventHandler<JoinEvent> joinEvent2EventHandler,
                            @Mock EventHandler<QuitEvent> quitEventEventHandler) {
        Events.subscribe(JoinEvent.class, joinEvent1EventHandler);
        Events.subscribe(JoinEvent.class, joinEvent2EventHandler);
        Events.subscribe(QuitEvent.class, quitEventEventHandler);
        assertNotNull(Events.getHandlerList(JoinEvent.class));
        assertNotNull(Events.getHandlerList(QuitEvent.class));
    }
}
