package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.OptionType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * A select option
 */
public final class SelectConfigOption extends BaseSelectOption<String> {
    @ApiStatus.Internal
    public SelectConfigOption(String key, String value, String label, Set<String> options) {
        super(key, value, label, OptionType.SELECT, options);
    }
}
