package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.Nullable;

public class MultiselectConfigOption extends ConfigOption {
    private String[] value;

    public MultiselectConfigOption(String key, String[] value, String label, String[] options) {
        super(key, label, OptionType.MULTISELECT, options);
        this.value = value;
    }

    public MultiselectConfigOption setValue(String... value) {
        this.value = value;
        return this;
    }

    @Override
    public @Nullable String[] getValue() {
        return value;
    }
}
