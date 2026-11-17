package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * An integer option
 */
public final class IntegerConfigOption extends BaseNumberOption<Long> {
    @ApiStatus.Internal
    public IntegerConfigOption(String key, Long value, String label) {
        this(key, value, label, null, null, null);
    }

    @ApiStatus.Internal
    public IntegerConfigOption(
            String key,
            Long value,
            String label,
            @Nullable Long min,
            @Nullable Long max,
            @Nullable Long step
    ) {
        super(key, value, label, OptionType.INTEGER, min, max, step);
    }
}
