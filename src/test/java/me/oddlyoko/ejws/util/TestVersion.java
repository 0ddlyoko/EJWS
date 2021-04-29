package me.oddlyoko.ejws.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestVersion {

    @Test
    @DisplayName("Test of(int[])")
    void testOf() {
        assertArrayEquals(new int[] { 1, 0, 0, 0}, Version.of(new int[] { 1 }).getVersion());
        assertArrayEquals(new int[] { 1, 2, 0, 0 }, Version.of(new int[] { 1, 2 }).getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 0 }, Version.of(new int[] { 1, 2, 3 }).getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, Version.of(new int[] { 1, 2, 3, 4 }).getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, Version.of(new int[] { 1, 2, 3, 4, 5 }).getVersion());
    }

    @Test
    @DisplayName("Test of(String)")
    void testOfString() {
        assertArrayEquals(new int[] { 1, 0, 0, 0 }, Version.of("1").getVersion());
        assertArrayEquals(new int[] { 1, 2, 0, 0 }, Version.of("1.2").getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 0 }, Version.of("1.2.3").getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, Version.of("1.2.3.4").getVersion());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, Version.of("1.2.3.4.5").getVersion());
        assertArrayEquals(new int[] { 100, 24, 32, 42 }, Version.of("100.24.32.42.5.6.7.8.9.10").getVersion());
    }

    @Test
    @DisplayName("Test of(String) Empty version")
    void testOfNull() {
        assertThrows(NullPointerException.class, () -> Version.of((String) null));
        assertThrows(IllegalArgumentException.class, () -> Version.of(""));
        assertThrows(IllegalArgumentException.class, () -> Version.of("\n"));
    }

    @Test
    @DisplayName("Test of(String) Invalid String")
    void testOfInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> Version.of("a"));
        assertThrows(IllegalArgumentException.class, () -> Version.of("test"));
        assertThrows(IllegalArgumentException.class, () -> Version.of("a.b"));
        assertThrows(IllegalArgumentException.class, () -> Version.of("1.2.c"));
        assertThrows(IllegalArgumentException.class, () -> Version.of("1.2.3.4.5.6.7.8.9.10.e"));
    }

    @Test
    @DisplayName("Test get(int)")
    void testGet() {
        Version v = Version.of("10.23.334.42");
        assertEquals(10, v.get(0));
        assertEquals(23, v.get(1));
        assertEquals(334, v.get(2));
        assertEquals(42, v.get(3));
    }

    @Test
    @DisplayName("Test getVersion() returns a cloned version")
    void testGetVersionClone() {
        Version v = Version.of("10.23.334.42");
        assertArrayEquals(new int[] { 10, 23, 334, 42 }, v.getVersion());
        v.getVersion()[0] = 56;
        assertArrayEquals(new int[] { 10, 23, 334, 42 }, v.getVersion());
    }

    @Test
    @DisplayName("Test compareTo(Version)")
    void testCompareTo() {
        // Test same
        assertEquals(0, Version.of("1.2.3.4").compareTo(Version.of("1.2.3.4")));
        assertEquals(0, Version.of("10.20.30.40").compareTo(Version.of("10.20.30.40")));
        assertEquals(0, Version.of("1.2.3.4").compareTo(Version.of("1.2.3.4.5.6.7.8.9.10")));
        assertEquals(0, Version.of("1.0.0.0").compareTo(Version.of("1")));
        // Test Different
        assertEquals(1, Version.of("1.2.3.4").compareTo(Version.of("0.2.3.4")));
        assertEquals(-1, Version.of("1.2.3.4").compareTo(Version.of("2.2.3.4")));
        assertEquals(1, Version.of("1.2.3.4").compareTo(Version.of("1.1.3.4")));
        assertEquals(-1, Version.of("1.2.3.4").compareTo(Version.of("1.3.3.4")));
        assertEquals(1, Version.of("1.2.3.4").compareTo(Version.of("1.2.2.4")));
        assertEquals(-1, Version.of("1.2.3.4").compareTo(Version.of("1.2.4.4")));
        assertEquals(1, Version.of("1.2.3.4").compareTo(Version.of("1.2.3.3")));
        assertEquals(-1, Version.of("1.2.3.4").compareTo(Version.of("1.2.3.5")));

        assertEquals(1, Version.of("1.2.3.4").compareTo(Version.of("1")));
        assertEquals(-1, Version.of("1.2.3.4").compareTo(Version.of("1.3")));
    }

    @Test
    @DisplayName("Test equals")
    void testEquals() {
        Version version = Version.V1_0;
        assertEquals(version, version);
        assertNotEquals(version, null);
        assertNotEquals(version, "1");
        assertNotEquals(version, Version.of("1.0.0.1"));
    }

    @Test
    @DisplayName("Test HashCode")
    void testHashCode() {
        Version version = Version.V1_0;
        assertEquals(version.hashCode(), version.hashCode());
    }

    @Test
    @DisplayName("Test clone()")
    void testClone() throws CloneNotSupportedException {
        Version version = Version.V1_0;
        assertEquals(version.clone(), version);
    }

    @Test
    @DisplayName("Test toString()")
    void testToString() {
        Version version = Version.V1_0;
        assertEquals("1.0.0.0", version.toString());

        Version version1 = Version.of("1.2.3.4");
        assertEquals("1.2.3.4", version1.toString());
    }
}
