package com.exaroton.api.util;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import com.exaroton.api.server.config.options.*;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigOptionTypeAdapter extends TypeAdapter<ConfigOption<?>> {
    @Override
    public void write(JsonWriter out, ConfigOption<?> value) throws IOException {
        out.beginObject();
        out.name("key").value(value.getKey());
        out.name("label").value(value.getLabel());
        out.name("type").value(value.getType().name().toLowerCase());
        if (value instanceof BaseSelectOption<?>) {
            BaseSelectOption<?> select = (BaseSelectOption<?>) value;
            out.name("options").beginArray();
            for (String option : select.getOptions()) {
                out.value(option);
            }
            out.endArray();
        }
        out.name("value");
        if (value instanceof StringConfigOption) {
            StringConfigOption option = (StringConfigOption) value;
            out.value(option.getValue());
        } else if(value instanceof SelectConfigOption) {
            SelectConfigOption option = (SelectConfigOption) value;
            out.value(option.getValue());
        } else if (value instanceof IntegerConfigOption) {
            IntegerConfigOption option = (IntegerConfigOption) value;
            out.value(option.getValue());
        } else if (value instanceof FloatConfigOption) {
            FloatConfigOption option = (FloatConfigOption) value;
            out.value(option.getValue());
        } else if (value instanceof BooleanConfigOption) {
            BooleanConfigOption option = (BooleanConfigOption) value;
            out.value(option.getValue());
        } else if (value instanceof MultiselectConfigOption) {
            MultiselectConfigOption option = (MultiselectConfigOption) value;
            out.beginArray();
            for (String optionValue : option.getValue()) {
                out.value(optionValue);
            }
            out.endArray();
        } else {
            throw new IllegalArgumentException("Unexpected option type " + value.getClass());
        }
        out.endObject();
    }

    @Override
    public ConfigOption<?> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        String key = null;
        String label = null;
        OptionType type = null;
        Object value = null;
        Set<String> options = new HashSet<>();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "key":
                    key = in.nextString();
                    break;
                case "label":
                    label = in.nextString();
                    break;
                case "type":
                    type = OptionType.valueOf(in.nextString().toUpperCase());
                    break;
                case "value":
                    switch (in.peek()) {
                        case BEGIN_ARRAY:
                            Set<String> collection = new HashSet<>();
                            in.beginArray();
                            while (in.hasNext()) {
                                collection.add(in.nextString());
                            }
                            in.endArray();
                            value = collection.toArray(new String[0]);
                            break;
                        case STRING:
                            value = in.nextString();
                            break;
                        case NUMBER:
                            value = in.nextDouble();
                            break;
                        case BOOLEAN:
                            value = in.nextBoolean();
                            break;
                        case NULL:
                            in.nextNull();
                            value = null;
                            break;
                        default:
                            throw new MalformedJsonException("Unexpected token " + in.peek() + " at " + in.getPath());
                    }
                    break;
                case "options":
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        break;
                    }
                    in.beginArray();
                    while (in.hasNext()) {
                        options.add(in.nextString());
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();

        if (type == null) {
            throw new MalformedJsonException("Missing getType at " + in.getPath());
        }

        switch (type) {
            case STRING:
                if (value == null || value instanceof String) {
                    return new StringConfigOption(key, (String) value, label);
                }
                throw new MalformedJsonException("Expected string getValue at " + in.getPath());

            case INTEGER:
                if (value == null || value instanceof Double) {
                    Double doubleValue = (Double) value;
                    Long longValue = doubleValue == null ? null : doubleValue.longValue();
                    return new IntegerConfigOption(key, longValue, label);
                }
                throw new MalformedJsonException("Expected integer getValue at " + in.getPath());

            case FLOAT:
                if (value == null || value instanceof Double) {
                    return new FloatConfigOption(key, (Double) value, label);
                }
                throw new MalformedJsonException("Expected float getValue at " + in.getPath());

            case BOOLEAN:
                if (value == null || value instanceof Boolean) {
                    return new BooleanConfigOption(key, (Boolean) value, label);
                }
                throw new MalformedJsonException("Expected boolean getValue at " + in.getPath());

            case MULTISELECT:
                if (value == null || value instanceof String[]) {
                    Set<String> set = value == null ? new HashSet<>() : new HashSet<>(Arrays.asList((String[]) value));
                    return new MultiselectConfigOption(key, set, label, options);
                }
                throw new MalformedJsonException("Expected string array getValue at " + in.getPath());

            case SELECT:
                if (value == null || value instanceof String) {
                    return new SelectConfigOption(key, (String) value, label, options);
                }
                throw new MalformedJsonException("Expected string getValue at " + in.getPath());

            default:
                throw new MalformedJsonException("Unexpected getType " + type + " at " + in.getPath());
        }
    }
}
