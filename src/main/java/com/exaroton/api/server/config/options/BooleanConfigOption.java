package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.Nullable;

public class BooleanConfigOption extends ConfigOption {
    private Boolean value;
    public BooleanConfigOption(String key, Boolean value, String label, String[] options) {
        super(key, label, OptionType.BOOLEAN, options);
        this.value = value;
    }

    public BooleanConfigOption setValue(Boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public @Nullable Boolean getValue() {
        return value;
    }
}
