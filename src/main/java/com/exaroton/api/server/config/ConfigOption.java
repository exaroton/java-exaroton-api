package com.exaroton.api.server.config;

import org.jetbrains.annotations.ApiStatus;

/**
 * An option from a config file
 * @param <T> type of the value
 */
@ApiStatus.NonExtendable
public abstract class ConfigOption<T> {
    /**
     * key of the option
     */
    protected final String key;
    /**
     * value of the option
     */
    protected T value;
    protected final String label;
    protected final OptionType type;

    /**
     * Create a new config option
     * @param key key of the option
     * @param value value of the option
     * @param label label of the option
     * @param type type of the option
     */
    protected ConfigOption(String key, T value, String label, OptionType type) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.type = type;
    }

    /**
     * Get the key of the option
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the value of the option
     * @return value
     */
    public T getValue() {
        return value;
    }

    /**
     * Set the value of the option
     * @param value new value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Get the label of the option
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the type of the option
     * @return type
     */
    public OptionType getType() {
        return type;
    }
}
