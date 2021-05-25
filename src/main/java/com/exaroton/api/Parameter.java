package com.exaroton.api;

/**
 * HTTP Parameter
 */
public class Parameter {
    /**
     * parameter name
     */
    private final String name;

    /**
     * parameter value
     */
    private final String value;

    /**
     * @param name parameter name
     * @param value parameter value
     */
    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * @return parameter value
     */
    public String getValue() {
        return value;
    }
}
