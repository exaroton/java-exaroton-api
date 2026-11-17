package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Base class for integer and float options
 * @param <T> value type (Long or Double)
 */
@ApiStatus.NonExtendable
@ApiStatus.AvailableSince("2.5.0")
public class BaseNumberOption<T extends Number> extends ConfigOption<T> {
    /**
     * Minimum allowed value
     */
    protected final @Nullable T min;

    /**
     * Maximum allowed value
     */
    protected final @Nullable T max;

    /**
     * Suggested increment
     */
    protected final @Nullable T step;

    /**
     * Create a new number config option
     * @param key key of the option
     * @param value value of the option
     * @param label label of the option
     * @param type type of the option
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @param step suggested increment
     */
    protected BaseNumberOption(
            String key,
            T value,
            String label,
            OptionType type,
            @Nullable T min,
            @Nullable T max,
            @Nullable T step
    ) {
        super(key, value, label, type);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    /**
     * Get the minimum allowed value
     * @return minimum value or empty if there is no minimum
     */
    public Optional<T> getMin() {
        return Optional.ofNullable(min);
    }

    /**
     * Get the maximum allowed value
     * @return maximum value or empty if there is no maximum
     */
    public Optional<T> getMax() {
        return Optional.ofNullable(max);
    }

    /**
     * Get the suggested increment for UI controls. This is not enforced by the API.
     * @return step or empty if there is no suggested step
     */
    public Optional<T> getStep() {
        return Optional.ofNullable(step);
    }
}
