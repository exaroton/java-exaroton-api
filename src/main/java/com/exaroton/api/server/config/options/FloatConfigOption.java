package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A floating point number option
 */
public final class FloatConfigOption extends BaseNumberOption<Double> {
    @ApiStatus.Internal
    public FloatConfigOption(String key, Double value, String label) {
        this(key, value, label, null, null, null);
    }

    @ApiStatus.Internal
    public FloatConfigOption(
            String key,
            Double value,
            String label,
            @Nullable Double min,
            @Nullable Double max,
            @Nullable Double step
    ) {
        super(key, value, label, OptionType.FLOAT, min, max, step);
    }
}
