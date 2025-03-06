package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * A select option where multiple options can be selected
 */
public final class MultiselectConfigOption extends BaseSelectOption<Set<String>> {
    /**
     * Create a new config option
     * @param key key of the option
     * @param value value of the option
     * @param label label of the option
     * @param options list of available options
     */
    @ApiStatus.Internal
    public MultiselectConfigOption(String key, Set<String> value, String label, Set<String> options) {
        super(key, new HashSet<>(value), label, OptionType.MULTISELECT, options);
    }

    /**
     * Add an option to the value
     * @param option option to add
     * @return this
     */
    public MultiselectConfigOption add(String option) {
        if (!this.options.contains(option)) {
            throw new IllegalArgumentException("Invalid value " + option + ". Available options: " + String.join(", ", this.options));
        }

        this.value.add(option);
        return this;
    }

    /**
     * Remove an option from the value
     * @param option option to remove
     * @return this
     */
    public MultiselectConfigOption remove(String option) {
        this.value.remove(option);
        return this;
    }
}
