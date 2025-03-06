package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

/**
 * A floating point number option
 */
public final class FloatConfigOption extends ConfigOption<Double> {
    @ApiStatus.Internal
    public FloatConfigOption(String key, Double value, String label) {
        super(key, value, label, OptionType.FLOAT);
    }
}
