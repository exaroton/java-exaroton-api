package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;

/**
 * An integer option
 */
public class IntegerConfigOption extends ConfigOption<Long> {
    public IntegerConfigOption(String key, Long value, String label) {
        super(key, value, label, OptionType.INTEGER);
    }
}
