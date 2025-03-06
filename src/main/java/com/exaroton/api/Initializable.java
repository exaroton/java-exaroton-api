package com.exaroton.api;

import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface for objects that can be initialized
 */
@ApiStatus.NonExtendable
public interface Initializable {
    /**
     * Initialize the object
     *
     * @param client exaroton client
     * @param gson   gson instance
     */
    void initialize(ExarotonClient client, Gson gson);
}
