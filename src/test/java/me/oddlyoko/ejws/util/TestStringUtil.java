package me.oddlyoko.ejws.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestStringUtil {

    @Test
    @DisplayName("Test IsBlank")
    void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        // Space
        assertTrue(StringUtil.isBlank(" "));
        // Tab
        assertTrue(StringUtil.isBlank("\t"));
        // Enter
        assertTrue(StringUtil.isBlank("\n"));
        // All
        assertTrue(StringUtil.isBlank("\n\n\n\n\t\t  \n\t"));
        assertFalse(StringUtil.isBlank("e"));
        assertFalse(StringUtil.isBlank(" e"));
        assertFalse(StringUtil.isBlank(" e "));
        assertFalse(StringUtil.isBlank("\n\n\n\ne"));
        assertFalse(StringUtil.isBlank("\n\n\n\n\t\t  \n\te"));
    }
}
