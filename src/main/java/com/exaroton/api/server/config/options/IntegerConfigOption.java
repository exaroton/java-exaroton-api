package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.Nullable;

public class IntegerConfigOption extends ConfigOption {
    private final Long value;

    public IntegerConfigOption(String key, Long value, String label,String[] options) {
        super(key, label, OptionType.INTEGER, options);
        this.value = value;
    }

    public IntegerConfigOption setValue(Long value) {
        return new IntegerConfigOption(this.getKey(), value, this.getLabel(), this.getOptions());
    }

    @Override
    public @Nullable Long getValue() {
        return value;
    }
}
