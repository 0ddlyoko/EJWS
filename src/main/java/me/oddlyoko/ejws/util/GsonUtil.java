package me.oddlyoko.ejws.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class GsonUtil {
    private static final JsonSerializer<Version> versionSerializer;
    private static final JsonDeserializer<Version> versionDeserializer;

    static {
        versionSerializer = (version, type, jsonSerializationContext) -> new JsonPrimitive(version.toString());
        versionDeserializer = (json, typeOfT, context) -> Version.of(json.getAsString());
    }

    public static JsonSerializer<Version> getVersionSerializer() {
        return versionSerializer;
    }

    public static JsonDeserializer<Version> getVersionDeserializer() {
        return versionDeserializer;
    }
}
