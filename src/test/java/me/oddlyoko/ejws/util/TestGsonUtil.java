package me.oddlyoko.ejws.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestGsonUtil {

    @Test
    @DisplayName("Test getVersionSerializer")
    void testGetVersionSerializer() {
        assertNotNull(GsonUtil.getVersionSerializer());
        assertEquals("\"1.0.0.0\"",
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionSerializer()).create().toJson(Version.V1_0));
        assertEquals("\"1.2.3.4\"",
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionSerializer()).create().toJson(Version.of("1.2.3.4")));
        assertEquals("\"2.0.0.0\"",
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionSerializer()).create().toJson(Version.V2_0));
        assertEquals("\"5.4.3.2\"",
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionSerializer()).create().toJson(Version.of(new int[] { 5, 4, 3, 2 })));
    }

    @Test
    @DisplayName("Test getVersionDeserializer")
    void testGetVersionDeserializer() {
        assertNotNull(GsonUtil.getVersionDeserializer());
        assertEquals(Version.V1_0,
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create().fromJson("1.0", Version.class));
        assertEquals(Version.of("1.2.3.4"),
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create().fromJson("1.2.3.4", Version.class));
        assertEquals(Version.V2_0,
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create().fromJson("2.0.0", Version.class));
        assertEquals(Version.of("5.4.3.2"),
                new GsonBuilder().registerTypeAdapter(Version.class, GsonUtil.getVersionDeserializer()).create().fromJson("5.4.3.2", Version.class));
    }
}
