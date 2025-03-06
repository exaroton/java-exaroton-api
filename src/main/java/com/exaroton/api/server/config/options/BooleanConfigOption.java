package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

/**
 * A boolean option
 */
public final class BooleanConfigOption extends ConfigOption<Boolean> {
    @ApiStatus.Internal
    public BooleanConfigOption(String key, Boolean value, String label) {
        super(key, value, label, OptionType.BOOLEAN);
    }
}
