package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

/**
 * A string option
 */
public final class StringConfigOption extends ConfigOption<String> {
    @ApiStatus.Internal
    public StringConfigOption(String key, String value, String label) {
        super(key, value, label, OptionType.STRING);
    }
}
