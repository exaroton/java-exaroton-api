package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.OptionType;

import java.util.Set;

/**
 * A select option
 */
public class SelectConfigOption extends BaseSelectOption<String> {

    public SelectConfigOption(String key, String value, String label, Set<String> options) {
        super(key, value, label, OptionType.SELECT, options);
    }
}
