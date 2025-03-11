package com.exaroton.api.util;

import com.exaroton.api.server.config.ConfigOption;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class ConfigOptionTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (ConfigOption.class.isAssignableFrom(type.getRawType())) {
            //noinspection unchecked
            return (TypeAdapter<T>) new ConfigOptionTypeAdapter();
        }
        return null;
    }
}
