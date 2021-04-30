package me.oddlyoko.ejws.event;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import me.oddlyoko.ejws.event.src.JoinEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestHandlerList {

    @Test
    @DisplayName("Test HandlerList.subscribe()")
    void testSubscribe(@Mock EventHandler<JoinEvent> eventHandler) {
        HandlerList<JoinEvent> handlerList = new HandlerList<>(null);
        handlerList.subscribe(Priority.LOWEST, eventHandler);
        // Test Publish
        JoinEvent e = new JoinEvent();
        handlerList.publish(e);
        verify(eventHandler, times(1)).execute(e);
    }

    @Test
    @DisplayName("Test Multiple subscribe()")
    void testMultipleSubscribe(@Mock EventHandler<JoinEvent> eventHandler,
                               @Mock EventHandler<JoinEvent> eventHandler1,
                               @Mock EventHandler<JoinEvent> eventHandler2,
                               @Mock EventHandler<JoinEvent> eventHandler3,
                               @Mock EventHandler<JoinEvent> eventHandler4,
                               @Mock EventHandler<JoinEvent> eventHandler5,
                               @Mock EventHandler<JoinEvent> eventHandler6,
                               @Mock EventHandler<JoinEvent> eventHandler7,
                               @Mock EventHandler<JoinEvent> eventHandler8,
                               @Mock EventHandler<JoinEvent> eventHandler9) {
        HandlerList<JoinEvent> handlerList = new HandlerList<>(null);
        handlerList.subscribe(Priority.LOWEST, eventHandler);
        handlerList.subscribe(Priority.LOW, eventHandler1);
        handlerList.subscribe(Priority.NORMAL, eventHandler2);
        handlerList.subscribe(Priority.HIGH, eventHandler3);
        handlerList.subscribe(Priority.HIGHEST, eventHandler4);
        handlerList.subscribe(Priority.LOWEST, eventHandler5);
        handlerList.subscribe(Priority.LOW, eventHandler6);
        handlerList.subscribe(Priority.NORMAL, eventHandler7);
        handlerList.subscribe(Priority.HIGH, eventHandler8);
        handlerList.subscribe(Priority.HIGHEST, eventHandler9);
        InOrder inOrder = inOrder(eventHandler,
                eventHandler1,
                eventHandler2,
                eventHandler3,
                eventHandler4,
                eventHandler5,
                eventHandler6,
                eventHandler7,
                eventHandler8,
                eventHandler9);
        JoinEvent e = new JoinEvent();
        handlerList.publish(e);
        inOrder.verify(eventHandler).execute(e);
        inOrder.verify(eventHandler5).execute(e);
        inOrder.verify(eventHandler1).execute(e);
        inOrder.verify(eventHandler6).execute(e);
        inOrder.verify(eventHandler2).execute(e);
        inOrder.verify(eventHandler7).execute(e);
        inOrder.verify(eventHandler3).execute(e);
        inOrder.verify(eventHandler8).execute(e);
        inOrder.verify(eventHandler4).execute(e);
        inOrder.verify(eventHandler9).execute(e);
        inOrder.verifyNoMoreInteractions();
    }
}
