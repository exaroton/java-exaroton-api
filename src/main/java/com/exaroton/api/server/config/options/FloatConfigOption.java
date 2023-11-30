package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.Nullable;

public class FloatConfigOption extends ConfigOption {
    private Double value;

    public FloatConfigOption(String key, Double value, String label,String[] options) {
        super(key, label, OptionType.FLOAT, options);
        this.value = value;
    }

    public FloatConfigOption setValue(Double value) {
        this.value = value;
        return this;
    }

    @Override
    public @Nullable Double getValue() {
        return value;
    }
}
