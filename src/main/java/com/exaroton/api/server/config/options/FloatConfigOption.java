package com.exaroton.api.server.config.options;

import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.OptionType;

/**
 * A floating point number option
 */
public class FloatConfigOption extends ConfigOption<Double> {
    public FloatConfigOption(String key, Double value, String label) {
        super(key, value, label, OptionType.FLOAT);
    }
}
