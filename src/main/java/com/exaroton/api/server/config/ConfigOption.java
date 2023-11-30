package com.exaroton.api.server.config;

import org.jetbrains.annotations.Nullable;

public abstract class ConfigOption {
    private final String key;
    private final String label;
    private final OptionType type;
    private final String[] options;

    protected ConfigOption(String key, String label, OptionType type, String[] options) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.options = options;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public @Nullable String[] getOptions() {
        return options;
    }

    abstract public @Nullable Object getValue();

    public OptionType getType() {
        return type;
    }
}
