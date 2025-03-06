package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

/**
 * An integer option
 */
public final class IntegerConfigOption extends ConfigOption<Long> {
    @ApiStatus.Internal
    public IntegerConfigOption(String key, Long value, String label) {
        super(key, value, label, OptionType.INTEGER);
    }
}
