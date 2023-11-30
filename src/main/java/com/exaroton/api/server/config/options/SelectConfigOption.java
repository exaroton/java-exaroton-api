package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.Nullable;

public class SelectConfigOption extends ConfigOption {
    private String value;

    public SelectConfigOption(String key, String value, String label, String[] options) {
        super(key, label, OptionType.SELECT, options);
        this.value = value;
    }

    public SelectConfigOption setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public @Nullable String getValue() {
        return value;
    }
}
