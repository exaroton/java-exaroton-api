package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;

import java.util.Set;

/**
 * Base class for select and multi-select options
 * @param <T> value type (String or Set<String>)
 */
public class BaseSelectOption<T> extends ConfigOption<T> {
    /**
     * List of all available options
     */
    protected Set<String> options;

    /**
     * Create a new config option with a list of available options
     * @param key key of the option
     * @param value value of the option
     * @param label label of the option
     * @param type type of the option
     * @param options list of available options
     */
    protected BaseSelectOption(String key, T value, String label, OptionType type, Set<String> options) {
        super(key, value, label, type);
        this.options = options;
    }

    /**
     * Get a list of all available options
     * @return set of available options
     */
    public Set<String> getOptions() {
        return options;
    }
}
