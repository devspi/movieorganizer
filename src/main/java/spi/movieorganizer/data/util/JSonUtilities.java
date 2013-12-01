package spi.movieorganizer.data.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JSonUtilities {

    private static final SimpleDateFormat sdf = new SimpleDateFormat();

    public static JsonElement stringToJsonPrimitive(final String value) {
        if (value == null)
            return JsonNull.INSTANCE;
        return new JsonPrimitive(value);
    }
    
    public static JsonElement charToJsonPrimitive(final Character value) {
        if (value == null)
            return JsonNull.INSTANCE;
        return new JsonPrimitive(value);
    }
    
    public static JsonElement numberToJsonPrimitive(final Number value) {
        if (value == null)
            return JsonNull.INSTANCE;
        return new JsonPrimitive(value);
    }
    
    public static JsonElement booleanToJsonPrimitive(final Boolean value) {
        if (value == null)
            return JsonNull.INSTANCE;
        return new JsonPrimitive(value);
    }

    public static String getValueAsString(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsString();
    }

    public static Date getValueAsDate(final String key, final JsonObject object, final String pattern) {
        if (object.get(key) == null || object.get(key).isJsonNull() || object.get(key).getAsString().isEmpty())
            return null;
        JSonUtilities.sdf.applyPattern(pattern);
        try {
            return JSonUtilities.sdf.parse(object.get(key).getAsString());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Double getValueAsDouble(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsDouble();
    }

    public static Integer getValueAsInteger(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsInt();
    }

    public static Boolean getValueAsBoolean(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsBoolean();
    }

    public static JsonArray getValueAsJsonArray(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsJsonArray();
    }

    public static Long getValuseAsLong(final String key, final JsonObject object) {
        if (object.get(key) == null || object.get(key).isJsonNull())
            return null;
        return object.get(key).getAsLong();
    }

}
