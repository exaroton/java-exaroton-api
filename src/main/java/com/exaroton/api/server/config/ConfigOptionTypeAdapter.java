package com.exaroton.api.server.config;

import com.exaroton.api.server.config.options.BooleanConfigOption;
import com.exaroton.api.server.config.options.FloatConfigOption;
import com.exaroton.api.server.config.options.IntegerConfigOption;
import com.exaroton.api.server.config.options.MultiselectConfigOption;
import com.exaroton.api.server.config.options.SelectConfigOption;
import com.exaroton.api.server.config.options.StringConfigOption;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigOptionTypeAdapter extends TypeAdapter<ConfigOption> {
    @Override
    public void write(JsonWriter out, ConfigOption value) throws IOException {
        out.beginObject();
        out.name("key").value(value.getKey());
        out.name("label").value(value.getLabel());
        out.name("type").value(value.getType().name().toLowerCase());
        out.name("options").beginArray();
        for (String option : value.getOptions()) {
            out.value(option);
        }
        out.endArray();
        out.name("value");
        switch (value.getType()) {
            case STRING:
            case SELECT:
                out.value((String) value.getValue());
                break;
            case INTEGER:
                out.value((Long) value.getValue());
                break;
            case FLOAT:
                out.value((Double) value.getValue());
                break;
            case BOOLEAN:
                out.value((Boolean) value.getValue());
                break;
            case MULTISELECT:
                Object optionValue = value.getValue();
                if (optionValue == null) {
                    out.nullValue();
                    break;
                }

                out.beginArray();
                for (String option : (String[]) optionValue) {
                    out.value(option);
                }
                out.endArray();
                break;
        }
    }

    @Override
    public ConfigOption read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        String key = null;
        String label = null;
        OptionType type = null;
        Object value = null;
        String[] options = null;
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
                            List<String> list = new ArrayList<>();
                            in.beginArray();
                            while (in.hasNext()) {
                                list.add(in.nextString());
                            }
                            in.endArray();
                            value = list.toArray(new String[0]);
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
                    List<String> list = new ArrayList<>();
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        break;
                    }
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(in.nextString());
                    }
                    in.endArray();
                    options = list.toArray(new String[0]);
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
                    return new StringConfigOption(key, (String) value, label, options);
                }
                throw new MalformedJsonException("Expected string getValue at " + in.getPath());

            case INTEGER:
                if (value == null || value instanceof Double) {
                    Double doubleValue = (Double) value;
                    Long longValue = doubleValue == null ? null : doubleValue.longValue();
                    return new IntegerConfigOption(key, longValue, label, options);
                }
                throw new MalformedJsonException("Expected integer getValue at " + in.getPath());

            case FLOAT:
                if (value == null || value instanceof Double) {
                    return new FloatConfigOption(key, (Double) value, label, options);
                }
                throw new MalformedJsonException("Expected float getValue at " + in.getPath());

            case BOOLEAN:
                if (value == null || value instanceof Boolean) {
                    return new BooleanConfigOption(key, (Boolean) value, label, options);
                }
                throw new MalformedJsonException("Expected boolean getValue at " + in.getPath());

            case MULTISELECT:
                if (value == null || value instanceof String[]) {
                    return new MultiselectConfigOption(key, (String[]) value, label, options);
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
