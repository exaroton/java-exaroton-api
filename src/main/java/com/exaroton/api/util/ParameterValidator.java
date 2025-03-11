package com.exaroton.api.util;

import java.util.Collection;
import java.util.Map;

public class ParameterValidator {
    /**
     * Require that the argument is a valid (server/account/credit pool) id
     *
     * @param id id to validate
     * @return the id
     * @throws IllegalArgumentException if the id is invalid
     */
    public static String requireValidId(String id) {
        if (id == null || id.length() != 16) {
            throw new IllegalArgumentException("Invalid id '" + id + "'");
        }

        return id;
    }

    /**
     * Require that the argument is not null or empty
     *
     * @param string string to validate
     * @param name   name of the parameter
     * @return the string
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static String requireNonEmpty(String string, String name) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }

        return string;
    }

    /**
     * Require that the argument is not null or empty
     *
     * @param collection collection to validate
     * @param name       name of the parameter
     * @param <C>        collection type
     * @param <I>        item type
     * @return the string
     * @throws IllegalArgumentException if the array is null or empty
     */
    public static <C extends Collection<I>, I> C requireNonEmpty(C collection, String name) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }

        int i = 0;
        for (I item : collection) {
            if (item == null) {
                throw new IllegalArgumentException(name + " must not contain null values (index: " + i + ")");
            }
            i++;
        }

        return collection;
    }

    /**
     * Require that the argument is not null or empty
     *
     * @param map  map to validate
     * @param name name of the parameter
     * @param <M>  map type
     * @param <K>  key type
     * @param <V>  value type
     * @return the string
     * @throws IllegalArgumentException if the array is null or empty
     */
    public static <M extends Map<K, V>, K, V> M requireNonEmpty(M map, String name) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }

        for (Map.Entry<K, V> item : map.entrySet()) {
            if (item.getKey() == null) {
                throw new IllegalArgumentException(name + " must not contain null keys");
            }

            if (item.getValue() == null) {
                throw new IllegalArgumentException(name + " must not contain null values (key: " + item.getKey() + ")");
            }
        }

        return map;
    }

    /**
     * Require that a number is not null and positive (&gt; 0)
     *
     * @param number number to validate
     * @param name   name of the parameter
     * @param <T>    number type
     * @return the number
     * @throws IllegalArgumentException if the number is null, zero or negative
     */
    public static <T extends Number> T requirePositive(T number, String name) {
        if (number == null || number.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }

        return number;
    }
}
