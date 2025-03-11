package com.exaroton.api.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class VoidTypeAdapter extends TypeAdapter<Void> {
    @Override
    public void write(JsonWriter out, Void value) throws IOException {
        out.nullValue();
    }

    @Override
    public Void read(JsonReader in) throws IOException {
        in.skipValue();
        return null;
    }
}
