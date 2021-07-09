package me.oddlyoko.ejws.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestPriority {

    @Test
    @DisplayName("Test getPriority()")
    public void testGetPriority() {
        assertEquals(1, Priority.LOWEST.getPriority());
        assertEquals(2, Priority.LOW.getPriority());
        assertEquals(3, Priority.NORMAL.getPriority());
        assertEquals(4, Priority.HIGH.getPriority());
        assertEquals(5, Priority.HIGHEST.getPriority());
    }
}
