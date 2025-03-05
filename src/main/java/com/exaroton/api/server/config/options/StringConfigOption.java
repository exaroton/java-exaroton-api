package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;

/**
 * A string option
 */
public class StringConfigOption extends ConfigOption<String> {
    public StringConfigOption(String key, String value, String label) {
        super(key, value, label, OptionType.STRING);
    }
}
