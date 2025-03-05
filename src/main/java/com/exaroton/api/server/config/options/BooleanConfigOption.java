package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;

/**
 * A boolean option
 */
public class BooleanConfigOption extends ConfigOption<Boolean> {
    public BooleanConfigOption(String key, Boolean value, String label) {
        super(key, value, label, OptionType.BOOLEAN);
    }
}
